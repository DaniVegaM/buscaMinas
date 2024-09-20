
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente{
    static Graphic ventana = new Graphic();
    public static int dificultad;

    public static String[] celdaSelected = {"0", "0", "D"}; //{fila,columna,modo}
    //NOTA: Aquí el modo puede ser FLAG (D) o DISCOVER (D), eso se determina si es click derecho o izquierdo


    public static void main(String[] args) {
        try {
            // Crear el socket del cliente
            Socket socket = new Socket("localhost", 3000);
            
            //Despues de conectarnos mostramos la ventana grafica
            ventana.setVisible(true);

            // Flujos para enviar y recibir datos
            ObjectOutputStream escribir = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream leer = new ObjectInputStream(socket.getInputStream());

            //LOGICA

            
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public static void setDificultad(int dificultad){
        Cliente.dificultad = dificultad;
    }
    public static void setCeldaSelected(String fila, String columna, String modo){
        celdaSelected[0] = fila;
        celdaSelected[1] = columna;
        celdaSelected[2] = modo;
    }
}