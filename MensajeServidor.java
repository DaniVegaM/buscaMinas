
import java.io.Serializable;


/*
    Este será la clase del objeto que el servidor enviará al cliente
 */
public class MensajeServidor implements Serializable{
    private static final long serialVersionUID = 2L;
    private String[][] matriz;

    private int scoreJugador = 999;
    private int scoreTotal = 999;
    private int tiempoJugador = 999;
    private int tiempoTotal = 999;
    
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
    
    public void setEndGame(int endGame){
        this.endGame = endGame;
    }
    
    public int getEndGame(){
        return endGame;
    }
    
    public int getScoreJugador() {
        return scoreJugador;
    }
    
    public int getScoreTotal() {
        return scoreTotal;
    }
    
    public int getTiempoJugador() {
        return tiempoJugador;
    }
    
    public int getTiempoTotal() {
        return tiempoTotal;
    }
    
    public void setScoreJugador(int scoreJugador) {
        this.scoreJugador = scoreJugador;
    }
    
    public void setScoreTotal(int scoreTotal) {
        this.scoreTotal = scoreTotal;
    }
    
    public void setTiempoJugador(int tiempoJugador) {
        this.tiempoJugador = tiempoJugador;
    }
    public void setTiempoTotal(int tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
    }
}
