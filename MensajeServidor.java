
import java.io.Serializable;


/*
    Este será la clase del objeto que el servidor enviará al cliente
 */
public class MensajeServidor implements Serializable{
    private String[][] matriz;
    private int resultado = 2; // 1= GANADOR, 0= PERDEDOR, 2=JUGANDO

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

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }
}
