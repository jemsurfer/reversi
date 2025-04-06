package reversi;

import java.util.ArrayList;
import java.util.stream.IntStream;
import java.lang.Math;

public class ReversiController implements IController {
    IModel model;
    IView view;

    @Override
    public void initialise(IModel model, IView view) {
        this.model=model;
        this.view=view;
        startup();
    }

    @Override
    public void startup() {
        //TODO: Figure out why the board isn't showing this
        //Set the initial 4 counters
        int width = model.getBoardWidth();
        int height = model.getBoardHeight();
        //Set all values to zero 
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                model.setBoardContents(i, j, 0);
            }
        }

        //Black goes first
        model.setPlayer(2);

        //Mark board unfinished
        model.setFinished(false);

        //then set initial layout
        model.setBoardContents(4,4,1);
        model.setBoardContents(5,4,2);
        model.setBoardContents(4,5,2);
        model.setBoardContents(5,5,1);
    }

    @Override
    public void update() {
        /* - If the controller uses the finished flag (in the model), then it should look at 
        * the board (in the model) and set the finished flag, or not, according to whether the game has finished.*/
        if (!model.hasFinished()){
            //The game is over when the board is full or there are no next possible moves
            int len = listLegalMoves(1).size()+listLegalMoves(2).size();
            if (boardFull() || len==0)
                model.setFinished(true);
        }

	 /* - If the controller uses the player number (in the model), then it should check it
     * in case it changed. (Probably nothing to do for this.)
     *  
	 * - set the feedback to the user according to which player is the current player 
     * (model.getPlayer()) and whether the game has finished or not.*/

    }

    @Override
    public void squareSelected(int player, int x, int y) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'squareSelected'");
    }

    @Override
    public void doAutomatedMove(int player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doAutomatedMove'");
    }

    ArrayList<Tuple<Integer,Integer>> listLegalMoves(int color) {
        ArrayList<Tuple<Integer,Integer>> l = new ArrayList<Tuple<Integer,Integer>>();

        for (int i=0;i<8;i++)
            for (int j=0;j<8;j++)
                if (isLegalMove(i, j, color)) 
                    l.add(new Tuple<Integer,Integer>(i, j));
        return l;
    }

    boolean boardFull(){
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                if (model.getBoardContents(i, j)!=0)
                    return true;
            }
        }
        return false;
    }

    boolean isLegalMove(int x, int y, int color) {
        //Move is legal if there's a square of opposite color adjacent to given square
        //and a square of the same color at some point in the line
        //I'll do this recursively

        int opposite;
        if (color==1){
           opposite = 2; 
        }else if (color == 2){
            opposite = 1;
        } else{
            opposite=0;
        }

        int[] adj = getAdjacentSquares(x, y);

        int opp=0;
        int[][] offsets = {{-1,-1},{0,-1},{1,-1},{-1,0},{0,0},{1,0},{-1,1},{0,1},{1,1}};

        //TODO: O(2^n) worst case
        for (int i=0; i<8; i++) {
            opp=adj[i];
            if (opp==opposite) {
                int[] curOffset = offsets[i];
                int xof = curOffset[0];
                int yof = curOffset[1];
                //If we can find a tile of the same color, then the move is valid 
                return lookForSame(x, y, color, opp, xof, yof, i);
            }
        }
        return false;
    }

    //Look along the row/col/diagonal until we find a tile of the original color
    //Or reach the end of the list and return false
    boolean lookForSame(int x, int y, int color, int opp, int xof, int yof, int index){
       int[] adj = getAdjacentSquares(x, y);

       if (adj[index]==opp){
            lookForSame(x+xof, y+yof, color, opp, xof, yof, index);
       } else if (adj[index]==color){
            return true;
       }

        return false;
    }

    int[] getAdjacentSquares(int x, int y) {
        int[] adj = new int[8];
        //Top left to bottom right

        int k=0;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (i==j)
                    continue;
                adj[k++] = model.getBoardContents(i, j);
            }
        }
        return adj;
    }

}
