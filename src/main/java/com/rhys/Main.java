package com.rhys;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    // Starts the first scene
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(loadFXML("Main"), 900, 900);
        stage.setTitle("Weather Data Viewer");
        stage.setScene(scene);
        stage.show();

        // Imports the css file required for styling the program.
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        // Sets a minimum width and height so that the application.
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args){
        launch(args);
    }
}
