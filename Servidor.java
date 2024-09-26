
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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

    private static int numBanderasAcertadas = 0;
    private static long mejorTiempo = Long.MAX_VALUE; // Variable para almacenar el mejor tiempo
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
                escribir.reset();
                escribir.writeObject(mensajeServidor);
                escribir.flush();

                //Medimos tiempo al empezar a jugar
                long tiempoInicio = System.currentTimeMillis();

                //Analizamos datos hasta que acabe el juego
                while(true){
                    // System.out.println("SERVIDOR AGUARDANDO CLICK");
                    System.out.println("mensaje antes de recibir: " + Arrays.toString(mensaje.getCeldaSelected()));
                    mensaje = (MensajeCliente) leer.readObject();

                    if(mensaje.getEndGame() == 1){
                        break;
                    }

                    System.out.println("SERVIDOR: mensaje recibido: " + Arrays.toString(mensaje.getCeldaSelected()));
                    
                    actualizarMatrizJuguete(mensaje.getCeldaSelected());

                    System.out.println("Ya actualize, regreso esta matriz actualizada");
                    printMatriz(matrizJuguete);

                    mensajeServidor.setMatriz(matrizJuguete);
                    escribir.reset();
                    escribir.writeObject(mensajeServidor);
                    escribir.flush();
                }

                //Terminó el juego
                long tiempoFin = System.currentTimeMillis();
                long tiempoTotal = (tiempoFin - tiempoInicio) / 1000; // Tiempo total en segundos

                mensajeServidor.setTiempoJugador((int)tiempoTotal);

                //Terminó el juego
                System.out.println("Juego terminado en servidor");

                if (mensajeServidor.getEndGame() == 1 && numBanderasAcertadas > 0) { // Asegúrate de que el jugador ganó
                    // Compara el tiempo del jugador con el mejor tiempo
                    if (tiempoTotal < mejorTiempo) {
                        mejorTiempo = tiempoTotal; // Actualiza el mejor tiempo
                        guardarMejorTiempo(mejorTiempo); // Guarda el nuevo mejor tiempo
                    }
                }
                // Leer el tiempo mínimo del archivo
                int tiempoMinimo = obtenerTiempoMinimo("./resultados.txt");
                mensajeServidor.setTiempoTotal(tiempoMinimo);


                //Mostramos score y tiempo
                String scoreJugador = "Tu score final es de: " + numBanderasAcertadas * 100;
                String tiempoJugador = "Terminaste en un tiempo de: " + tiempoTotal + " segundos";
                String scoreTotal = "Score total: " + numBombas * 100;

                //Envio resultados
                String[][] lastMatrix = pintarBombasEnUltimaMatriz(matriz);
                mensajeServidor.setMatriz(lastMatrix);
                escribir.reset();
                escribir.writeObject(mensajeServidor);

                System.out.println(scoreJugador);
                System.out.println(tiempoJugador);
                System.out.println(scoreTotal);
            }

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
    private static int obtenerTiempoMinimo(String rutaArchivo) {
        int tiempoMinimo = Integer.MAX_VALUE; // Inicializar con el valor máximo posible
    
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(" ");
                // Verificamos que haya al menos 2 partes en la línea
                for (int i = 0; i < partes.length; i++) {
                    if (partes[i].startsWith("Tiempo:") && i + 1 < partes.length) {
                        int tiempo = Integer.parseInt(partes[i + 1].trim());
                        if (tiempo < tiempoMinimo) {
                            tiempoMinimo = tiempo;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return tiempoMinimo == Integer.MAX_VALUE ? 999 : tiempoMinimo; // Retorna 999 si no se encontró tiempo
    }
    
    private static void guardarMejorTiempo(long tiempo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("mejorTiempo.txt"))) {
            writer.write("Mejor tiempo: " + tiempo + " segundos");
        } catch (IOException e) {
            System.out.println("Error al guardar el mejor tiempo: " + e.getMessage());
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
        //NOTA: B = Bomba F= FLAG 
        if(matriz[celdaa[0]][celdaa[1]].equals("X")){
            if(celda[2].equals("D")){
                matrizJuguete[celdaa[0]][celdaa[1]] = "B";
                mensajeServidor.setScoreJugador(numBanderasAcertadas); //Perdió
                mensajeServidor.setScoreTotal(numBombas);
                mensajeServidor.setEndGame(1);
            } else if(celda[2].equals("F")){
                matrizJuguete[celdaa[0]][celdaa[1]] = "F";
                numBanderasAcertadas++;
            }
        } else if(matriz[celdaa[0]][celdaa[1]].equals("0")){
            //Se descubren las celdas adyacentes
            if(celda[2].equals("D")){
                descubrirOtrasCeldas(celdaa[0], celdaa[1]);
            } else if(celda[2].equals("F")){
                matrizJuguete[celdaa[0]][celdaa[1]] = "F";
            }
        } else{ //Si es un numero diferente a cero solo descubrimos esa
            if(celda[2].equals("D")){
                matrizJuguete[celdaa[0]][celdaa[1]] = matriz[celdaa[0]][celdaa[1]];
            } else{ //Si es bandera
                if(matrizJuguete[celdaa[0]][celdaa[1]].equals("?")){
                    matrizJuguete[celdaa[0]][celdaa[1]] = "F";
                }
            }
        }

    }

    public static boolean estaDentroDeLimitesYHayBomba(String[][] matriz, int fila, int columna) {
        return fila >= 0 && fila < matriz.length && columna >= 0 && columna < matriz[0].length && matriz[fila][columna].equals("X");
    }
    
    public static boolean estaDentroDeLimitesYNOHayBomba(String[][] matriz, int fila, int columna) {
        return fila >= 0 && fila < matriz.length && columna >= 0 && columna < matriz[0].length && !matriz[fila][columna].equals("X");
    }

    public static void descubrirOtrasCeldas(int fila, int columna){
        if (!estaDentroDeLimitesYNOHayBomba(matriz, fila, columna) || !matrizJuguete[fila][columna].equals("?")) {
        return;
    }

    // Descubrimos la celda actual
    matrizJuguete[fila][columna] = matriz[fila][columna];

    // Si la celda es "0", descubrimos las celdas adyacentes
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
                descubrirOtrasCeldas(nuevaFila, nuevaColumna);
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

    public static String[][] pintarBombasEnUltimaMatriz(String[][] matriz){
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if(matriz[i][j].equals("X")){
                    matrizJuguete[i][j] = "B";
                }
            }
        }
        return matrizJuguete;
    }
}
