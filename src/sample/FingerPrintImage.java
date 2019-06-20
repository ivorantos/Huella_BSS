package sample;

import java.awt.image.BufferedImage;

public class FingerPrintImage {



    public enum Fase { CARGA, ESCALA_GR,HIST,BN,FILTER,THIN,MIN}

    private int [] [] imagen;//matriz de imagen

    private Fase fase;//fase en la que se encuentra la imagen


    public FingerPrintImage() {

        fase=Fase.CARGA;//por defecto al inicio
    }


    public FingerPrintImage(int[][] img, Fase fase) {

        imagen=new int[img.length][img[0].length];

        imagen=img.clone();



        this.fase = fase;
    }


    /**
     * Constructor por copia
     * @param f
     */
    public FingerPrintImage(FingerPrintImage f) {

        imagen=new int[f.getWidth()][f.getHeight()];

        for(int i=0; i<imagen.length; i++)
            for(int j=0; j<imagen[i].length; j++)
                imagen[i][j]=f.getImagen()[i][j];


        this.fase = f.fase;
    }

    public FingerPrintImage(FingerPrintImage f,int x,int y,int xi,int yi){

        fase=f.fase;
        imagen=new int[(xi-x)+1][(yi-y)+1];

        for (int i=x;i<xi;i++){
            for (int j=y;j<yi;j++){

                int r=i-x;
                int c=j-y;

                imagen[r][c]=f.getPixel(i,j);

            }
        }





    }

    public FingerPrintImage(BufferedImage b, Fase fase) {
        this.imagen = new int[b.getWidth()][b.getHeight()];//matriz del mismo tamaño

        for (int i = 0; i < b.getWidth(); i++) {//anchura(numero de columnas y); va cambiando la x cartesiana
            for (int j = 0; j < b.getHeight(); j++) {//altura (numero de filas x); va cambiando la y cartesiana

                this.imagen[i][j] = b.getRGB(i, j);



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

    public BufferedImage getBuffer(int modo){

        BufferedImage buffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);//buffered salida

        for (int i = 0; i < this.getWidth(); i++) {//anchura(numero de columnas y); va cambiando la x cartesiana
            for (int j = 0; j < this.getHeight(); j++) {//altura (numero de filas x); va cambiando la y cartesiana


                int valor = this.getPixel(i, j);
                if (modo == 0) {//B/N
                    valor = valor * 255;
                }
                int pixelRGB = (255 << 24 | valor << 16 | valor << 8 | valor);
                buffer.setRGB(i, j, pixelRGB);
            }
        }
        return buffer;
    }




}
