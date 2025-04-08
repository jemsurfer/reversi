package reversi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        view.feedbackToUser(2,new String("Black - your turn!"));

        //Mark board unfinished
        model.setFinished(false);

        //then set initial layout
        model.setBoardContents(3,3,1);
        model.setBoardContents(4,3,2);
        model.setBoardContents(3,4,2);
        model.setBoardContents(4,4,1);
    }

    @Override
    public void update() {
        /* - If the controller uses the finished flag (in the model), then it should look at 
        * the board (in the model) and set the finished flag, or not, according to whether the game has finished.*/
        if (!model.hasFinished()) {
            //The game is over when the board is full or there are no next possible moves
            //int len = listLegalMoves(1).size()+listLegalMoves(2).size();
            int len = 1;

            if (boardFull() || len==0)
                model.setFinished(true);
        }

        /* - If the controller uses the player number (in the model), then it should check it
        * in case it changed. (Probably nothing to do for this.)*/
        //Not sure how to implement this

        /* - set the feedback to the user according to which player is the current player 
        * (model.getPlayer()) and whether the game has finished or not.*/

        if (!model.hasFinished()) {
            int curPlayer = model.getPlayer();
            String msg;

            if (curPlayer == 1) {
                msg = new String("White - your turn!");
            } else {
                msg = new String("Black - your turn!");
            }
            view.feedbackToUser(curPlayer, msg);
        }

    }

    @Override
    public void squareSelected(int player, int x, int y) {
        //Don't let the player go if it's not their turn
        if (model.getPlayer()!=player) {
            view.feedbackToUser(player, new String("Not your turn!"));
            return;
        }

        if (!isLegalMove(x, y, player)) {
            view.feedbackToUser(player, new String("Illegal move!"));
            return;
        }

        //Update the board to reflect the new piece
        model.setBoardContents(x, y, player);

        //Send a message to the user who just played
        String msg = new String(String.format("Your last move was (%s,%s)", x, y));
        view.feedbackToUser(player, msg);

        if (player==1){
            model.setPlayer(2);
        }else{
            model.setPlayer(1);
        }

        update();
        view.refreshView();
    }

    @Override
    public void doAutomatedMove(int player) {
        //TODO: Implement greedy AI
        Tuple<Integer,Integer> move = getGreedyMove(player);
        squareSelected(player, move.first(), move.second()); 
    }

    private Tuple<Integer,Integer> getGreedyMove(int player) {
        throw new UnsupportedOperationException("Unimplemented function: getGreedyMove");
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
        boolean full=true;
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                full = full && (model.getBoardContents(i, j)!=0);
            }
        }
        return full;
    }

    boolean isLegalMove(int x, int y, int color) {
        //Move is legal if there's a square of opposite color adjacent to given square
        //and a square of the same color at some point in the line
        //I'll do this recursively

        int opposite;

        if (model.getBoardContents(x, y)!=0){
            return false;
        }

        if (color==1){
           opposite = 2; 
        }else if (color == 2){
            opposite = 1;
        } else{
            opposite=0;
        }

        int[] adj = getAdjacentSquares(x, y, color);
        System.out.println(Arrays.toString(adj));

        int opp=0;
        int[][] offsets = {{-1,-1},{0,-1},{1,-1},{-1,0},{0,0},{1,0},{-1,1},{0,1},{1,1}};

        //TODO: O(2^n) worst case - improve this
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
       int[] adj = getAdjacentSquares(x, y, color);

       if (adj[index]==opp){
            lookForSame(x+xof, y+yof, color, opp, xof, yof, index);
       } else if (adj[index]==color){
            return true;
       }

        return false;
    }

    int[] getAdjacentSquares(int x, int y, int color) {
        int[] adj = new int[8];
        //Top left to bottom right (w.r.t white's perspective)

        int k=0;
        for (int i=-1;i<2;i++){
            for (int j=-1;j<2;j++){
                if (i==j && i==0)
                    continue;
                adj[k++] = model.getBoardContents(x+i, y+j);
            }
        }
        if (color == 2){
            Collections.reverse(Arrays.asList(adj));
        }
        return adj;
    }

}
