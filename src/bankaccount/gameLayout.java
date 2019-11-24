package bankaccount;

import java.awt.Color;
import java.awt.Graphics;

public class gameLayout 
{   
    public int [][] rockLayout;
    public int rockWidth, rockHeight;
    public gameLayout(int row, int column)
    {
        rockLayout = new int [row][column];
        for (int i = 0; i <row; i++)
        {
            for (int j = 0; j < column; j++)
            {
                rockLayout[i][j] = 1;       // has value of one which allows shape 
            }
        }
        rockWidth = 35; 
        rockHeight = 30;
        
    }
    public void drawRocks(Graphics g)
    {
        for(int i = 0; i<rockLayout.length; i++)
        {
            for(int j =0; j<rockLayout[0].length; j++)
            {
		if(rockLayout[i][j] > 0)
		{
                g.setColor(Color.RED);
                g.fillRect(j * rockWidth, i * rockHeight, rockWidth, rockHeight);   //create rectangle shape that's red
                g.setColor(Color.WHITE);
		g.drawRect(j * rockWidth, i * rockHeight, rockWidth, rockHeight);   //outline the shape above white				
                }
            }
        }
    }
    public void setValue (int value, int row, int column)
    {
        rockLayout[row][column] = value;                 // if value is zero, rock disappears
    }
}
