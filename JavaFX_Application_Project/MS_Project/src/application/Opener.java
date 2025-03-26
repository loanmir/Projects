package application;
import javafx.application.Application;
import javafx.scene.control.ComboBox;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.application.Application;
import javafx.stage.Stage;
//import Distributed.src.Mandelbrot;

public class Opener extends Application {
	//Mandelbrot dist = new Mandelbrot();
	@Override
	public void start(Stage primaryStage) {
		// Create buttons
        
        Button button = new Button("Start");
		Text title = new Text("The Mandelbrot Set");
		title.setFont(new Font(25));
		Label text = new Label("Select the running mode:");
		
		ComboBox<String> dropdown = new ComboBox<>();
        dropdown.getItems().addAll("Sequential", "Parallel");
        dropdown.setValue("Select");

        // Create a layout pane to center the buttons
        /*StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(dropdown);*/
        
        button.setOnAction(event -> {
        	try {
        		String selectedValue = dropdown.getValue();
        		if(selectedValue == "Sequential"){
        			MSFrame seq = new MSFrame();
        			seq.start(new Stage());
        		} else if(selectedValue == "Parallel"){
        			MSFrameParallel par = new MSFrameParallel();
        			par.start(new Stage());
        		} else if(selectedValue == "Select") {
        			System.out.println("Cannot be in select mode");
        		} 
        	} catch(Exception e) {
        		System.out.println("Error has occured: "+e);
        	}
       });
        
        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(title, text, dropdown, button);
       
        

        // Create a scene and add it to the stage
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setTitle("Mandelbrot Set");
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
}

