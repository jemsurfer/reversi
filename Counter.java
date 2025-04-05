package reversi;

import javax.swing.*;
import java.awt.*;

public class Counter extends JLabel {
    int color;

    //Color - black is true white is false
    public Counter(int color) {
        this.color=color;
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        //1 is white, 2 is black
        if (color==1) 
            g2d.setColor(Color.WHITE);
        else if (color==2)
            g2d.setColor(Color.BLACK);
        else
            return;

        int width = getWidth();
        int height = getHeight();
        g2d.fillOval(0, 0, (int)(width*0.98), (int)(height*0.98));
        g2d.dispose();
    }
}
