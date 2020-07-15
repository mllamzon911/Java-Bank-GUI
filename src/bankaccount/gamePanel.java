package bankaccount;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
    
public class gamePanel extends JPanel 
{
    /* New Window width and height */
    public static final int WIDTH = 490;
    public static final int HEIGHT = 350;
    public static final int MAX_SCORE = 30;
    
    /* Variables for images, its height, width, and positions */
    private ImageIcon starShip, plasmaShot; 
    private int xMoveShip = 225;
    private int plasmaYPos = HEIGHT - 100;
    private int plasmaXPos = xMoveShip + 10;
    private int plasmaWidth = 5, plasmaHeight = 5;
    private int MOVEX, MOVEY;
    private int rockLayoutY = 14, rockLayoutX = 4;
    
    /* keep track of score */
    public int score, plasmaShots = 0;
    public boolean fired = false, betted = false;
    public double betValue = 0;
    private gameLayout rocks; 
    
    private Timer timer;
    private final int DELAY = 20;
    
    public gamePanel ()
    {
        
        timer = new Timer(DELAY, new shipKeyListener());
        addKeyListener(new shipKeyListener());
        
        /* create windows, create "rocks", and add images */
        starShip = new ImageIcon("galaga-ship.jpg");
        plasmaShot = new ImageIcon("plasma.png");
        rocks = new gameLayout(rockLayoutX,rockLayoutY);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);    
        
        /* Betting option for the current user's bank account */
        int option = JOptionPane.showConfirmDialog(null, "Would you like to gamble money?", "Play Rock Shooter!", JOptionPane.YES_NO_OPTION); 
        
        if(option == JOptionPane.YES_OPTION) {
            do {
            String betValueStr = JOptionPane.showInputDialog("Enter an amount between $1.00 and $5.00");
            betValue = Double.parseDouble(betValueStr);
            } while(betValue < 1 || betValue > 5);
            
            try {   
            betValue = BankAccount.betAmount(betValue); 
                if (betValue == 0)  
                    betted = false;
                else
                    betted = true;
            }
            catch(NullPointerException npex) {
                JOptionPane.showMessageDialog(null, "Error, please find or add an account to bet on. \n" + "However, you can still play the game!");
                betted = false;
            }  
        }
        timer.start();      
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        /* Draw red blocks on the window */
        g.setColor(Color.RED);
        rocks.drawRocks(g);
        
        /* Insert Galaga ship */
        starShip.paintIcon(this, g, xMoveShip, HEIGHT - 40);
        g.setColor(Color.CYAN);
        
        /* rendering/updating the score and shots fired */
        if(fired == true) {
            plasmaShot.paintIcon(this, g, plasmaXPos, plasmaYPos);  
            g.setColor(Color.white);
            g.setFont(new Font("Serif", Font.BOLD, 24));
            g.drawString("Score: "+score, 0, HEIGHT - 20);
            g.drawString("Plasma Shots Fire: " + plasmaShots, 0, HEIGHT - 40);
            g.dispose();
        } 
        
        /* Add game controls on the newly created window */
        g.setFont(new Font("Serif", Font.BOLD, 18));
        g.drawString("-----Controls-----", 10 , HEIGHT-160);
        g.drawString("Left Arrow Key Move Left '<'", 10 , HEIGHT-140);
        g.drawString("Right Arrow Key Move Right '>'",10, HEIGHT-120);
        g.drawString("Up Arrow Key to Start '^'",10, HEIGHT-100);
        g.drawString("Destroy 30 red rocks to win! Ammo - 15!",10, HEIGHT-80); 
        g.drawString("----Must Finish the game----",10, HEIGHT-60);
    }
    
    private class shipKeyListener implements KeyListener, ActionListener {
        public void actionPerformed(ActionEvent event) {
            BankAccount.gameFrame.setVisible(true);
            
            /* Initialize variables */
            String message = "";
            int rXPos, rYPos, rWidth, rHeight;
            DecimalFormat fmt = new DecimalFormat("$0.00");
            
            /* Positions and Movement */
            xMoveShip = xMoveShip + MOVEX;
            plasmaXPos = plasmaXPos + MOVEX;
            plasmaYPos = plasmaYPos + MOVEY;
            if(xMoveShip < 0 || plasmaXPos< 0) {
                xMoveShip = 1;
                plasmaXPos = xMoveShip + 12;
            }
            if(xMoveShip <= 500 && xMoveShip >=460) {
                xMoveShip = 460;
                plasmaXPos = xMoveShip + 12;
            }
            if(fired) {
                for (int i = 0; i < rocks.rockLayout.length; i++){
                    for (int j = 0; i <rocks.rockLayout[0].length; j++) {   
                        if(rocks.rockLayout[i][j] > 0) {
                            rXPos = j * rocks.rockWidth;
                            rYPos = i * rocks.rockHeight;
                            rWidth = rocks.rockWidth;
                            rHeight = rocks.rockHeight;
                    
                            /* image positions */
                            Rectangle rockRect = new Rectangle(rXPos, rYPos, rWidth, rHeight); 
                            Rectangle plasmaRect = new Rectangle(plasmaXPos, plasmaYPos, plasmaWidth, plasmaHeight);
                            /* Collision between rock and plasma */
                            if (plasmaRect.intersects(rockRect)) {
                                rocks.setValue(0, i, j);            // causes the intersected rectangle to to disappear when set to 0
                                score++;                            // increment score
                            }
                        }
                        if(j == 13) {
                            break;
                        } 
                    } 
                }
                /* Resets the plasma position, continues to fire */
                if(plasmaYPos < 0) {
                    plasmaYPos = HEIGHT - 100;
                    plasmaShots++;
                }
            }
            
            /* All rocks are destroyed */
            if(score == MAX_SCORE) {      
                timer.stop();
                BankAccount.gameFrame.dispose();
                if (betted == true) {   
                    BankAccount.processCredit(betValue);
                    message = "Here's your reward for playing! \n \n"
                        + "-------------"+ fmt.format((betValue*2)) + "------------- \n \n"
                        + "Adding to your bank account... \n \n";   // for bank account to give user $1.00 for playing
                }
                else {
                    message = "Thanks for playing! \n \n";
                }
                JOptionPane.showMessageDialog(null, message);
            }
            
            /* If the player was unable to destroy all rocks */
            if(score < MAX_SCORE && plasmaShots == 15)
            {
                timer.stop();
                BankAccount.gameFrame.dispose();
                if(betted == true) {
                    message = "You lost! \n \n"
                            + "-------------" + fmt.format((betValue*2)) + "------------- \n \n"
                            + "Please try again next time! \n \n";
                    BankAccount.processDebit(betValue);  
                }
                else {
                message = "           You lose!        \n \n"
                        + "Please try again next time! \n \n";
                }
                JOptionPane.showMessageDialog(null, message);
            }
            repaint();        
        }
        
        /* Control user's input */
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:  
                MOVEX = -5;
                break;
            case KeyEvent.VK_RIGHT:
                MOVEX = 5;
                break;
            case KeyEvent.VK_UP:
                MOVEY = -25;
                fired = true;
                break;
            }
        }
        
        public void keyTyped(KeyEvent e) { }
        
        /* stop moving the image when the left/right key is released */
        public void keyReleased(KeyEvent e) { MOVEX = 0; }
    }
}
