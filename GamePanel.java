import java.awt.*;
import java.awt.event.*;
import java.lang.constant.DirectMethodHandleDesc;

import javax.swing.*;
import java.util.Random;

import javax.swing.Jpanel;

public class GamePanel extends JPanel implements ActionListener {
    //Skärmhöjd och bredd - standard 600 varje
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;

    //Storlek på rutor samt utrräkning på hur många
    static final int UNIT_SIZE = 25; //standard 25
    static final int UNIT_REDUCTION = 4;
    static final int GAME_UNITS=(SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;

    //Lägre nummer = snabbare spel (75)//// Tid mellan tick i ms
    static final int DELAY = 75;

    //Arrayer som håller alla kordinater på spelplanen
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    //Startlängd på snake
    int bodyParts = 6; //standard 6
    int applesEaten = 0;

    //Kordinater för äpplen
    int appleX;
    int appleY;

    //Startriktning R=RIGHT L=LEFT U=UP  D=DOWN
    char direction = 'R';

    boolean running = false;

    Timer timer;
    Random random;


    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
         
        //TODO - IMPLEMENTERA EN FUNKTION FÖR ATT KONTINUERLIGT BYTA FÄRG PÅ BAKGRUND
        this.setBackground(Color.black); //standard Color.black
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        startGame();
    }

    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();        
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        /*for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
            g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
        }*/

        //äpple - standard Color.red
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        for (int i = 0; i < bodyParts; i++) {
            //huvud - standard röd
            if(i== 0){
                g.setColor(Color.red);
                g.fillRect(x[i] + UNIT_REDUCTION, y[i] + UNIT_REDUCTION, UNIT_SIZE-(2*UNIT_REDUCTION), UNIT_SIZE-(2*UNIT_REDUCTION));
            }

            //resten av kroppen - standard grön
            else{
                g.setColor(Color.green);
                g.fillRect(x[i] + UNIT_REDUCTION, y[i] + UNIT_REDUCTION, UNIT_SIZE - (2*UNIT_REDUCTION), UNIT_SIZE - (2*UNIT_REDUCTION));

            }
        }


    }

    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    public void move(){
        for(int i = bodyParts; i>0;i--){
            x[i] = x[i-1];
            y[i] = y[i-1];

        }

        switch(direction){
        case 'U':
            y[0] = y[0]-UNIT_SIZE;
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

    public void checkApple(){
        if((x[0] == appleX)&&(y[0] == appleY)){
            //bodyParts++;
            bodyParts ++;
            newApple();
        }
    }

    //Funktion för att kolla efter kollisioner med hjälp av x/y[0] (huvudet)
    public void checkCollisions(){

        //KOllar om huvudet har samma kordinater som någon av de andra kroppsdelarna
        for(int i=bodyParts; i>0; i--){
            if((x[0] == x[i])&&(y[0] == y[i])){
                running = false;
            }
        }

        //Kollar om huvudets kordinater ligger utanför vänster kant
        if(x[0] < 0){
            running = false;
        }

        //Kollar om huvudets kordinater ligger utanför höger kant
        if(x[0] > SCREEN_WIDTH - UNIT_SIZE){
            running = false;
        }

        //Kollar om huvudets kordinater är utanför övre kant
        if(y[0] < 0){
            running = false;
        }

        //Kollar om huvudets kordinater är utanför nedre kant
        if(y[0] > SCREEN_HEIGHT - UNIT_SIZE){
            running = false;
        }

        //avslutar timer om "Running" inte är true
        if(!running){
            timer.stop();
        }
    }

    public void gameOver(Graphics g){

    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if(running){
            move();
            checkApple();
            checkCollisions();
        }
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

            }
        }
    }

}
