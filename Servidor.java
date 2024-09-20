
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor{
    public static void main(String[] args) {

        try {
            // Crear el socket del servidor
            ServerSocket serverSocket = new ServerSocket(3000);
            System.out.println("Servidor esperando conexión");
            
            while(true){
                // Esperar a que el cliente se conecte
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado al servidor");
                reset();
        
                // Flujos para enviar y recibir datos
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                //LOGICA
            }

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public static void reset(){
        //Cada vez que se conecte un cliente se reinician las variables
        //La idea es que solo se conecte 1 cliente

    }
}
