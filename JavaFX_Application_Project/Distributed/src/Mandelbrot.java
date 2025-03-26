import mpi.*;

// -jar ../starter.jar -np 4 -> np stands for the number of processes that are runnable

// Comm class for communication context -> Send(void) & Recv(status)
// void Send(Object buf, int offset, int count, Datatype type, int dst, int tag)
// Status Recv(buf, offset, count, type, src, tag)
// BUF must be an array -> any kind of array
// OFFSET -> element in buf array where message starts
// COUNT -> number of items to send
// TYPE -> type of the items
 
// MPI SCATTER -> Designated root process that takes an array of elements and distributes the elements int the order of process rank
// The root process(process 0) will after MPI.Scatter contain just his part of the array. 

// MPI GATHER -> opposite of Scatter, takes elements from many processes and gathers them to on single process (root process)

import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

//import application.MSFrameParallel.LineTask;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.image.WritableImage;
//import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Mandelbrot {
	// INITIAL VALUES FOR MANDELBROT SET
	private static int HEIGHT = 600;
	private static int WIDTH = 800;
	
	
	private static int maxIterations = 50;
	static double zoomScale = 250.0;
	private static double RE_MIN = -2.1;
    private static double RE_MAX = 1.1;
    private static double IM_MIN = -1.2;
    private static double IM_MAX = 1.2;
    static Color black = Color.BLACK;
    private static double precision = Math.max((RE_MAX - RE_MIN) / WIDTH, (IM_MAX - IM_MIN) / HEIGHT);
	
	
    
    
    public static void main(String[] args) throws MPIException, IOException{
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		MPI.Init(args);
		
		int process_rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		
		int numLinesPerThread =  HEIGHT / size;
		
		int startSection = process_rank * (HEIGHT / size);
		int endSection = startSection + numLinesPerThread;
		
		//BUILD A SECTION OF THE MANDELBROT SET
		BufferedImage sectionImage = render(startSection, endSection, numLinesPerThread);
		
		if(process_rank == 0 ) {
			BufferedImage finalImage = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = finalImage.createGraphics();
			
			//Drawing its section
			graphics.drawImage(sectionImage, 0, 0, null);
			
			//Receive the sections from all other processes
			for(int i = 1; i < size; i++) { //ALL PROCESSES EXCEPT THE ROOT PROCESS
				byte[] data = new byte[WIDTH * numLinesPerThread * Integer.BYTES];
				MPI.COMM_WORLD.Recv(data, 0, data.length, MPI.BYTE, i, 0);
				//CHECK THISS!!!!
				BufferedImage receivedImage = toBufferedImage(data, WIDTH, numLinesPerThread);
                graphics.drawImage(receivedImage, 0, i * numLinesPerThread, null);
			}//for
			graphics.dispose(); //Release all the buffers!!!
			long endTime = System.currentTimeMillis();
	        long runtime = endTime - startTime;
	        System.out.println("Runtime: "+ runtime + " milliseconds" );
	        //Saving final image
	        ImageIO.write(finalImage, "png", new File("MandelbrotSet.png"));
		} else {
			
			byte[] sendData = toByteArray(sectionImage);
            MPI.COMM_WORLD.Send(sendData, 0, sendData.length, MPI.BYTE, 0, 0);
		}
		
		MPI.Finalize();
		
	}

	 static BufferedImage render(int start , int endSection ,int numLines){
        
        BufferedImage sectionImg = new BufferedImage(WIDTH, numLines, BufferedImage.TYPE_INT_ARGB);
        
        for(double c = RE_MIN, x=0; x < WIDTH; c = c + precision, x++){
            for(double ci = IM_MIN + (start * precision), y=start; y < endSection; ci = ci + precision, y++){
                renderPoint(c, ci, sectionImg, (int) y, (int) x, start);
            }
        }//forj
        
        
        return sectionImg;
        
    }//render
	
	 
	
	
	public static void renderPoint(double c, double ci, BufferedImage image, int yre, int xre, int start) {
        
        double convergence = checkConvergence(ci, c, maxIterations);
        double colorValue = (double) (convergence / maxIterations);
        
        
        Color rgb = Color.getHSBColor((float) (colorValue * 255), (float)1.0, (float)1.0);
        
        
        if (convergence != maxIterations) {
            
            image.setRGB(xre, yre - start, rgb.getRGB());
        } else {
            
        	image.setRGB(xre, yre - start, black.getRGB());
        }
        
    }//renderPoint
	
	public static int checkConvergence(double ci, double c, int convergenceSteps){
        double z = 0;
        double zi = 0;
        for (int i = 0; i < convergenceSteps; i++) {
            double ziT = 2 * (z * zi);
            double zT = z * z - (zi * zi);
            z = zT + c;
            zi = ziT + ci;

            if (z * z + zi * zi >= 4.0) {
                return i;
            }
        }
        return convergenceSteps;
    }//checkConvergence()
	
	public static byte[] toByteArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        DataBufferInt buffer = (DataBufferInt) image.getRaster().getDataBuffer();
        int[] intData = buffer.getData();
        byte[] byteData = new byte[width * height * 4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = intData[y * width + x];
                byteData[(y * width + x) * 4] = (byte) (pixel >> 24);      // Alpha
                byteData[(y * width + x) * 4 + 1] = (byte) (pixel >> 16);  // Red
                byteData[(y * width + x) * 4 + 2] = (byte) (pixel >> 8);   // Green
                byteData[(y * width + x) * 4 + 3] = (byte) pixel;          // Blue
            }
        }
        return byteData;
    }//toByteArray
	//Each pixel requires four bytes in the byte array to store its RGBA components
	
	static BufferedImage toBufferedImage(byte[] data, int width, int height){
        int[] intData = new int[data.length / 4];
        for (int i = 0; i < intData.length; i++){
            intData[i] = ((data[i * 4] & 0xFF) << 24) |  //Alpha
                    ((data[i * 4 + 1] & 0xFF) << 16) |  //Red
                    ((data[i * 4 + 2] & 0xFF) << 8) | 	//Green
                    (data[i * 4 + 3] & 0xFF);			// Blue
            // AND Logic operation to avoid sign extension.
            // << -> left-shifting to needed position
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, intData, 0, width);
        return image;
    }//ToBufferedImage
	
}//Mandelbrot
