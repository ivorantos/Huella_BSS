package sample;

public class FingerPrintImage {



    public enum Fase { CARGA, ESCALA_GR,HIST }

    private int [] [] imagen;//matriz de imagen

    private Fase fase;//fase en la que se encuentra la imagen


    public FingerPrintImage() {

        fase=Fase.CARGA;//por defecto al inicio
    }

    public FingerPrintImage(int[][] imagen, Fase fase) {

        this.imagen = imagen;
        this.fase = fase;
    }

    public int getWidth() {
        return imagen.length;
    }


    public int getHeight() {

        return imagen [0].length;//Longitud(vertical) de la primera casilla del vector 0 (horizontal)

    }

    public void setPixel(int i, int j, int valorNuevo) {
        imagen [i] [j]=valorNuevo;
    }


    public int getPixel(int i, int j) {

        return imagen [i] [j];
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
