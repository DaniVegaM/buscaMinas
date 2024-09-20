
/*
    Este será la clase del objeto que el servidor enviará al cliente
 */
public class Matriz {
    private String[][] matriz;
    private int resultado; // 1= GANADOR, 0= PERDEDOR

    public Matriz(String[][] matriz){
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
