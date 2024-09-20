
/* 
    Este será la clase del objeto que el cliente enviará al servidor
*/
public class Mensaje {
    private String dificultad;
    private int primeraVez; //Para saber si por primera vez hay que crear la matriz
    private String[] celdaSelected;

    public Mensaje(String dificultad, int primeraVez){
        this.dificultad = dificultad;
        this.primeraVez = primeraVez;
    }

    //Getters y Setters
    public void setCeldaSelected(String[] celdaSelected){
        this.celdaSelected[0] =  celdaSelected[0];
        this.celdaSelected[1] =  celdaSelected[1];
        this.celdaSelected[2] =  celdaSelected[2];
    }

    public String[] getCeldaSelected(){
        return celdaSelected;
    }

    public void setDificultad(String dificultad){
        this.dificultad = dificultad;
    }

    public String getDificultad(){
        return dificultad;
    }

    public void setPrimeraVez(int primeraVez){
        this.primeraVez = primeraVez;
    }

    public int getPrimeraVez(){
        return primeraVez;
    }
}
