
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Cliente{
    static Graphic ventana = new Graphic();
    public static int dificultad = 0;

    public static String[] celdaSelected = {"0", "0", "X"}; //{fila,columna,modo}
    public static String[] celdaSelectedTEMP = {"0", "0", "X"};
    //NOTA: Aquí el modo puede ser FLAG (F) o DISCOVER (D), eso se determina si es click derecho o izquierdo


    public static void main(String[] args) throws ClassNotFoundException {
        try {
            // Crear el socket del cliente
            Socket socket = new Socket("localhost", 3000);
            
            //Despues de conectarnos mostramos la ventana grafica
            ventana.setVisible(true);
            
            // Flujos para enviar y recibir datos
            ObjectOutputStream escribir = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream leer = new ObjectInputStream(socket.getInputStream());
            
            // System.out.println("Ahorita en un rato envio la dificultad");

            //LOGICA
            //Se manda la selección de nivel para iniciar el juego
            while (true) {
                if (dificultad != 0) { //Ya se selecciono una dificultad
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            
            //Ahora mando el mensaje inicial
            MensajeCliente mensaje = new MensajeCliente(dificultad);
            escribir.reset();
            escribir.writeObject(mensaje);
            escribir.flush();
            System.out.println("Cliente: Ya inicié el juego");

            MensajeServidor matriz = (MensajeServidor) leer.readObject();

            //IMPORTANTE: Esta matriz recibida no es la solucion al juego, sino que es la matriz en tiempo
            //real que el usuario esta destapando y colocando banderas
            System.out.println("Matriz de juguete inicial: ");
            printMatriz(matriz.getMatriz());

            //Seguimos jugando
            while(matriz.getResultado() == 2){
                //Selecciono una celda
                // System.out.println("CLIENTE ESPERANDO CELDA");
                esperarClickCelda();
                mensaje.setCeldaSelected(celdaSelected);
                escribir.reset();
                escribir.writeObject(mensaje);
                System.out.println("CLIENTE mensaje enviado: " + Arrays.toString(mensaje.getCeldaSelected()));
                escribir.flush();


                System.out.println("Jugando");

                //Actualizo campo de minas en base a respuesta del servidor
                matriz = (MensajeServidor) leer.readObject();
                System.out.println("Matriz de juguete actualizada: ");
                printMatriz(matriz.getMatriz());
            }

            //Terminó el juego
            escribir.reset();
            mensaje.setEndGame(1);
            escribir.writeObject(mensaje);
            System.out.println("Juego terminado en cliente");

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

        System.out.println("Ahora celdaSelected vale:");
        System.out.println(Arrays.toString(celdaSelected));
    }

    public static void esperarClickCelda(){
        while (true) {
            if (!Arrays.equals(celdaSelected, celdaSelectedTEMP)) {
                celdaSelectedTEMP = celdaSelected.clone(); // Actualiza celdaSelectedTEMP con una copia de celdaSelected
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Para pruebas en consola
    public static void printMatriz(String[][] matriz){
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print(matriz[i][j] + "  ");
            }
            System.out.println();
        }
    }
}