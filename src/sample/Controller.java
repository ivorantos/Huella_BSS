package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;

public class Controller {

    @FXML

    private ImageView img1;
    private ImageView img2;


    @FXML private Label fileSelected;


//    private String imageFile;


    public void clean (ActionEvent actionEvent) {

            img1.setImage(null);
            img2.setImage(null);


    }


    public void chooseFile(ActionEvent actionEvent) throws MalformedURLException {


        Button b=(Button) actionEvent.getSource();



        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open image");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.bmp", "*.png", "*.jpg", "*.gif")); // limit chooser options to image files

        File file = chooser.showOpenDialog(fileSelected.getScene().getWindow());


        if (file != null) {
            String imagepath = file.toURI().toURL().toString();

            System.out.println("************************************file:" + imagepath);



          if(b.getId().equals("button1")){
                img1.setImage(new Image(imagepath));

            }

          else if(b.getId().equals("button2")){
                img2.setImage(new Image(imagepath));

            }

        }

        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Please Select a File");
            alert.showAndWait();
        }


    }

}
