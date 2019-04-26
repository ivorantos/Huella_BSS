package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");

//        primaryStage.setMaximized(true);//abrir en pantalla completa
        primaryStage.setScene(new Scene(root, 720, 640));
        primaryStage.show();
    }


    public static void main(String[] args) {


        launch(args);
    }
}
