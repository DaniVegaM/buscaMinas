import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class Graphic extends JFrame implements ActionListener{

    //>>>PARAMETROS PARA AJUSTAR<<<
    private int tamañoCelda = 10;
    private int filas = 0;
    private int columnas = 0;

    private int posXCampo = 300;
    private int posYCampo = 70;

    //>>>FIN DE PARAMETROS PARA AJUSTAR<<<

    private Button button1; //Dificultad facil
    private Button button2; //Dificultad intermedio
    private Button button3; //Dificultad dificil

    public Graphic(){ //Constructor

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

                // Métodos para dibujar todo
                dibujarCampo(g);
            }
        };

        //Null para posicionar elementos manualmente
        panel.setLayout(null);

        //---> Layout de entorno grafico <---

        // Crea un botón para ingresar patrón
        button1 = new Button("Baby");
        button1.addActionListener(this); // Registra la clase actual como el listener del botón
        button1.setBounds(38, 160, 140, 30); // Posición y tamaño del botón
        panel.add(button1); // Agrega el botón al panel

        // Crea un botón para ingresar patrón
        button2 = new Button("Normal");
        button2.addActionListener(this); // Registra la clase actual como el listener del botón
        button2.setBounds(38, 200, 140, 30); // Posición y tamaño del botón
        panel.add(button2); // Agrega el botón al panel
        
        // Crea un botón para ingresar patrón
        button3 = new Button("Insane");
        button3.addActionListener(this); // Registra la clase actual como el listener del botón
        button3.setBounds(38, 240, 140, 30); // Posición y tamaño del botón
        panel.add(button3); // Agrega el botón al panel

        // Agrega el panel a la ventana
        getContentPane().add(panel);


        //---> Etiquetas <---

        // Etiqueta del título
        JLabel titleLabel = new JLabel("BuscaMinas");
        JLabel titleLabel2 = new JLabel("Select the difficulty level");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Personaliza la fuente
        titleLabel2.setFont(new Font("Arial", Font.PLAIN, 16)); // Personaliza la fuente
        titleLabel.setBounds(400, 0, 300, 30); // Posición y tamaño de la etiqueta
        titleLabel2.setBounds(30, 100, 300, 30);
        panel.add(titleLabel);
        panel.add(titleLabel2);

        // Etiqueta del subtítulo
        JLabel subtitleLabel = new JLabel("Daniel Vega Miranda y Jayim Reyes Reyes");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Personaliza la fuente
        subtitleLabel.setBounds(300, 45, 400, 30); // Posición y tamaño de la etiqueta
        panel.add(subtitleLabel);

        
        //Método para leer cuando se de click en una celda
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Obtén las coordenadas del clic en píxeles
                int x = e.getX();
                int y = e.getY();

                // Convierte las coordenadas de píxeles en coordenadas de celdas
                int columna = (x - posXCampo) / tamañoCelda;
                int fila = (y - posYCampo) / tamañoCelda;
                String modo;

                // Verifica si las coordenadas están dentro del tablero
                if (columna >= 0 && columna < columnas && fila >= 0 && fila < filas) {
                    if (SwingUtilities.isLeftMouseButton(e)) { //DISCOVER
                        modo = "D";
                        Cliente.setCeldaSelected(String.valueOf(fila), String.valueOf(columna), modo);
                    } else if (SwingUtilities.isRightMouseButton(e)) { //FLAG
                        modo = "F";
                        Cliente.setCeldaSelected(String.valueOf(fila), String.valueOf(columna), modo);
                    }
                }
            }
        });

    }

    public void dibujarCampo(Graphics g){
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
            }
        }
    }

    //Metodo para leer botones
    @Override
    public void actionPerformed(ActionEvent e){
        Object source = e.getSource();
        if (source == button1) {
            Cliente.setDificultad(1);
            tamañoCelda = 60;
            filas = 9;
            columnas = 9;
            posXCampo = 350;
            posYCampo = 140;
            repaint();
        } else if (source == button2) {
            Cliente.setDificultad(2);
            tamañoCelda = 40;
            filas = 16;
            columnas = 16;
            posXCampo = 310;
            posYCampo = 85;
            repaint();
        } else if (source == button3) {
            Cliente.setDificultad(3);
            tamañoCelda = 30;
            filas = 16;
            columnas = 24;
            posXCampo = 220;
            posYCampo = 120;
            repaint();
        }
    }
}
