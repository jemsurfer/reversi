package reversi;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Grid extends JPanel {
  int color;
  IController cont;
  IModel model;
  ArrayList<ArrayList<Square>> squares;
  
  public Grid(int color, IController cont, IModel model) {
    setLayout(new GridLayout(8, 8));
    this.color=color;
    this.cont=cont;
    this.model=model;
    squares = new ArrayList<ArrayList<Square>>();

    if (color==1){
      for (int i=0; i<model.getBoardWidth(); i++){
        ArrayList<Square> inner = new ArrayList<Square>();

        for (int j=0; j<model.getBoardHeight(); j++){
          int c = model.getBoardContents(i, j);
          Square s = new Square(1, j, i, cont);

          if (c != 0) 
            s.addCounter(c);

          add(s);
          inner.add(s);
        }
        squares.add(inner);
      }
    } 
    //Invert the black grid
    else if (color==2) {
      for (int i=model.getBoardWidth()-1; i>=0; i--){
        ArrayList<Square> inner = new ArrayList<Square>();

        for (int j=model.getBoardHeight()-1; j>=0; j--){
          Square s = new Square(2, j, i, cont);
          int c = model.getBoardContents(i, j);

          if (c != 0)
            s.addCounter(c);

          add(s);
          inner.add(s);
        }
        squares.add(inner);
      }
    }

    refresh();
  }

  public void refresh() {
    for (ArrayList<Square> row : squares){
      for (Square s : row){

        int[] pos = s.getPos();
        int col = model.getBoardContents(pos[0], pos[1]);

        if (s.getCol()!=col)
          s.addCounter(col);
      }
    }
    revalidate();
  }
}