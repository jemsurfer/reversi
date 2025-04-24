package reversi;

import java.util.List;
import java.util.ArrayList;

public class ReversiController implements IController {
    IModel model;
    IView view;
    String[] playerNames = {"None","White","Black"};
    
    @Override
    public void initialise(IModel model, IView view) {
        this.model=model;
        this.view=view;
        startup();
    }

    @Override
    public void startup() {
        int width = model.getBoardWidth();
        int height = model.getBoardHeight();

        //Set all values to zero (as startup is also used to ) 
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                model.setBoardContents(i, j, 0);
            }
        }

        //Black goes first in every version of othello
        //apart from jason's version apparently
        model.setPlayer(1);
        view.feedbackToUser(1,new String("White player - choose where to put your piece"));
        view.feedbackToUser(2,new String("Black player - not your turn"));

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

        int curPlayer = model.getPlayer();

        //The game is over when the board is full or there are no next possible moves
        int curPlayerMoves = listLegalMoves(curPlayer).size();
        int oppPlayer = oppositePlayer(curPlayer);
        int oppMoves = listLegalMoves(oppPlayer).size();

        //We're done if the board is full or neither player can go
        if (boardFull() || (curPlayerMoves==oppMoves && oppMoves==0))
            model.setFinished(true);
        else
            model.setFinished(false);

        if (curPlayerMoves==0 && oppMoves!=0) {
            model.setPlayer(oppPlayer);
            oppPlayer=curPlayer;
            curPlayer=model.getPlayer();
        }

        /* - If the controller uses the player number (in the model), then it should check it
        * in case it changed. (Probably nothing to do for this.)*/
        //Not sure how to implement this

        /* - set the feedback to the user according to which player is the current player 
        * (model.getPlayer()) and whether the game has finished or not.*/

        if (!model.hasFinished()) {
            String myTurn = String.format("%s player - choose where to put your piece", playerNames[curPlayer]);
            String notMyTurn = String.format("%s player - not your turn", playerNames[oppPlayer]);

            view.feedbackToUser(oppPlayer,notMyTurn); 
            view.feedbackToUser(curPlayer, myTurn);
        } else{
            Tuple<Integer,Integer> pieces = countPieces();

            int black = pieces.first();
            int white = pieces.second();

            if (white==black) {
                String draw = String.format("Draw. Both players ended with %s pieces. Reset the game to replay.",white);
                view.feedbackToUser(1, draw);
                view.feedbackToUser(2, draw);
            }else{
                String winner = new String();
                String loser = new String();
                int winnerCount=0;
                int loserCount=0;
                
                if (black>white) {
                    winner=new String("Black");
                    loser=new String("White");
                    winnerCount=black;
                    loserCount=white;
                }
                else if (white>black) {
                    winner = new String("White");
                    loser = new String("Black");
                    winnerCount=white;
                    loserCount=black;
                }

                String win = String.format("%s won. %s %d to %s %d. Reset the game to replay.", winner, winner, winnerCount, loser, loserCount);

                view.feedbackToUser(1, win);
                view.feedbackToUser(2, win);
            }
        }

    }

    int oppositePlayer(int curPlayer) {
        if (curPlayer==1){
            return 2;
        }else if (curPlayer==2){
            return 1;
        }
        return 0;
    }

    @Override
    public void squareSelected(int player, int x, int y) {

        //Don't let the player go if it's not their turn / the game has finished
        if (model.hasFinished())
            return;

        if (model.getPlayer()!=player) {
            String notMyTurn = String.format("%s player - not your turn", playerNames[oppositePlayer(player)]);
            view.feedbackToUser(player, notMyTurn);
            return;
        }

        List<Tuple<Integer,Integer>> affectedSquares = listAffectedSquares(x, y, player);

        if (affectedSquares.size()==0) {
            view.feedbackToUser(player, new String("Illegal move!"));
            return;
        }

        //Update the board to reflect the new piece
        model.setBoardContents(x, y, player);

        //And flip the respective squares (recursing to flip other affected squares)
        flipSquares(affectedSquares);

        model.setPlayer(oppositePlayer(player));

        update();
        view.refreshView();
    }

    ///Returns counted pieces in order (black,white)
    Tuple<Integer,Integer> countPieces() {
        int black=0;
        int white=0;
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                switch (model.getBoardContents(i, j)){
                    case 1: {white++;break;}
                    case 2: black++;
                }
            }
        }
        return new Tuple<Integer,Integer>(black, white);
    }

    @Override
    public void doAutomatedMove(int player) {
        //TODO: Implement greedy AI
        if (model.hasFinished()){
            return;
        }

        Tuple<Integer,Integer> move = getGreedyMove(player);
        squareSelected(player, move.first(), move.second()); 
    }

    private Tuple<Integer,Integer> getGreedyMove(int player) {
        List<Tuple<Tuple<Integer,Integer>,Integer>> moves =  listLegalMoves(player); 

        int max=0;
        Tuple<Integer,Integer> maxPoint = new Tuple<>(null, null); 

        for (Tuple<Tuple<Integer,Integer>,Integer> tup: moves) {
            if (tup.second()>max) {
                max=tup.second();
                maxPoint = tup.first();
            }
        }
        return maxPoint;
    }

    //Returns a list which looks like [((x1,y1),score1),((x2,y2),score2),...]
    List<Tuple<Tuple<Integer,Integer>,Integer>> listLegalMoves(int color) {
        List<Tuple<Tuple<Integer,Integer>,Integer>> l = new ArrayList<>();
        List<Tuple<Integer,Integer>> affectedSquares; 

        for (int i=0;i<8;i++)
            for (int j=0;j<8;j++){
                affectedSquares=listAffectedSquares(i, j, color);
                if (affectedSquares.size()!=0) {
                    l.add(new Tuple<>(new Tuple<>(i,j),affectedSquares.size()));
                }
            }

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

    List<Tuple<Integer,Integer>> listAffectedSquares(int x, int y, int color) {

        //Move is legal if there's a square of opposite color adjacent to given square
        //and a square of the same color at some point in the line
        //I'll do this recursively
        List<Tuple<Integer,Integer>> squaresToFlip = new ArrayList<>();
        Tuple<Integer,Integer> point = new Tuple<>(x,y);

        //Can't put a counter over another counter
        //Ignore visited squares
        //Ignore colors other than 1 and 2
        if (model.getBoardContents(x, y)!=0 || (color!=1 && color!=2)){
            return squaresToFlip;
        }

        int opposite = oppositePlayer(color);

        int[] adj = getAdjacentSquares(x, y);

        int opp;

        //List of coordinate offsets for each adjacent square 
        int[][] offsets = {{-1,-1},{0,-1},{1,-1},{-1,0},{1,0},{-1,1},{0,1},{1,1}};

        //Iterate through all adjacent tiles
        //TODO: O(2^n) worst case - improve this
        for (int i=0; i<8; i++) {
            opp=adj[i];
            if (opp==opposite) {
                int[] curOffset = offsets[i];
                int xof = curOffset[0];
                int yof = curOffset[1];

                //If we can find a tile of the same color, then the move is valid 
                List<Tuple<Integer,Integer>> affectedSquares = new ArrayList<>();

                if (lookForSame(x, y, color, opp, xof, yof, i, affectedSquares)) {
                    affectedSquares.add(new Tuple<>(x+xof,y+yof));

                    //Apparently we don't do cascading changes in this version of the game, so this is all not needed (:
                    //Create a backup as we can't modify a list while iterating through it
                    //List<Tuple<Integer,Integer>> backup = new ArrayList<>(affectedSquares);
                    //For each of the squares being flipped, check wether they also cause squares to be flipped 
                    //for (Tuple<Integer,Integer> s: backup) {
                        //int backupCol = model.getBoardContents(s.first(), s.second());
                        ////Hack: set the current square to zero, so we don't return an empty list
                        //model.setBoardContents(s.first(), s.second(), 0);
                        ////Recurse with one of these squares to be flipped
                        //affectedSquares.addAll(listAffectedSquares(s.first(), s.second(), color, visited)); 
                        ////Set the square back to the color it's supposed to be
                        //model.setBoardContents(s.first(), s.second(), backupCol);
                    //}

                    squaresToFlip.addAll(affectedSquares); 
                }

            }
        }
        return squaresToFlip;
    }

    void flipSquares(List<Tuple<Integer,Integer>> affectedSquares) {

        for (Tuple<Integer,Integer> p : affectedSquares) {
        
            int x = p.first();
            int y = p.second();
            int opp = 0;


            switch (model.getBoardContents(x, y)){
                case 1:{opp=2;break;}
                case 2:{opp=1;}
            }

            model.setBoardContents(x, y, opp);
        }
    }

    //Look along the row/col/diagonal until we find a tile of the original color
    //Or reach the end of the list and return false
    //Keep track of opposite squares to be flipped after validation
    boolean lookForSame(int x, int y, int color, int opp, int xof, int yof, int index, List<Tuple<Integer,Integer>> affectedSquares){
        x+=xof;
        y+=yof;

       if (x>model.getBoardWidth() || y>model.getBoardHeight() || x<0 || y<0){
        return false;
       }

       int adj = getAdjacentSquares(x, y)[index];

        //If we've found a square of the opposite color
        if (adj==opp){
                //Add it to the affectedSquares array
                affectedSquares.add(new Tuple<Integer,Integer>(x+xof,y+yof));
                return lookForSame(x, y, color, opp, xof, yof, index, affectedSquares);
        } else if (adj==color)
                return true;

        return false;
    }

    int[] getAdjacentSquares(int x, int y) {
        int[] adj = new int[8];
        //Top left to bottom right (w.r.t white's perspective)

        int k=0;
        int height = model.getBoardHeight()-1;
        int width = model.getBoardWidth()-1;

        for (int i=-1;i<2;i++){
            for (int j=-1;j<2;j++){
                if (i==j && i==0)
                    continue;
                int xof = x+j;
                int yof = y+i;
                if (xof<0 || xof>width || yof<0 || yof>height) {
                    adj[k++]=0;
                    continue;
                }
                adj[k++] = model.getBoardContents(xof,yof);
            }
        }
        return adj;
    }

}
