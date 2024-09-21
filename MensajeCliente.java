
import java.io.Serializable;


/* 
    Este será la clase del objeto que el cliente enviará al servidor
*/
public class MensajeCliente implements Serializable{
    private int dificultad;
    private String[] celdaSelected = new String[3];
    private int endGame = 0;

    public MensajeCliente(int dificultad){
        this.dificultad = dificultad;
        celdaSelected[0] = "0";
        celdaSelected[1] = "0";
        celdaSelected[2] = "X";
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

    public void setDificultad(int dificultad){
        this.dificultad = dificultad;
    }

    public int getDificultad(){
        return dificultad;
    }

    public void setEndGame(int endGame){
        this.endGame = endGame;
    }

    public int getEndGame(){
        return endGame;
    }
}
