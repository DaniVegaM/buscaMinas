
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Cliente{
    public static Graphic ventana = new Graphic();
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
            while(matriz.getEndGame() == 0 && faltaDescubrir(matriz.getMatriz())){
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

                repintar(matriz.getMatriz());
            }

            //Terminó el juego
            escribir.reset();
            mensaje.setEndGame(1);
            escribir.writeObject(mensaje);
            System.out.println("Juego terminado en cliente");

            matriz = (MensajeServidor) leer.readObject();
            boolean haGanado = !faltaDescubrir(matriz.getMatriz()); // Si no hay celdas por descubrir, el jugador ha ganado
            System.out.println("Ya se leyo ahora mostramos resultados");
            mostrarResultados(matriz.getScoreJugador(), matriz.getScoreTotal(), matriz.getTiempoJugador(), matriz.getTiempoTotal(), haGanado);


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

    public static boolean faltaDescubrir(String[][] matriz){
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if(matriz[i][j].equals("?")){ //Si falta descubrir retorna true
                    return true;
                }
            }
        }
        return false; //Si no encontro ningun ? retorna false
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

    public static void repintar(String[][] nuevaMatriz) {
        ventana.matriz = nuevaMatriz;
        ventana.repaint();
    }
   
    public static void guardarResultados(int tiempoJugador, int scoreJugador) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./resultados.txt", true))) {
            writer.write(" Tiempo: " + tiempoJugador + " segundos, Puntaje: " + scoreJugador + " puntos");
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Ocurrio un error al guardar los resultados: " + e.getMessage());
        }
    }
    
    
    public static void mostrarResultados(int scoreJugador, int scoreTotal, int tiempoJugador, int tiempoTotal, boolean haGanado) {
        if (haGanado) {
            // Guardar resultados solo si el jugador ganó
            guardarResultados(tiempoJugador, scoreJugador);
        }
    
        if (scoreJugador > scoreTotal) {
            ventana.scoreJugador = "New record! Score: " + String.valueOf(scoreJugador) + " points";
        } else {
            ventana.scoreJugador = "Score reached: " + String.valueOf(scoreJugador) + " points";
        }
        ventana.scoreTotal = "Score Record: " + String.valueOf(scoreTotal) + " points";
        if (tiempoJugador < tiempoTotal && haGanado) {
            ventana.tiempoJugador = "New record! Time: " + String.valueOf(tiempoJugador) + " seconds";
        } else {
            ventana.tiempoJugador = "Game finished in: " + String.valueOf(tiempoJugador) + " seconds";
        }
        ventana.tiempoRecord = "Time Record: " + String.valueOf(tiempoTotal) + " seconds";
    
        ventana.repaint();
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