package application;
	

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;




public class Main extends Application {
	
	public static void main(String[] args) {
		launch(args);
	} 
	@Override
	public void start(Stage stage) {
/*		try {
			
			
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,1000,800, Color.BLUE);
			
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	 */
		try {
		Parent root= FXMLLoader.load(getClass().getResource("Main.fxml"));
		Scene scene= new Scene(root);
		
		String css= this.getClass().getResource("application.css").toExternalForm();
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		scene.getStylesheets().add(css);
		
		stage.setTitle("John Conway's Game of Life");
		//stage.setFullScreen(true);
		stage.setScene(scene);
		stage.show( ); 
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	
}
