package application;

import javafx.animation.PauseTransition;
import javafx.application.Application;

import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
import javafx.scene.input.KeyCode.*;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
//import javafx.scene.input.KeyCode.SUBTRACT;
//import javafx.scene.canvas.CanvasBuilder;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class MSFrameParallel extends Application{
    //size of canvas
    private static double HEIGHT = 600;
    private static double WIDTH = 800;
    // Left and right border
    private GraphicsContext context;
    
    // Top and Bottom border
    
    //Values for Mandelbrot set
    private static double RE_MIN = -2;
    private static double RE_MAX = 1;
    private static double IM_MIN = -1.2;
    private static double IM_MAX = 1.2;
    private static double precision = Math.max((RE_MAX - RE_MIN) / WIDTH, (IM_MAX - IM_MIN) / HEIGHT);
    private static int maxIterations = 50;
    private double zoomIncrement = 1.1;
    private double zoomFactor = 1;
    private double moveFactor = 0.3;
    private int cores = Runtime.getRuntime().availableProcessors();
    private int THREAD_POOL_SIZE = cores;
    PauseTransition resizePause = new PauseTransition(Duration.millis(200));

    WritableImage image = new WritableImage((int) WIDTH, (int) HEIGHT);
    PixelWriter pixelWriter = image.getPixelWriter();

    @FXML
    StackPane menu;
    @Override
    public void start(Stage primaryStage) throws Exception{
    	long startTime = System.currentTimeMillis();
       
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        
        context = canvas.getGraphicsContext2D();
        root.getChildren().addAll(canvas);
        Scene scene = new Scene(root);
        
        context.setFill(Color.YELLOW);
        primaryStage.setTitle("Mandelbrot set");
        

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case PLUS:
                        System.out.println("add");
                        zoomFactor *= zoomIncrement;
                        IM_MIN /= zoomIncrement;
                        IM_MAX /= zoomIncrement;
                        RE_MIN /= zoomIncrement;
                        RE_MAX /= zoomIncrement;
                        //drawMandelbrotSet();
                        render(context, RE_MIN, RE_MAX, IM_MIN, IM_MAX);
                        break;
                    case MINUS:
                        System.out.println("sub");
                        zoomFactor /= zoomIncrement;
                        IM_MIN *= zoomIncrement;
                        IM_MAX *= zoomIncrement;
                        RE_MIN *= zoomIncrement;
                        RE_MAX *= zoomIncrement;
                        //drawMandelbrotSet();
                        render(context, RE_MIN, RE_MAX, IM_MIN, IM_MAX);
                        break;
                    case LEFT:
                        System.out.println("left");
                        RE_MIN -= moveFactor;
                        RE_MAX -= moveFactor;
                        render(context, RE_MIN, RE_MAX, IM_MIN, IM_MAX);
                        break;
                    case RIGHT:
                        System.out.println("right");
                        RE_MIN += moveFactor;
                        RE_MAX += moveFactor;
                        render(context, RE_MIN, RE_MAX, IM_MIN, IM_MAX);
                        break;
                    case UP:
                        System.out.println("up");
                        IM_MIN -= moveFactor;
                        IM_MAX -= moveFactor;
                        render(context, RE_MIN, RE_MAX, IM_MIN, IM_MAX);
                        break;
                    case DOWN:
                        System.out.println("down");
                        IM_MIN += moveFactor;
                        IM_MAX += moveFactor;
                        render(context, RE_MIN, RE_MAX, IM_MIN, IM_MAX);
                        break;
                     case ENTER:
                        SnapshotParameters parameters = new SnapshotParameters();
                        parameters.setFill(Color.TRANSPARENT);
                        WritableImage snapshot = canvas.snapshot(parameters, null);
                        File file = new File("mandelbrotParallel.png");
                        try{
                            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                            System.out.println("Frame saved as "+ file.getAbsolutePath());
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        System.out.println("def");
                        break;
                }
            }
        });

        render(
            context,
            RE_MIN,
            RE_MAX,
            IM_MIN,
            IM_MAX
        ); 
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //System.out.println(cores);
        
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());
        
        canvas.widthProperty().addListener((obs, oldVal, newVal) -> {
        	WIDTH = newVal.doubleValue();
        	
            resizePause.setOnFinished(e -> render(context, RE_MIN, RE_MAX, IM_MIN, IM_MAX));
            resizePause.playFromStart();
        });
       
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> {
        	HEIGHT = newVal.doubleValue();
        	
        	resizePause.setOnFinished(e -> render(context, RE_MIN, RE_MAX, IM_MIN, IM_MAX));
        	resizePause.playFromStart();
        });
        
        long endTime = System.currentTimeMillis();
        long runtime = endTime - startTime;
        System.out.println("Runtime: "+ runtime + " milliseconds" );
    }//start
    
    

    private void render(GraphicsContext gCon, double reMin, double reMax, double imMin, double imMax){
        
        
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, 10, TimeUnit.SECONDS, queue);
        //ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        int numLinesPerThread = (int) Math.ceil(HEIGHT / THREAD_POOL_SIZE);
        //System.out.println(numLinesPerThread);
        ArrayList<LineTask> jobs = new ArrayList<LineTask>(THREAD_POOL_SIZE);

        // Calculate number of lines to be rendered by each thread
        
        //Initialization of tasks
        for(int i=0; i < THREAD_POOL_SIZE; i++){
            //double startLineID = i * numLinesPerThread;
            double startLineID = i * (double) HEIGHT/ THREAD_POOL_SIZE;
            LineTask task = new LineTask(startLineID, reMin, reMax, imMin, imMax, WIDTH, numLinesPerThread, gCon);
            threadPool.execute(task);
            jobs.add(task);               
        }
        
        threadPool.shutdown();

        try {
            threadPool.awaitTermination(100000000L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        for (LineTask task : jobs) {
            WritableImage tmpImg = task.chunkImage;
            pixelWriter.setPixels(0, (int) task.lineID,(int) WIDTH, numLinesPerThread, tmpImg.getPixelReader(), 0, 0);
        }

        context.drawImage(image, 0, 0);
        
    }//render

   


    public static void main(String[] args) {
        launch(args);
    }

    //This class is a runnable task that renders a line of the canvas
    class LineTask implements Runnable{
        double lineID,reMin, reMax, imMin, imMax, width;
        int numLinesPerThread;
        GraphicsContext gCon;
        WritableImage chunkImage;
        PixelWriter chunkPixelWriter;

        public LineTask(double lineID, double reMin, double reMax, double imMin, double imMax, double width, int numLinesPerThread, GraphicsContext gCon){
            this.lineID = lineID;
            this.reMin = reMin;
            this.reMax = reMax;
            this.imMin = imMin;
            this.imMax = imMax;
            this.width = width;
            this.numLinesPerThread = numLinesPerThread;
            this.gCon = gCon;
            chunkImage = new WritableImage((int) WIDTH, numLinesPerThread);
            chunkPixelWriter = chunkImage.getPixelWriter();
        }
        public void run(){
            

            for(double c = reMin, x=0; x < WIDTH; c = c + precision, x++){
                for(double ci = imMin + (lineID * precision), y=0; y < numLinesPerThread; ci = ci + precision, y++){
                    renderPoint(reMin, reMax, imMin, imMax, width, c, ci, gCon, (int) x, (int) y);
                }
            }//forj

        }//run

        public synchronized void renderPoint(double reMin, double reMax, double imMin, double imMax, double width, double c, double ci, GraphicsContext gCon, int xre, int yre) {
        
            double convergence = checkConvergence(ci, c, maxIterations);
            double colorValue = (double) convergence / maxIterations;
        
            if (convergence != maxIterations) {
                chunkPixelWriter.setColor(xre, yre, Color.hsb(colorValue * 350, 1.0, 1.0));
               
            } else {
                chunkPixelWriter.setColor(xre, yre, Color.BLACK);
                
            }
            
        }

        public int checkConvergence(double ci, double c, int convergenceSteps){
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

        
    }//LineTask
}//MSFrameParallel


