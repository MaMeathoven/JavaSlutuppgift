import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    // Skärmhöjd och bredd - standard 600 varje
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;

    // Storlek på rutor samt utrräkning på hur många
    static final int UNIT_SIZE = 25; // standard 25
    static final int UNIT_REDUCTION = 4;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;

    // Lägre nummer = snabbare spel (75)//// Tid mellan tick i ms
    static final int DELAY = 75;

    // Arrayer som håller alla kordinater på spelplanen
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    // Startlängd på snake
    int startingBodyParts = 6;
    int bodyParts; // standard 6
    int applesEaten = 0; // Används för att räkna poäng
    int highScore = 0; // Sparar högsta score under en instans

    // Kordinater för äpplen
    int appleX;
    int appleY;

    // Startriktning R=RIGHT L=LEFT U=UP D=DOWN
    char direction = 'R';

    boolean running = false;
    boolean paused = false;
    boolean restarting = false;

    String scoreString;

    Timer timer=new Timer(DELAY, this);
    Random random;


    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        // TODO - IMPLEMENTERA EN FUNKTION FÖR ATT KONTINUERLIGT BYTA FÄRG PÅ BAKGRUND
        this.setBackground(Color.black); // standard Color.black
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        startGame();
    }

    public void startGame() {
        bodyParts = startingBodyParts;
        applesEaten = 0;
        newApple();

        running = true;
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {


        if (running) {
            /*
             * for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
             * g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
             * g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
             * }
             */

            // äpple - standard Color.red
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                // huvud - standard: Color.green
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i] + UNIT_REDUCTION, y[i] + UNIT_REDUCTION, UNIT_SIZE - (2 * UNIT_REDUCTION),
                            UNIT_SIZE - (2 * UNIT_REDUCTION));
                }

                // resten av kroppen - standard new Color(45, 100, 0)
                else {
                    g.setColor(new Color(45, 100, 0));
                    g.fillRect(x[i] + UNIT_REDUCTION, y[i] + UNIT_REDUCTION, UNIT_SIZE - (2 * UNIT_REDUCTION),
                            UNIT_SIZE - (2 * UNIT_REDUCTION));

                }
            }

            if (!paused) {
                scoreString = ("Score: " + Integer.toString(applesEaten));
            } else if (paused) {
                scoreString = "PAUSED";
            }


            g.setColor(Color.gray);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString(scoreString, (SCREEN_WIDTH - metrics.stringWidth(scoreString)) / 2, g.getFont().getSize()); // Mellanrum
                                                                                                           // mellan
                                                                                                           // texten
        } else{
            gameOver(g);
        }

    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {

        if (!restarting && !paused) {

            for (int i = bodyParts; i > 0; i--) {
                x[i] = x[i - 1];
                y[i] = y[i - 1];

            }

            switch (direction) {
                case 'U':
                    y[0] = y[0] - UNIT_SIZE;
                    break;

                case 'D':
                    y[0] = y[0] + UNIT_SIZE;
                    break;

                case 'L':
                    x[0] = x[0] - UNIT_SIZE;
                    break;

                case 'R':
                    x[0] = x[0] + UNIT_SIZE;
                    break;

            }
        }
        // TODO FIXA RESTARTING

        /*
         * else if(restarting){
         * for(int i = bodyParts; i>0;i--){
         * if(i != 0){
         * x[i] = 0;
         * y[i] = 0;
         * }
         * else if(i ==0){
         * x[i] = 0;
         * y[i] = 0;
         * }
         * }
         * 
         * 
         * restarting = false;
         * }
         */
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            // bodyParts++;
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    // Funktion för att kolla efter kollisioner med hjälp av x/y[0] (huvudet)
    public void checkCollisions() {

        boolean collision = false;

        // KOllar om huvudet har samma kordinater som någon av de andra kroppsdelarna
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                collision = true;
            }
        }

        // Kollar om huvudets kordinater ligger utanför vänster kant
        if (x[0] < 0) {
            collision = true;
        }

        // Kollar om huvudets kordinater ligger utanför höger kant
        if (x[0] > SCREEN_WIDTH - UNIT_SIZE) {
            collision = true;
        }

        // Kollar om huvudets kordinater är utanför övre kant
        if (y[0] < 0) {
            collision = true;
        }

        // Kollar om huvudets kordinater är utanför nedre kant
        if (y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
            collision = true;
        }

        // avslutar timer om "Running" inte är true
        if (collision) {
            running = false;
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {

        // Gamer Over text
        g.setColor(Color.red);
        // TODO - IMPORTERA CUSTOM BLOCKY FONT
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        // Skriver ut poäng med mindre font och annan färg
        g.setColor(Color.gray);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score" + applesEaten)) / 2,
                (SCREEN_HEIGHT / 2) + metrics.getHeight() + 5); // Mellanrum mellan texten
        g.drawString("High Score: " + highScore, (SCREEN_WIDTH - metrics.stringWidth("High Score: " + highScore)) / 2,
                (SCREEN_HEIGHT / 2) + (metrics.getHeight() * 2) + 5); // Mellanrum mellan texten

    }

    public void restartGame() {
        bodyParts = startingBodyParts;

        // Cyclar igenom alla kroppsdelar och placerar dom på 0,0. VIktigt att börja med
        // huvudet först eftersom att alla andra följer
        for (int i = 0; i < bodyParts; i++) {

            x[i] = 0;
            y[i] = 0;

        }

        //Sätter huvudet 1 steg före alla andra kroppsdelar
        x[0] = UNIT_SIZE;
        y[0] = 0;
        direction = 'R';


        if (applesEaten >= highScore) {
            highScore = applesEaten;
        }

        running = true;
        restarting = false;

        applesEaten = 0;
        newApple();

        timer = new Timer(DELAY, this);
        timer.start();
        
    }



    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        repaint();
        if (running && !restarting) {
            move();
        }
        checkCollisions();
            checkApple();
        repaint();

    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT, KeyEvent.VK_D:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP, KeyEvent.VK_W:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN, KeyEvent.VK_S:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;

                case KeyEvent.VK_R:
                    timer.stop();
                    restarting = true;
                    restartGame();
                    restarting = false;
                    break;

                // TODO - LÖS SÅ ATT "PAUSED" VISAS NÄR SPELET ÄR PAUSAT
                case KeyEvent.VK_SPACE:
                    if (!paused) {
                        paused = true;
                    } else if (paused) {
                        paused = false;
                    }
                    break;

            }
        }
    }
}
