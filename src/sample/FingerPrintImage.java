package sample;

import java.awt.image.BufferedImage;

public class FingerPrintImage {



    public enum Fase { CARGA, ESCALA_GR,HIST,BN }

    private int [] [] imagen;//matriz de imagen

    private Fase fase;//fase en la que se encuentra la imagen


    public FingerPrintImage() {

        fase=Fase.CARGA;//por defecto al inicio
    }


    public FingerPrintImage(int[][] imagen, Fase fase) {

        this.imagen = imagen;
        this.fase = fase;
    }


    /**
     * Constructor por copia
     * @param f
     */
    public FingerPrintImage(FingerPrintImage f) {

        this.imagen = f.imagen;
        this.fase = f.fase;
    }

    public FingerPrintImage(BufferedImage b, Fase fase) {
        imagen = new int[b.getWidth()][b.getHeight()];//matriz del mismo tamaño

        for (int i = 0; i < b.getWidth(); i++) {//anchura(numero de columnas y); va cambiando la x cartesiana
            for (int j = 0; j < b.getHeight(); j++) {//altura (numero de filas x); va cambiando la y cartesiana

                imagen[i][j] = b.getRGB(i, j);



            }
        }


        this.fase = fase;
    }

    public int getWidth() {
        return imagen.length;
    }


    public int getHeight() {

        return imagen [0].length;//Longitud(vertical) de la primera casilla del vector 0 (horizontal)

    }

    public void setPixel(int i, int j, int valorNuevo) {
        imagen[i][j]=valorNuevo;
    }


    public int getPixel(int i, int j) {

        return imagen[i][j];
    }


    public int[][] getImagen() {
        return imagen;
    }

    public void setImagen(int[][] imagen) {
        this.imagen = imagen;
    }

    public Fase getFase() {
        return fase;
    }


    public void setFase(Fase fase) {
        this.fase = fase;

    }


}
