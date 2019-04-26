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

public class Controller {

    @FXML
    private ImageView img_viewer1;

    @FXML
    private ImageView img_viewer2;

    private BufferedImage image_buffer;//buffer de la imagen actual

    private FingerPrintImage image_finger_act;//fingerPrint de la imagen actual (se pisa constantemente)
    private FingerPrintImage image_finger_ant;//fingerPrint de la imagen anterior (Se mantiene hasta el inicio del siguiente paso)

    //pila de deshacer; controlar la imagen actual y actualizarla todo
/*
Bufferedimage b= Swing.FXUtils.FromFximage();  image to buffer
metodo de show(conversion) imgRandom->Image?????
 */


    public void clean () {


        img_viewer1.imageProperty().set(null);
        img_viewer2.imageProperty().set(null);

        image_finger_act=null;
        image_buffer=null;


    }

    private void RGB2Grey(){

        int [][] mat=new int [image_buffer.getWidth()][image_buffer.getHeight()];//matriz del mismo tamaño

        for (int i=0;i<image_buffer.getWidth();i++){//anchura(numero de columnas y); va cambiando la x cartesiana
            for (int j=0;j<image_buffer.getHeight();j++){//altura (numero de filas x); va cambiando la y cartesiana

                int rgb = image_buffer.getRGB(i,j);
                int r = (rgb >> 16) & 0xFF;//shift y and con 255
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
                int nivelGris = (r + g + b) / 3;
                mat [i][j]=nivelGris;

            }
        }

        image_finger_act = new FingerPrintImage(mat, FingerPrintImage.Fase.ESCALA_GR);

    }


    /**
     * Paso de la finger actual a (Buffer y a ->) Image para pintarla en pantalla
     * @param modo
     * @return
     */
    private void Grey2RGB(int modo){

        BufferedImage buffer=new BufferedImage(image_finger_act.getWidth(), image_finger_act.getHeight(),BufferedImage.TYPE_INT_RGB);//buffered salida

        for (int i = 0; i< image_finger_act.getWidth(); i++){//anchura(numero de columnas y); va cambiando la x cartesiana
            for (int j = 0; j< image_finger_act.getHeight(); j++){//altura (numero de filas x); va cambiando la y cartesiana


                int valor= image_finger_act.getPixel(i, j);
                if(modo==0){//B/N
                    valor=valor*255;
                }
                int pixelRGB=(255<<24 | valor << 16 | valor << 8 | valor);
                buffer.setRGB(i, j,pixelRGB);
            }
        }



//        image_finger_ant =SwingFXUtils.toFXImage(buffer, null);//Buffer to image   **ESTO ES BASURA HAY QUE HACER UN CONVERTIR PARA ALMACENAR TODAS

    }

    private void Grey2Hist(){



        int width = image_finger_act.getWidth();
        int height = image_finger_act.getHeight();
        image_finger_act=new FingerPrintImage(new int [width][height] , FingerPrintImage.Fase.HIST);//nueva actual
        int tampixel= width*height;
        int[] histograma = new int[256];
        int i =0;



        // Calculamos frecuencia relativa de ocurrencia
        // de los distintos niveles de gris en la imagen
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int valor= image_finger_act.getPixel(x, y);
                histograma[valor]++;
            }
        }
        int sum =0;
// Construimos la Lookup table LUT
        float[] lut = new float[256];
        for ( i=0; i < 256; ++i )
        {
            sum += histograma[i];
            lut[i] = sum * 255 / tampixel;
        }
// Se transforma la imagen utilizando la tabla LUT
        i=0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int valor= image_finger_act.getPixel(x, y);
                int valorNuevo= (int) lut[valor];
                image_finger_act.setPixel(x, y, valorNuevo);//actualizo
                i=i+1;
            }
        }

    }


    public void main(ActionEvent event) {

        Button b=(Button) event.getSource();

        switch (b.getId()){

            case "Cargar":
                try {
                    chooseFile();
                } catch (IOException e) {
                    System.err.println("PROBLEM during choose file");                }
                break;

            case "Limpiar":
                clean();
                break;

            case "Accion"://por determinar;faltan los case y los valores (o botones de cada accion)

                RGB2Grey();
                Grey2RGB(1);
//                img_viewer2.setImage(image_finger_act);//convertir esta mierda
                break;

        }


//        System.out.println("Ancho: "+image_finger_act.getWidth()+"X Alto: "+ image_finger_act.getHeight());


    }


    private void chooseFile() throws IOException {


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

            image_buffer=ImageIO.read(file);//ahora no le veo sentido


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
