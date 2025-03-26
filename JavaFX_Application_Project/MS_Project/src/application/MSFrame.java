package application;
	
import javafx.animation.PauseTransition;


import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
import java.io.File;
import java.io.IOException;
import javafx.util.Duration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;


public class MSFrame extends Application {
    //size of canvas
    private static double HEIGHT = 600;
    private static double WIDTH = 800;
    
    // Left and right border
    
    // Top and Bottom border
    
    //Values for Mandelbrot set
    private static double RE_MIN = -2;
    private static double RE_MAX = 1;
    private static double IM_MIN = -1.2;
    private static double IM_MAX = 1.2;
    private double zoomIncrement = 1.1;
    private double zoomFactor = 1;
    private double moveFactor = 0.3;
    private Canvas canvas;
    PauseTransition resizePause = new PauseTransition(Duration.millis(200));
    @FXML
    StackPane menu;
    @Override
    public void start(Stage primaryStage) throws Exception{
    	long startTime = System.currentTimeMillis();
        Pane root = new Pane();
        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();
        root.getChildren().addAll(canvas);
        Scene scene = new Scene(root);
        context.setFill(Color.YELLOW);
        primaryStage.setTitle("Mandelbrot set");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        

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
                        File file = new File("mandelbrotSequential.png");
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
       

        render(
            context,
            RE_MIN,
            RE_MAX,
            IM_MIN,
            IM_MAX
        );    

        long endTime = System.currentTimeMillis();
        long runtime = endTime - startTime;
        System.out.println("Runtime: "+ runtime + " milliseconds" );
    }//start

   

    private void render(GraphicsContext gCon, double reMin, double reMax, double imMin, double imMax){
       
        double precision = Math.max((reMax - reMin) / WIDTH, (imMax - imMin) / HEIGHT);
        int maxIterations = 50;
        
        
        for(double c = reMin, x=0; x < WIDTH; c = c + precision, x++){
            
            for(double ci = imMin, y=0; y < HEIGHT; ci = ci + precision, y++){
                
                double convergence = checkConvergence(ci, c, maxIterations);
                double colorValue = (double) convergence / maxIterations;
                
                if (convergence != maxIterations) {
                    
                    gCon.setFill(Color.hsb(colorValue * 350, 1.0, 1.0));
                    
                } else {
                    gCon.setFill(Color.BLACK);
                }//ifelse
                    gCon.fillRect(x, y, 1, 1);
                    
            }//fori
            
        }//forj
        
        
    }//render

    

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


    public static void main(String[] args) {
        launch(args);
    }
}// MSFrame

