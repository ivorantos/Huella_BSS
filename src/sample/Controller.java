package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller {

    final private static int[][] nbrs = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

    final private static int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6}, {0, 4, 6}}};

    static List<Point> toWhite = new ArrayList<Point>();

    private int corte = 0;
    private int bifurcacion = 0;

    private int paso = 0;

    ObservableList<String> OptionsList = FXCollections.observableArrayList("Cargar", "Grises", "Histograma", "B/N", "Filtros", "Adelgazamiento", "Minucias", "Ángulos", "Limpiar");


    @FXML
    private ImageView img_viewer1;

    @FXML
    private ImageView img_viewer2;


    @FXML
    private JFXSlider slider;

    @FXML
    private Label umbral;

    @FXML
    private JFXButton BN;

    private BufferedImage image_buffer;//buffer de la imagen actual

    private FingerPrintImage image_finger_act;//fingerPrint de la imagen actual (se pisa constantemente)

    private FingerPrintImage image_finger_ant;//fingerPrint de la imagen anterior (Se mantiene hasta el inicio del siguiente paso)

    @FXML
    private ArrayList<minucia> minucias;

    @FXML
    private Button min;

    @FXML
    private ChoiceBox<String> options;

    @FXML
    void click(int g) {

        if (paso == 4) {

            int[][] mat = new int[image_finger_ant.getWidth()][image_finger_ant.getHeight()];//matriz del mismo tamaño


            for (int x = 0; x < image_finger_ant.getWidth(); x++) {
                for (int y = 0; y < image_finger_ant.getHeight(); y++) {


                    if (image_finger_ant.getPixel(x, y) < g) {

                        mat[x][y] = 0;
                    } //negro
                    else {
                        mat[x][y] = 1;//blanco
                    }
                }
            }

            image_finger_act = new FingerPrintImage(mat, FingerPrintImage.Fase.BN);

            Show(0);


        }
    }

    @FXML
    private void initialize() {
        options.setItems(OptionsList);

        options.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> main(OptionsList.get(newValue.intValue())));

        slider.valueProperty().addListener((observable, oldValue, newValue) -> click(newValue.intValue()));
    }


    //pila de deshacer; controlar la imagen actual y actualizarla todo
/*
Bufferedimage b= Swing.FXUtils.FromFximage();  image to buffer
metodo de show(conversion) imgRandom->Image?????
 */


    private void clean() {


        img_viewer1.imageProperty().set(null);
        img_viewer2.imageProperty().set(null);

        image_finger_act = null;
        image_finger_ant = null;


    }


    private void RGB2Grey() {

        int[][] mat = new int[image_buffer.getWidth()][image_buffer.getHeight()];//matriz del mismo tamaño

        for (int i = 0; i < image_buffer.getWidth(); i++) {//anchura(numero de columnas y); va cambiando la x cartesiana
            for (int j = 0; j < image_buffer.getHeight(); j++) {//altura (numero de filas x); va cambiando la y cartesiana

                int rgb = image_buffer.getRGB(i, j);
                int r = (rgb >> 16) & 0xFF;//shift y and con 255
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
                int nivelGris = (r + g + b) / 3;
                mat[i][j] = nivelGris;

            }
        }

        image_finger_act = new FingerPrintImage(mat, FingerPrintImage.Fase.ESCALA_GR);//actual a grises

    }


    private void Hist2BN(int umbral) {

        int[][] mat = new int[image_finger_act.getWidth()][image_finger_act.getHeight()];//matriz del mismo tamaño


        for (int x = 0; x < image_finger_act.getWidth(); x++) {
            for (int y = 0; y < image_finger_act.getHeight(); y++) {


                if ((image_finger_act.getPixel(x, y)) < umbral) {

                    mat[x][y] = 0;
                } //negro
                else {
                    mat[x][y] = 1;//blanco
                }
            }
        }

        image_finger_act = new FingerPrintImage(mat, FingerPrintImage.Fase.BN);

    }


    /**
     * Paso de la finger actual a (Buffer y a ->) Image para pintarla en pantalla
     *
     * @param modo
     * @return
     */
    private void Show(int modo) {


        BufferedImage buffer = new BufferedImage(image_finger_act.getWidth(), image_finger_act.getHeight(), BufferedImage.TYPE_INT_RGB);//buffered salida

        for (int i = 0; i < image_finger_act.getWidth(); i++) {//anchura(numero de columnas y); va cambiando la x cartesiana
            for (int j = 0; j < image_finger_act.getHeight(); j++) {//altura (numero de filas x); va cambiando la y cartesiana

                int valor = image_finger_act.getPixel(i, j);
                if (modo == 0) {//B/N
                    valor = valor * 255;

                }
                int pixelRGB = (255 << 24 | valor << 16 | valor << 8 | valor);

                buffer.setRGB(i, j, pixelRGB);
            }
        }

        img_viewer2.setImage(SwingFXUtils.toFXImage(buffer, null));//Buffer to image para pintar

    }


    private void filterImg(int f) {

        //parte central de la imagen

        for (int x = 1; x < image_finger_act.getWidth() - 1; x++) {

            for (int y = 1; y < image_finger_act.getHeight() - 1; y++) {

                int centro = image_finger_act.getPixel(x, y);//pixel central

                int v[] = new int[nbrs.length - 1];

                for (int i = 0; i < nbrs.length - 1; i++) {


                    v[i] = image_finger_act.getImagen()[x + nbrs[i][1]][y + nbrs[i][0]];
                }

                if (f == 1) {
                    int f1 = centro | v[0] & v[4] & (v[7] | v[2]) | v[7] & v[2] & (v[0] | v[4]);

                    image_finger_act.setPixel(x, y, f1);//le pongo valor de filtro 1

                } else {

                    int f2 = centro & ((v[7] | v[0] | v[6]) & (v[2] | v[4] | v[3]) | (v[0] | v[1] | v[2]) & (v[6] | v[5] | v[4]));//calculo f2 con valores de f1

                    image_finger_act.setPixel(x, y, f2);//le pongo valor de filtro 2

                }


                image_finger_act.setFase(FingerPrintImage.Fase.FILTER);


            }
        }

    }

    private void Grey2Hist() {


        int width = image_finger_act.getWidth();
        int height = image_finger_act.getHeight();
//        FingerPrintImage image_finger_aux = new FingerPrintImage(new int[width][height], FingerPrintImage.Fase.HIST);//nueva actual
        image_finger_act.setFase(FingerPrintImage.Fase.HIST);
        int tampixel = width * height;
        int[] histograma = new int[256];


        // Calculamos frecuencia relativa de ocurrencia
        // de los distintos niveles de gris en la imagen
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int valor = image_finger_act.getPixel(x, y);
                histograma[valor]++;
            }
        }
        int sum = 0;
// Construimos la Lookup table LUT
        float[] lut = new float[256];
        for (int i = 0; i < 256; i++) {
            sum += histograma[i];
            lut[i] = sum * 255 / tampixel;
        }
// Se transforma la imagen utilizando la tabla LUT
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int valor = image_finger_act.getPixel(x, y);
                int valorNuevo = (int) lut[valor];
                image_finger_act.setPixel(x, y, valorNuevo);//actualizo

            }
        }

    }

    private void thinning() {

        boolean firstStep = false;
        boolean hasChanged;


        do {
            hasChanged = false;
            firstStep = !firstStep;

            for (int i = 1; i < image_finger_act.getWidth() - 1; i++) {
                for (int j = 1; j < image_finger_act.getHeight() - 1; j++) {

                    if (image_finger_act.getImagen()[i][j] != 0)
                        continue;

                    int nv = numNeighbors(i, j);
                    if (nv < 2 || nv > 6)
                        continue;

                    int tr = transitions(i, j);

                    if (tr != 1 || tr != 2 && EvenOddcomp(i, j, i + j % 2 == 0 ? 0 : 1))
                        continue;


                    if (!atLeastOneIsWhite(i, j, firstStep ? 0 : 1))
                        continue;

                    toWhite.add(new Point(i, j));
                    hasChanged = true;
                }
            }

            for (Point p : toWhite)
                image_finger_act.getImagen()[p.x][p.y] = 1;
            toWhite.clear();

        } while (firstStep || hasChanged);

        image_finger_act.setFase(FingerPrintImage.Fase.THIN);

    }

    private Boolean EvenOddcomp(int i, int j, int odd) {

        Boolean b1;

        int v[] = new int[nbrs.length - 1];

        for (int x = 0; x < nbrs.length - 1; x++) {


            v[x] = image_finger_act.getImagen()[i + nbrs[x][1]][j + nbrs[x][0]];
        }

        if (odd == 0) {

            //p2xp8=1&&p5=0
            //p6xp8=1&&p3xp4xp7=1
            b1 = v[0] * v[6] == 1 && v[3] == 0 || v[4] * v[6] == 1 && v[1] * v[2] * v[5] == 1;

        } else {

            //p4xp6=1&&p9=0
            //p4xp2=1&&p3xp7xp8=1
            b1 = v[2] * v[4] == 1 && v[7] == 0 || v[2] * v[0] == 1 && v[1] * v[5] * v[6] == 1;

        }


        return b1;

    }


    private void minucias() {

        int v[] = new int[9];

        int s = 0;
        minucias = new ArrayList<>();

        BufferedImage b = image_finger_act.getBuffer(0);


        for (int x = 1; x < image_finger_act.getWidth() - 1; x++) {
            for (int y = 1; y < image_finger_act.getHeight() - 1; y++) {

                if (image_finger_act.getPixel(x, y) == 0) {//negro

                    v[0] = image_finger_act.getPixel(x, y + 1);
                    v[1] = image_finger_act.getPixel(x - 1, y + 1);
                    v[2] = image_finger_act.getPixel(x - 1, y);
                    v[3] = image_finger_act.getPixel(x - 1, y - 1);
                    v[4] = image_finger_act.getPixel(x, y - 1);
                    v[5] = image_finger_act.getPixel(x + 1, y - 1);
                    v[6] = image_finger_act.getPixel(x + 1, y);
                    v[7] = image_finger_act.getPixel(x + 1, y + 1);
                    v[8] = image_finger_act.getPixel(x, y + 1);


                    for (int w = 0; w < v.length - 1; w++) {
                        s = s + Math.abs(v[w] - v[w + 1]);
                    }

                    s /= 2;

                    if (s == 1 || s == 3) {

                        minucias.add(new minucia(s, new Point(x, y)));


                        b.setRGB(x - 1, y, Color.MAGENTA.getRGB());
                        b.setRGB(x + 1, y, Color.MAGENTA.getRGB());
                        b.setRGB(x, y - 1, Color.MAGENTA.getRGB());
                        b.setRGB(x, y + 1, Color.MAGENTA.getRGB());
                    }

                    s = 0;

                }


            }

        }

        for (minucia m : minucias) {

            if (m.tipo == 1)
                corte++;
            else
                bifurcacion++;

        }


        img_viewer2.setImage(SwingFXUtils.toFXImage(b, null));//se muestra aqui directamente porque se usa el buffer con las minucias marcadas

    }


    public void main(String s) {


        slider.setVisible(false);//las escondo
        umbral.setVisible(false);


        switch (s) {

            case "Cargar":

                if (paso > 1 && paso < 8) {

                    if (confirm_alert() == 0)

                        break;

                    else
                        paso = 0;


                }

                paso++;

                try {
                    clean();
                    chooseFile();

                } catch (IOException e) {
                    System.err.println("PROBLEM during choose file");
                }
                break;

            case "Limpiar":

                if (paso > 1 && paso < 8) {


                    if (confirm_alert() == 0)

                        break;

                    else
                        paso = 0;

                }

                clean();
                break;

            case "Grises":

                if (paso == 1) {

                    paso++;

                    image_finger_ant = new FingerPrintImage(image_finger_act);//antes de trabajar sobre la actual la guardo
                    RGB2Grey();
                    Show(1);//pinto

                }

                else{
                    info_alert();
                }

                    break;



            case "Histograma":

                if (paso == 2) {

                    paso++;


                    image_finger_ant = new FingerPrintImage(image_finger_act);//antes de trabajar sobre la actual la guardo
                    Grey2Hist();
                    Show(1);

                } else{
                    info_alert();
                }

                break;

            case "B/N":

                if (paso == 3) {

                    //segun pulso el boton hago visibles el slider y el label
                    paso++;


                    slider.setVisible(true);
                    umbral.setVisible(true);

                    image_finger_ant = new FingerPrintImage(image_finger_act);//antes de trabajar sobre la actual la guardo
                    Hist2BN((int) slider.getValue());
//                interest_region();

                    Show(0);

                } else{
                    info_alert();
                }

                break;

            case "Filtros":

                if (paso == 4) {

                    paso++;

                    image_finger_ant = new FingerPrintImage(image_finger_act);//antes de trabajar sobre la actual la guardo
                    filterImg(1);//filtro 1
                    filterImg(2);//filtro 2
                    Show(0);
                } else{
                    info_alert();
                }
                break;


            case "Adelgazamiento":

                if (paso == 5) {

                    paso++;

                    image_finger_ant = new FingerPrintImage(image_finger_act);//antes de trabajar sobre la actual la guardo
                    thinning();
                    Show(0);

                } else{
                    info_alert();
                }
                break;

            case "Minucias":

                if (paso == 6) {

                    paso++;

                    image_finger_ant = new FingerPrintImage(image_finger_act);//antes de trabajar sobre la actual la guardo
                    minucias();//ya se muestra desde aqui
                } else{
                    info_alert();
                }
                break;

            case "Ángulos":


                if (paso == 7) {

                    paso++;

                    image_finger_ant = new FingerPrintImage(image_finger_act);//antes de trabajar sobre la actual la guardo
                    angles();
                    record_log();
                } else{
                    info_alert();
                }
                break;




        }


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

//            image_act = SwingFXUtils.toFXImage(ImageIO.read(file), null);//convertir de archivo a image
//            image_finger_ant

            image_finger_act = new FingerPrintImage(ImageIO.read(file), FingerPrintImage.Fase.CARGA);//construir un Finger con esto(para la actual)

            image_buffer = ImageIO.read(file);//solo se en el primer paso
//            image_finger_ant=new FingerPrintImage(image_finger_act);//en este caso actualizamos anterior y actual a la imagen cargada

            img_viewer1.setImage(SwingFXUtils.toFXImage(ImageIO.read(file), null));//se carga en pantalla

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Please Select a File");
            alert.showAndWait();
        }


    }


    /**
     * Calcula el numero de vecinos (1) que tiene el pixel x,y
     * dadas.
     *
     * @param fila
     * @param columna
     * @return vecinos
     */
    private int numNeighbors(int fila, int columna) {

        int vecinos = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (image_finger_act.getImagen()[fila + nbrs[i][1]][columna + nbrs[i][0]] == 0)//negro
                vecinos++;

        return vecinos;
    }

    /**
     * Calcula el numero de transiciones de cero a uno en los pixeles vecinos al (x,y).
     *
     * @param x
     * @param y
     * @return transiciones
     */
    private int transitions(int x, int y) {

        int transitions = 0;

        for (int i = 0; i < nbrs.length - 1; i++)
            if (image_finger_act.getImagen()[x + nbrs[i][1]][y + nbrs[i][0]] == 1) {//blanco
                if (image_finger_act.getImagen()[x + nbrs[i + 1][1]][y + nbrs[i + 1][0]] == 0)//negro
                    transitions++;
            }
        return transitions;
    }


    private boolean atLeastOneIsWhite(int r, int c, int step) {


        int count = 0;
        int[][] group = nbrGroups[step];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < group[i].length; j++) {
                int[] nbr = nbrs[group[i][j]];
                if (image_finger_act.getImagen()[r + nbr[1]][c + nbr[0]] == 1) {//blanco
                    count++;
                    break;
                }
            }
        return count > 1;
    }


    private Point nextBlack(Point p, ArrayList<Point> w) {

        Point v[] = new Point[nbrs.length - 1];//vecinos
        Point next = new Point(9, 9, -88);//siguiente nulo
        Boolean x = false;

        for (int i = 0; i < nbrs.length - 1; i++) {

            Point punto = new Point((p.x + nbrs[i][1]), (p.y + nbrs[i][0]), image_finger_act.getImagen()[(p.x + nbrs[i][1])][(p.y + nbrs[i][0])]);
            v[i] = punto;
        }

        for (Point c : v) {

            if (c.valor == 0 && !w.contains(c))//es negro;forma parte del recorrido de la minucia y no esta visitado
                next = c;


        }

        return next;

    }

    private double calculate(Point l) {

        double result = 0;


        if (l.x != 0) { // No divisiones entre 0
            result = l.y / l.x;
            result = (Math.atan(result)); // Arc tg
            result = Math.toDegrees(result); //  grados.
        } else {
            if (l.y != 0 && l.x == 0) {
                result = 90;
            }


        }


        return result;


    }

    private void pixels(ArrayList<Point> x, int p, Point act) {

        if (p < 6) {//si no es ultima iteracion

            x.add(act);//añado actual a visitados

            Point next = nextBlack(act, x);
            p++;//inc paso

            if (next.valor != -88)//-88 seria que no ha encontrado siguiente->error
                pixels(x, p, next);//vuelvo a llmada recursiva con el siguiente

        }

        //cuando no se cumpla no hago nada porque ya tengo los visitados llenos que es lo que quiero para calcularlo abajo
    }

    private void angles() {

        double result = 0;
        Point Gx_Gy;
        double[] ang_bif = new double[3];


        ArrayList<Point> visitados = new ArrayList<>();

        for (minucia m : minucias) {//cada minucia

            result = 0;//se limpia despues de cada minucia

            if (m.tipo == 1) {//minucias corte

                visitados.clear();

                pixels(visitados, 0, m.cordenadas);//visitados (vacio),paso 0,y punto actual minucia

                Gx_Gy = new Point();

                Gx_Gy.x = visitados.get(visitados.size() - 1).x - visitados.get(0).x;//xf-xi
                Gx_Gy.y = visitados.get(visitados.size() - 1).y - visitados.get(0).y;//yf-yi

                result = calculate(Gx_Gy);

                for (minucia a : minucias) {
                    if (a.equals(m))//cuando encuentre la minucia
                        a.setAngulo(result);//le pongo su angulo
                }


            } else {//bifurcacion tiene que sacar 6 vecinos por rama; 18

                visitados.clear();

                for (int z = 0; z < 3; z++) {//tres ramas de la bifurcacion

                    pixels(visitados, 0, m.cordenadas);//visitados (lleno),paso 0,y punto actual minucia

                    Gx_Gy = new Point();

                    try {
                        Gx_Gy.x = visitados.get(visitados.size() - 1).x - visitados.get(6 * z).x;//xf-xi
                        Gx_Gy.y = visitados.get(visitados.size() - 1).y - visitados.get(6 * z).y;//yf-yi

                    } catch (Exception e) {
                    } finally {


                        ang_bif[z] = calculate(Gx_Gy);//guardo los angulos de cada

                    }
                }

                //ya tengo los tres angulos de las ramas
                double f = 0;
                for (double d : ang_bif) {
                    f += d;
                }

                result = f / 3;

                for (minucia a : minucias) {
                    if (a.equals(m))
                        a.setAngulo(result);
                }
            }


        }

    }

    private void record_log() {

        FileWriter fichero = null;
        PrintWriter pw = null;

        try {
            fichero = new FileWriter("information.log");
            pw = new PrintWriter(fichero);

            pw.println("Tipo       Coordenadas      Ángulo");
            pw.println("----------------------------------");

            for (minucia m : minucias) {

                if (m.getTipo() == 1) {//corte

                    pw.println("Corte       " + m.cordenadas.x + "," + m.cordenadas.y + "    " + m.getAngulo());


                } else {//bifurcacion

                    pw.println("Bifurcación       " + m.cordenadas.x + "," + m.cordenadas.y + "    " + m.getAngulo());


                }


            }


            fichero.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Se ha generado un archivo information.log con el resumen del proceso.");
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int confirm_alert() {


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.initStyle(StageStyle.UTILITY);
        alert.setContentText("¿Realmente quiere limpiar/cargar un nuevo archivo? \n Se perderán todos los cambios sobre la imagen actual.");
        Optional <ButtonType> result=alert.showAndWait();

        if (result.get()==ButtonType.OK)
            return 1;
        else
            return 0;


    }


    private void info_alert() {


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Realice el paso/pasos anteriores antes de realizar la acción.");
        alert.showAndWait();


    }






    private void interest_region(){

        int c=0;

        int x=0;
        int y=0;
        int xi=0;
        int yi=0;



        for (int i=0;i<image_finger_act.getWidth();i++){
            c=0;
            for (int j=0;j<image_finger_act.getHeight();j++){

                if(image_finger_act.getPixel(i,j)==0)//negro
                    c++;

            }

            if(c>25){

                x=i;
                break;
            }
        }

        c=0;

        for (int i=image_finger_act.getWidth()-1;i>=0;i--){
            for (int j=image_finger_act.getHeight()-1;j>=0;j--) {

                if(image_finger_act.getPixel(i,j)==0)//negro
                    c++;
              }

            if(c>25) {

                xi = i;
                break;
            }
        }

            for (int j=0;j<image_finger_act.getHeight();j++) {

                if (image_finger_act.getPixel(x,j)==0)//primer pixel negro en la fila marcada por arriba
                    y=j;


            }



            for (int j=image_finger_act.getHeight()-1;j>0;j--) {

                if (image_finger_act.getPixel(x,j)==0)
                    yi=j;

        }


        image_finger_act=new FingerPrintImage(image_finger_act,x,y,xi,yi);


    }



    class minucia{

        minucia(){

        }

        public int getTipo() {
            return tipo;
        }

        public void setTipo(int tipo) {
            this.tipo = tipo;
        }

        public Point getCordenadas() {
            return cordenadas;
        }

        public void setCordenadas(Point cordenadas) {
            this.cordenadas = cordenadas;
        }

        public minucia(int tipo, Point cordenadas) {
            this.tipo = tipo;

            this.cordenadas = cordenadas;
            angulo=0.0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

        /* Check if o is an instance of minucia or not
          "null instanceof [type]" also returns false */
            if (!(o instanceof minucia)) {
                return false;
            }

            minucia m = (minucia) o;

            // Compare the data members and return accordingly
            return  this.cordenadas.equals(m.cordenadas) && this.tipo==m.tipo;
        }


        int tipo;//1 corte 3 bifurcacion
        Point cordenadas;//
        double angulo;


        public double getAngulo() {
            return angulo;
        }

        public void setAngulo(double angulo) {
            this.angulo = angulo;
        }
    }


    class Point{

        Point(){

        }
        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getValor() {
            return valor;
        }

        public void setValor(int valor) {
            this.valor = valor;
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
            this.valor = -9999;
        }

        public Point(int x, int y, int valor) {
            this.x = x;
            this.y = y;
            this.valor = valor;
        }

        int x;
        int y;
        int valor;

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

        /* Check if o is an instance of Point or not
          "null instanceof [type]" also returns false */
            if (!(o instanceof Point)) {
                return false;
            }

            Point p = (Point) o;

            // Compare the data members and return accordingly
            return  this.x==((Point) o).x && this.y== ((Point) o).y;
        }
    }

}





