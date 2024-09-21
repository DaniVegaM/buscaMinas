
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Servidor{
    private static int numBombas = 0;
    private static String[][] matrizJuguete;
    private static String[][] matriz;
    private static MensajeServidor mensajeServidor;
    public static void main(String[] args) throws ClassNotFoundException {

        try {
            // Crear el socket del servidor
            ServerSocket serverSocket = new ServerSocket(3000);
            System.out.println("Servidor esperando conexión");
            
            while(true){
                // Esperar a que el cliente se conecte
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado al servidor");
                // reset();
        
                // Flujos para enviar y recibir datos
                ObjectOutputStream escribir = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream leer = new ObjectInputStream(socket.getInputStream());


                //LOGICA
                //Recibe parametros inciales para empezar el juego
                MensajeCliente mensaje = (MensajeCliente) leer.readObject();
                System.out.println("Ya recibi el mensaje del cliente para iniciar");

                matriz = generarMatriz(mensaje.getDificultad());
                // System.out.println("Ya generé matriz solucion");

                actualizarMatrizJuguete(new String[]{"0", "0", "X"});
                System.out.println("Matriz solucion generada: ");
                printMatriz(matriz);

                mensajeServidor = new MensajeServidor(matrizJuguete);
                escribir.writeObject(mensajeServidor);
                escribir.flush();

                //Analizamos datos hasta que acabe el juego
                while(mensaje.getEndGame() == 0){
                    // System.out.println("SERVIDOR AGUARDANDO CLICK");
                    System.out.println("mensaje antes de recibir: " + Arrays.toString(mensaje.getCeldaSelected()));
                    mensaje = (MensajeCliente) leer.readObject();

                    System.out.println("SERVIDOR: mensaje recibido: " + Arrays.toString(mensaje.getCeldaSelected()));
                    
                    actualizarMatrizJuguete(mensaje.getCeldaSelected());

                    mensajeServidor = new MensajeServidor(matrizJuguete);
                    escribir.writeObject(mensajeServidor);
                    escribir.flush();
                }

                //Terminó el juego
                System.out.println("Juego terminado en servidor");

            }

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public static void reset(){
        //Cada vez que se conecte un cliente se reinician las variables
        //La idea es que solo se conecte 1 cliente por eso se hace reset

    }

    public static String[][] generarMatriz(int dificultad){
        /* 0 => No hay nada, Número = Numero de bombas cerca, X = BOMBA */

        switch(dificultad){
            case 1:
                matriz = new String[9][9];
                matrizJuguete = new String[9][9];
                numBombas = 10;
            break;
            case 2:
                matriz = new String[16][16];
                matrizJuguete = new String[16][16];
                numBombas = 40;
            break;
            default: //Seria equivalente a la dificultad mas dificil
                matriz = new String[16][30];
                matrizJuguete = new String[16][30];
                numBombas = 99;
            break;
        }

        //Llenar matriz con ceros
        for(int i = 0; i < matriz.length; i++){
            for(int j = 0; j < matriz[i].length; j++){
                matriz[i][j] = "0";
            }
        }

        // System.out.println("Ya llene de ceros");

        HashSet<String> posicionesOcupadas = new HashSet<>();

        int contador = 0;

        while (contador < numBombas) {
            int x = random(0, matriz.length - 1); // Genera fila
            int y = random(0, matriz[0].length - 1); // Genera columna
            String pos = x + "," + y;

            // Verifica si la posición ya está ocupada
            if (!posicionesOcupadas.contains(pos)) {
                matriz[x][y] = "X"; // Coloca bomba
                posicionesOcupadas.add(pos); // Agrega a posiciones ocupadas
                contador++;
                // System.out.println("Bombas listas: " + contador);
            }
        }

        // System.out.println("Ya llene de bombas");

        //Llenamos de números adyacentes a bombas
        for(int i = 0; i < matriz.length; i++){
            for(int j = 0; j < matriz[i].length; j++){
                if(!matriz[i][j].equals("X")){ //Si no es una bomba
                    int cont = 0; //Para contar numero de bombas adyacentes

                    if(estaDentroDeLimitesYHayBomba(matriz, i-1, j)){ //arriba
                        cont++;
                    }
                    if(estaDentroDeLimitesYHayBomba(matriz, i+1, j)){ //abajo
                        cont++;
                    }
                    if(estaDentroDeLimitesYHayBomba(matriz, i, j-1)){ //izquierda
                        cont++;
                    }
                    if(estaDentroDeLimitesYHayBomba(matriz, i, j+1)){ //derecha
                        cont++;
                    }
                    if(estaDentroDeLimitesYHayBomba(matriz, i+1, j-1)){ //arriba izq
                        cont++;
                    }
                    if(estaDentroDeLimitesYHayBomba(matriz, i+1, j+1)){ //arriba der
                        cont++;
                    }
                    if(estaDentroDeLimitesYHayBomba(matriz, i-1, j-1)){ //abajo izq
                        cont++;
                    }
                    if(estaDentroDeLimitesYHayBomba(matriz, i-1, j+1)){ //abajo der
                        cont++;
                    }

                    matriz[i][j] = String.valueOf(cont);
                }
            }
        }
        return matriz;
    }

    public static void actualizarMatrizJuguete(String[] celda){
        if(celda[2].equals("X")){ //Se acaba de crear la matriz de juguete
            for(int i = 0; i < matrizJuguete.length; i++){
                for(int j = 0; j < matrizJuguete[i].length; j++){
                    matrizJuguete[i][j] = "?";
                }
            }
            return;
        }

        int[] celdaa = new int[2];
        for(int i = 0; i < 2; i++){
            celdaa[i] = Integer.parseInt(celda[i]);
        }

        //Comprobemos si hay bomba
        if(matriz[celdaa[0]][celdaa[1]].equals("X")){
            mensajeServidor.setResultado(0); //Perdió
            matrizJuguete[celdaa[0]][celdaa[1]] = "B";
        } else if(matriz[celdaa[0]][celdaa[1]].equals("0")){
            //Se descubren las celdas adyacentes
            descubrirOtrasCeldas(celdaa[0], celdaa[1]);
        } else{ //Si es un numero diferente a cero solo descubrimos esa
            matrizJuguete[celdaa[0]][celdaa[1]] = matriz[celdaa[0]][celdaa[1]];
        }

    }

    public static boolean estaDentroDeLimitesYHayBomba(String[][] matriz, int fila, int columna) {
        return fila >= 0 && fila < matriz.length && columna >= 0 && columna < matriz[0].length && matriz[fila][columna].equals("X");
    }
    public static boolean estaDentroDeLimitesYNOHayBomba(String[][] matriz, int fila, int columna) {
        return fila >= 0 && fila < matriz.length && columna >= 0 && columna < matriz[0].length && !matriz[fila][columna].equals("X");
    }

    public static void descubrirOtrasCeldas(int fila, int columna){
        if(!estaDentroDeLimitesYNOHayBomba(matriz, fila, columna)){
            return;
        }

        //Descubrimos la celda actual
        matrizJuguete[fila][columna] = matriz[fila][columna];

        //Si la celda es "0", descubrimos las celdas adyacentes
        if (matriz[fila][columna].equals("0")) {
            // Recorremos todas las celdas adyacentes (incluyendo diagonales)
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    // Saltamos la celda actual (i == 0 && j == 0)
                    if (i == 0 && j == 0) {
                        continue;
                    }
    
                    int nuevaFila = fila + i;
                    int nuevaColumna = columna + j;
    
                    // Llamada recursiva para descubrir la celda adyacente
                    if (nuevaFila >= 0 && nuevaFila < matriz.length && nuevaColumna >= 0 && nuevaColumna < matriz[0].length) {
                        descubrirOtrasCeldas(nuevaFila, nuevaColumna);
                    }
                }
            }
        }
    }

    //Utilities
    public static int random(int min, int max){ //Genera numero random en cierto rango
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static void printMatriz(String[][] matriz){
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print(matriz[i][j] + "  ");
            }
            System.out.println();
        }
    }
}
