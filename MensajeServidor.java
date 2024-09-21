
import java.io.Serializable;


/*
    Este será la clase del objeto que el servidor enviará al cliente
 */
public class MensajeServidor implements Serializable{
    private static final long serialVersionUID = 2L;
    private String[][] matriz;
    private int score = 999;
    private int endGame = 0;

    public MensajeServidor(String[][] matriz){
        this.matriz = matriz;
    }

    //Getters y Setters
    public String[][] getMatriz() {
        return matriz;
    }

    public void setMatriz(String[][] matriz) {
        this.matriz = matriz;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setEndGame(int endGame){
        this.endGame = endGame;
    }

    public int getEndGame(){
        return endGame;
    }
}
