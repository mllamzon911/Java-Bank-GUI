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
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

public class gamePanel extends JPanel 
{
    public static final int WIDTH = 490;
    public static final int HEIGHT = 350;
    private ImageIcon starShip, plasmaShot; // images 
    private Timer timer;
    private final int DELAY = 20;
    private int xMoveShip = 225;
    private int plasmaYPos = HEIGHT - 100;
    private int plasmaXPos = xMoveShip + 10;
    private int plasmaWidth = 5, plasmaHeight = 5;
    private int MOVEX, MOVEY;
    private int rockLayoutY = 14, rockLayoutX = 4;
    public int score, plasmaShots = 0;
    public boolean fired = false, betted = false;  // to start the game, user must fire ship at least once
    public double betValue = 0;
    private gameLayout rocks; 
    
    public gamePanel ()
    {
        boolean done = false;
        timer = new Timer(DELAY, new shipKeyListener());
        addKeyListener(new shipKeyListener());
        starShip = new ImageIcon("galaga-ship.jpg");
        plasmaShot = new ImageIcon("plasma.png");
        rocks = new gameLayout(rockLayoutX,rockLayoutY);     // creates a "grid" of rectangles called from rockLayout class
       
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);    
        int option = JOptionPane.showConfirmDialog(null, "Would you like to gamble money?", "Play Rock Shooter!", 
                                                               JOptionPane.YES_NO_OPTION); // if user wants to save or not when transactions are added 
        if(option == JOptionPane.YES_OPTION)
        {
            do{
            String betValueStr = JOptionPane.showInputDialog("Enter an amount between $1.00 and $5.00");
            betValue = Double.parseDouble(betValueStr);
            }while(betValue < 1 || betValue > 5);

            try
            {   
            betValue = BankAccount.betAmount(betValue); // sets the betValue to the returned value being 0, or an amount between 1 and 5
                if (betValue == 0)  // if the balance of the user is less the 1, betted = false since they had no money to bet
                    betted = false;
                else
                    betted = true;
            }
            catch(NullPointerException npex)
            {
            JOptionPane.showMessageDialog(null, "Error, please find or add an account to bet on. \n"
                                              + "However, you can still play the game!");
            betted = false;
            }
            
        }
        timer.start();      
    }
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        g.setColor(Color.RED);
        rocks.drawRocks(g);
        starShip.paintIcon(this, g, xMoveShip, HEIGHT - 40);
        g.setColor(Color.CYAN);
        if(fired == true)
        {
            plasmaShot.paintIcon(this, g, plasmaXPos, plasmaYPos);  // places icon in jpanel
            g.setColor(Color.white);
            g.setFont(new Font("Serif", Font.BOLD, 24));
            g.drawString("Score: "+score, 0, HEIGHT - 20);          // keeps track of user's score
            g.drawString("Plasma Shots Fire: " + plasmaShots, 0, HEIGHT - 40);
            g.dispose();    // removes/disposes the string of controls below
        }   
        g.setFont(new Font("Serif", Font.BOLD, 18));
        g.drawString("-----Controls-----", 10 , HEIGHT-160);
        g.drawString("Left Arrow Key Move Left '<'", 10 , HEIGHT-140);
        g.drawString("Right Arrow Key Move Right '>'",10, HEIGHT-120);
        g.drawString("Up Arrow Key to Start '^'",10, HEIGHT-100);
        g.drawString("Destroy 30 red rocks to win! Ammo - 15!",10, HEIGHT-80); 
        g.drawString("----Must Finish the game----",10, HEIGHT-60);
    }
    
    public void startSound()
    {
        AudioPlayer MGP = AudioPlayer.player;           // playing a wav file
        AudioStream BGM;
        AudioData MD;
        AudioDataStream sound = null;
        
        try
        {
        BGM = new AudioStream(new FileInputStream("/Users/marvinllamzon/NetBeansProjects/BankAccount/src/bankaccount/PewPew.wav")); //from macbook files
        MD = BGM.getData();
        sound = new AudioDataStream(MD);
        }
        catch(IOException ie)
        {
            System.err.print(ie);
        }
        MGP.start(sound);
    }
    private class shipKeyListener implements KeyListener, ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            String message = "";
            int rXPos, rYPos, rWidth, rHeight;
            DecimalFormat fmt = new DecimalFormat("$0.00");       
            xMoveShip = xMoveShip + MOVEX;
            plasmaXPos = plasmaXPos + MOVEX;
            plasmaYPos = plasmaYPos + MOVEY;
            if(xMoveShip < 0 || plasmaXPos< 0)
            {
                xMoveShip = 1;
                plasmaXPos = xMoveShip + 12;
            }
            if(xMoveShip <= 500 && xMoveShip >=460)
            {
                xMoveShip = 460;
                plasmaXPos = xMoveShip + 12;
            }
            if(fired)
            {
            for (int i = 0; i < rocks.rockLayout.length; i++)
            {
                for (int j = 0; i <rocks.rockLayout[0].length; j++)
                {   
                    if(rocks.rockLayout[i][j] > 0)
                    {
                        rXPos = j * rocks.rockWidth;    // same as rockLayout method draw rocks for exact shape of the rectangles drawn
                        rYPos = i * rocks.rockHeight;
                        rWidth = rocks.rockWidth;
                        rHeight = rocks.rockHeight;
                    
                        Rectangle rockRect = new Rectangle(rXPos, rYPos, rWidth, rHeight);  // create a rectangle 
                        Rectangle plasmaRect = new Rectangle(plasmaXPos, plasmaYPos, plasmaWidth, plasmaHeight);
                        if (plasmaRect.intersects(rockRect))    // when the plasma rectangle intersects the rock rectangle at given x/y pos and width/height
                        {
                            rocks.setValue(0, i, j);            // causes the intersected rectangle to to disappear when set to 0
                            score++;                            // increment score
                        }
                    }
                    if(j==13)   // must break after all the rectangles are formed in the jpanel
                    {
                        break;  // stop loop or will have ArrayOutOfBounds Exception
                    }
                } 
            }
            if(plasmaYPos < 0)  // if plasma blast passes jpanel screen reset back to start of ship
            {
                plasmaYPos = HEIGHT - 100;
                plasmaShots++;
            }
            }
            BankAccount.gameFrame.setVisible(true);     // making sure the user doesn't exit during the game
            if(score == 30)    // Once all rectangles are intersected and score is maxed
            {      
                timer.stop();
                BankAccount.gameFrame.dispose();
                if (betted == true)
                {   
                    BankAccount.processCredit(betValue);
                    message = "Here's your reward for playing! \n \n"
                        + "-------------"+ fmt.format((betValue*2)) + "------------- \n \n"
                        + "Adding to your bank account... \n \n";   // for bank account to give user $1.00 for playing
                }
                else
                {
                    message = "Thanks for playing! \n \n";
                }
                JOptionPane.showMessageDialog(null, message);
               
            }   
            if(score < 30 && plasmaShots == 15)
            {
                timer.stop();
                BankAccount.gameFrame.dispose();
                if(betted == true)
                {
                    message = "You lost! \n \n"
                            + "-------------" + fmt.format((betValue*2)) + "------------- \n \n"
                            + "Please try again next time! \n \n";
                    BankAccount.processDebit(betValue);  
                }
                else
                {
                message = "           You lose!        \n \n"
                        + "Please try again next time! \n \n";
                }
                JOptionPane.showMessageDialog(null, message);
                
            }
                
            repaint();        
        }
        public void keyPressed(KeyEvent e) 
        {
            switch (e.getKeyCode())
            {
            case KeyEvent.VK_LEFT:  
                MOVEX = -5;
                break;
            case KeyEvent.VK_RIGHT:
                MOVEX = 5;
                break;
            case KeyEvent.VK_UP:    // UP key starts the game
                MOVEY = -25;
                startSound();
                fired = true;
                break;
            }
        }
        public void keyTyped(KeyEvent e)  {}
        public void keyReleased(KeyEvent e) 
        {
            MOVEX = 0;  // when left or right key is released, stop moving
        }
        
    }
}