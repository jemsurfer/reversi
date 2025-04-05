package reversi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Square extends JPanel {
    int player;
    int i,j;
    IController cont;
    int color;

    Square(int player, int i, int j, IController cont) {
        this.player=player;
        this.i=i;
        this.j=j;
        this.cont=cont;
        this.color=0;

        setLayout(new BorderLayout());
        setBackground(Color.GREEN);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                cont.squareSelected(player, i, j);
            }
        });
    }

    public void addCounter(int color) {
        /*Remove other counters*/
        this.color=color;
        for (Component c : getComponents())
            remove(c); 
        add(new Counter(color));
    }

    public int[] getPos(){
        int[] ret = {i,j};
        return ret;
    }

    public int getCol(){
        return color; 
    }
}
