package io.github.sidvenu.connect4.logic;
import java.util.LinkedList;
import java.util.Random;


/**
 * Original Version: https://github.com/raulgonzalezcz/Connect4-AI-Java
 * @author Raúl González <raul.gonzalezcz@udlap.mx> ID: 151211
 * @version 1.0
 * @since 05/05/17
 * Here is the core of the MinMax algorithm
 */

public class MinMax {
    //Variable that holds the maximum depth the MinMax algorithm will reach (level of the three)
    private int maxDepth;
    //Variable that holds which letter the computer controls
    private int computerLetter;
    ////////////////////

    public MinMax(int thePlayerLetter) {
        maxDepth = 5; //This is important to get a better decision (more depth, more accurate decision, more time)
        computerLetter = thePlayerLetter;
    }//end Constructor

    //Initiates the MinMax algorithm
    public GamePlay getNextMove(State board) {
        //We want to take the lowest of its recursive generated values in order to choose the greatest
        return max(board.boardWithExpansion(board), 0);
    }//end getNextMove

    //The max and min methods are called interchangingly, one after another until a max depth is reached
    //The difference between Tic Tac Toe program is that here we can specify a depth (number of levels in the three)
    private GamePlay min(State board, int depth) { //MIN plays 'X' (user)
        Random r = new Random();
        // If MIN is called on a state that is terminal or after a maximum depth is reached, then a heuristic is calculated on the state and the move returned.
        if((board.checkGameOver()) || (depth == maxDepth)){
            GamePlay baseMove = new GamePlay();
            GamePlay lastMove = board.lastMoves.get(board.lastMoves.size()-1);
            baseMove = baseMove.possibleMove(lastMove.row, lastMove.col, board.utilityFunction());
            return baseMove;
        }else{
            //The children-moves of the state are calculated (expansion)
            LinkedList<State> children = new LinkedList<>(board.getChildren(State.X));
            GamePlay minMove = new GamePlay();
            minMove = minMove.moveToCompare(Integer.MAX_VALUE);
            for (int i =0; i < children.size();i++) {
                State child = children.get(i);
                GamePlay lastMove = child.lastMoves.get(child.lastMoves.size()-1);
                //And for each child max is called, on a lower depth
                GamePlay move = max(child, depth + 1);
                //The child-move with the lowest value is selected and returned by max
                if(move.getValue() <= minMove.getValue()) {
                    if ((move.getValue() == minMove.getValue())) {
                        //If the heuristic has the same value then we randomly choose one of the two moves
                        if (r.nextInt(2) == 0) { //If 0, we rewrite the maxMove. Else, the maxMove is the same
                            minMove.setRow(lastMove.row);
                            minMove.setCol(lastMove.col);
                            minMove.setValue(move.getValue());
                        }
                    }
                    else {
                        minMove.setRow(lastMove.row);
                        minMove.setCol(lastMove.col);
                        minMove.setValue(move.getValue());
                    }
                }
            }
            return minMove;
        }
    }//end min

    //The max and min methods are called interchangingly, one after another until a max depth or game over is reached
    private GamePlay max(State board, int depth) { //MAX plays 'O'
        Random r = new Random();
        //If MAX is called on a state that is terminal or after a maximum depth is reached, then a heuristic is calculated on the state and the move returned.
        if((board.checkGameOver()) || (depth == maxDepth)) {
            GamePlay baseMove = new GamePlay();
            GamePlay lastMove = board.lastMoves.get(board.lastMoves.size()-1);
            baseMove = baseMove.possibleMove(lastMove.row, lastMove.col, board.utilityFunction());
            return baseMove;
        }else{
            LinkedList<State> children = new LinkedList<>(board.getChildren(computerLetter));
            GamePlay maxMove = new GamePlay();
            maxMove = maxMove.moveToCompare(Integer.MIN_VALUE);
            for (int i =0; i < children.size();i++) {
                State child = children.get(i);
                GamePlay lastMove = child.lastMoves.get(child.lastMoves.size()-1);
                GamePlay move = min(child, depth + 1);
                //Here is the difference with Min method: The greatest value is selected
                if(move.getValue() >= maxMove.getValue()) {
                    if ((move.getValue() == maxMove.getValue())) {
                        if (r.nextInt(2) == 0) {
                            maxMove.setRow(lastMove.row);
                            maxMove.setCol(lastMove.col);
                            maxMove.setValue(move.getValue());
                        }
                    }
                else {
                    maxMove.setRow(lastMove.row);
                    maxMove.setCol(lastMove.col);
                    maxMove.setValue(move.getValue());
                    }
                }
            }
            return maxMove;
        }
    }//end max
}//end class MinMax
