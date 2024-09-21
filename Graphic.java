import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Graphic extends JFrame implements ActionListener {

    //>>>PARAMETROS PARA AJUSTAR<<<
    private int tamañoCelda = 10;
    private int filas = 0;
    private int columnas = 0;
    private int posXCampo = 300;
    private int posYCampo = 70;

    // Matriz para almacenar los valores de las celdas
    public String[][] matriz;

     // Imágenes para las celdas
     private Image bombImage;
     private Image flagImage;

    //>>>FIN DE PARAMETROS PARA AJUSTAR<<<

    private Button button1; //Dificultad facil
    private Button button2; //Dificultad intermedio
    private Button button3; //Dificultad dificil

    public Graphic() { // Constructor
        // Carga las imágenes
        try {
            bombImage = ImageIO.read(new File("img/bomb.png"));
            flagImage = ImageIO.read(new File("img/flag.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        // Configura la ventana
        setTitle("BuscaMinas - DaniV & JayimR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla

        // Crea un panel personalizado para dibujar todo
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarCampo(g);
            }
        };

        // Null para posicionar elementos manualmente
        panel.setLayout(null);

        // Crea botones para diferentes niveles de dificultad
        button1 = new Button("Baby");
        button1.addActionListener(this);
        button1.setBounds(38, 160, 140, 30);
        panel.add(button1);

        button2 = new Button("Normal");
        button2.addActionListener(this);
        button2.setBounds(38, 200, 140, 30);
        panel.add(button2);

        button3 = new Button("Insane");
        button3.addActionListener(this);
        button3.setBounds(38, 240, 140, 30);
        panel.add(button3);

        // Agrega el panel a la ventana
        getContentPane().add(panel);

        // Etiquetas
        JLabel titleLabel = new JLabel("BuscaMinas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(400, 0, 300, 30);
        panel.add(titleLabel);

        // Método para leer cuando se da clic en una celda
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int columna = (x - posXCampo) / tamañoCelda;
                int fila = (y - posYCampo) / tamañoCelda;
                String modo;

                // Verifica si las coordenadas están dentro del tablero
                if (columna >= 0 && columna < columnas && fila >= 0 && fila < filas) {
                    if (SwingUtilities.isLeftMouseButton(e)) { // DISCOVER
                        modo = "D";
                        Cliente.setCeldaSelected(String.valueOf(fila), String.valueOf(columna), modo);
                    } else if (SwingUtilities.isRightMouseButton(e)) { // FLAG
                        modo = "F";
                        Cliente.setCeldaSelected(String.valueOf(fila), String.valueOf(columna), modo);
                    }
                }
            }
        });
    }

    public void dibujarCampo(Graphics g) {
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                int x = posXCampo + columna * tamañoCelda;
                int y = posYCampo + fila * tamañoCelda;
    
                // Color base de la celda (gris claro)
                g.setColor(new Color(192, 192, 192));
                g.fillRect(x, y, tamañoCelda, tamañoCelda);
    
                // Sombra oscura en el borde inferior y derecho
                g.setColor(new Color(128, 128, 128));
                g.fillRect(x + tamañoCelda - 4, y, 4, tamañoCelda);  // Sombra derecha
                g.fillRect(x, y + tamañoCelda - 4, tamañoCelda, 4);  // Sombra inferior
    
                // Brillo en el borde superior e izquierdo
                g.setColor(Color.WHITE);
                g.fillRect(x, y, tamañoCelda - 4, 4);  // Brillo superior
                g.fillRect(x, y, 4, tamañoCelda - 4);  // Brillo izquierdo
    
                // Dibuja el valor de la matriz
                if (matriz != null && matriz[fila][columna] != null) {
                    if (matriz[fila][columna].equals("B")) {
                        g.drawImage(bombImage, x, y, tamañoCelda, tamañoCelda, null);
                    } else if (matriz[fila][columna].equals("F")) {
                        g.drawImage(flagImage, x, y, tamañoCelda, tamañoCelda, null);
                    } else if (matriz[fila][columna].equals("0")) {
                        // Sombra en el borde superior e izquierdo
                        g.setColor(new Color(128, 128, 128)); // Color más oscuro para la sombra
                        g.fillRect(x, y, tamañoCelda - 4, 4);  // Sombra superior
                        g.fillRect(x, y, 4, tamañoCelda - 4);  // Sombra izquierda
                    } 
                    else {
                        // Dibuja el número en la celda
                        g.setColor(Color.BLACK);
                        g.setFont(new Font("Arial", Font.BOLD, 30));
                        g.drawString(matriz[fila][columna], x + tamañoCelda / 4, y + (3 * tamañoCelda) / 4);
                    }
                }
            }
        }
    }
    

    // Método para leer botones
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == button1) {
            Cliente.setDificultad(1);
            tamañoCelda = 60;
            filas = 9;
            columnas = 9;
            posXCampo = 350;
            posYCampo = 100;
            matriz = new String[filas][columnas]; // Inicializa la matriz
            repaint();
        } else if (source == button2) {
            Cliente.setDificultad(2);
            tamañoCelda = 40;
            filas = 16;
            columnas = 16;
            posXCampo = 310;
            posYCampo = 65;
            matriz = new String[filas][columnas]; // Inicializa la matriz
            repaint();
        } else if (source == button3) {
            Cliente.setDificultad(3);
            tamañoCelda = 30;
            filas = 16;
            columnas = 24;
            posXCampo = 220;
            posYCampo = 120;
            matriz = new String[filas][columnas]; // Inicializa la matriz
            repaint();
        }
    }
}
