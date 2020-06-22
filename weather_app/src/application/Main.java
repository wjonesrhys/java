package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    //Starts the first scene
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/Main.fxml"));
        primaryStage.setTitle("Weather Data Viewer");
        Scene firstScene = new Scene(root, 900, 900);

        // Imports the css file required for styling the program.
        firstScene.getStylesheets().add(getClass().getResource("assets/css/application.css").toExternalForm());
        primaryStage.setScene(firstScene);
        primaryStage.show();

        // Sets a minimum width and height so that the application.
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
    }

    public static void main(String[] args){
        launch(args);
    }
}
