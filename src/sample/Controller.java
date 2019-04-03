package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Controller {

    @FXML
    private ImageView img_viewer1;

    @FXML
    private ImageView img_viewer2;




//    private String imageFile;


    public void clean (ActionEvent actionEvent) {

//            img_viewer1.setImage(null);
//            img_viewer2.setImage(null);

        img_viewer1.imageProperty().set(null);
        img_viewer2.imageProperty().set(null);


    }


    public void chooseFile(ActionEvent actionEvent) throws IOException {


        Button b=(Button) actionEvent.getSource();



        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open image");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.bmp", "*.png", "*.jpg", "*.gif")); // limit chooser options to image files

        File file = chooser.showOpenDialog(new Stage());


        if (file != null) {
            //String imagepath = file.toURI().toURL().toString();

            //System.out.println("************************************file:" + imagepath);

           Image img= SwingFXUtils.toFXImage(ImageIO.read(file), null);


          if(b.getId().equals("button1")){
                img_viewer1.setImage(img);

            }

          else if(b.getId().equals("button2")){
                img_viewer2.setImage(img);

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
