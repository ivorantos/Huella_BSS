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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Controller {

    @FXML
    private ImageView img_viewer1;

    @FXML
    private ImageView img_viewer2;

    private BufferedImage image_buffer;

    private FingerPrintImage image_finger;




//    private String imageFile;


    public void clean (ActionEvent actionEvent) {

//            img_viewer1.setImage(null);
//            img_viewer2.setImage(null);

        img_viewer1.imageProperty().set(null);
        img_viewer2.imageProperty().set(null);


    }

    public FingerPrintImage RGB2Grey(){

        int [][] mat=new int [image_buffer.getWidth()][image_buffer.getHeight()];//matriz del mismo tamaño

        for (int i=0;i<image_buffer.getWidth();i++){//anchura(numero de columnas y); va cambiando la x cartesiana
            for (int j=0;j<image_buffer.getHeight();){//altura (numero de filas x); va cambiando la y cartesiana

                int rgb = image_buffer.getRGB(i,j);
                int r = (rgb >> 16) & 0xFF;//shift y and con 255
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
                int nivelGris = (r + g + b) / 3;
                mat [i][j]=nivelGris;

            }
        }

        return new FingerPrintImage(mat, FingerPrintImage.Fase.ESCALA_GR);

    }


    public Image Grey2RGB(int modo){

        BufferedImage buffer=new BufferedImage(image_finger.getWidth(),image_finger.getHeight(),BufferedImage.TYPE_INT_RGB);//buffered salida

        for (int i=0;i<image_finger.getWidth();i++){//anchura(numero de columnas y); va cambiando la x cartesiana
            for (int j=0;j<image_finger.getHeight();){//altura (numero de filas x); va cambiando la y cartesiana


                int valor= image_finger.getPixel(i, j);
                if(modo==0){//B/N
                    valor=valor*255;
                }
                int pixelRGB=(255<<24 | valor << 16 | valor << 8 | valor);
                buffer.setRGB(i, j,pixelRGB);
            }
        }

        return SwingFXUtils.toFXImage(buffer, null);

    }


    public void chooseFile(ActionEvent actionEvent) throws IOException {


        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open image");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.bmp", "*.png", "*.jpg", "*.gif")); // limit chooser options to image files

        File file = chooser.showOpenDialog(new Stage());


        if (file != null) {
            //String imagepath = file.toURI().toURL().toString();

            //System.out.println("************************************file:" + imagepath);

           Image img= SwingFXUtils.toFXImage(ImageIO.read(file), null);//convertir de archivo a image

            image_buffer=ImageIO.read(file);

                img_viewer1.setImage(img);


        }

        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Please Select a File");
            alert.showAndWait();
        }


    }

}
