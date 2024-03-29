package io.github.sidvenu.connect4.logic;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Original Version: https://github.com/raulgonzalezcz/Connect4-AI-Java
 * @author Raúl González <raul.gonzalezcz@udlap.mx> ID: 151211
 * @version 1.0
 * @since 05/05/17
 * Here is the definition of the board for Connect4
 */

public class State {

    public static final int X = 1;     //User (used in Main and switch case)
    public static final int O = -1;    //Computer (used in Main and switch case)

    public static final int EMPTY = 0;              //Blank space
    //We need to know the player that made the last move
    ArrayList<GamePlay> lastMoves;
    public int lastLetterPlayed;
    public int winner;
    public int[][] gameBoard;
    private int rows, cols;
    public int[][] winningPositions = new int[4][2];
    //------------

    //Constructor of a state (board)
    public State(int rows, int cols) {
        lastMoves = new ArrayList<>();
        lastLetterPlayed = O; //The user starts first
        winner = 0;
        setBoardSize(rows, cols);
        gameBoard = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gameBoard[i][j] = EMPTY;
            }
        }
    }

    private void setBoardSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    private void setWinner(int winner) {
        this.winner = winner;
    }//end setWinner

    //Make a movement based on a column and a player
    public void makeMove(int col, int letter) {
        Log.v("TAGundo","makeMove row:"+getRowPosition(col)+"col:"+col);
        if (lastMoves.isEmpty())
            lastMoves.add(new GamePlay().moveDone(getRowPosition(col), col));
        else
            lastMoves.add(
                    lastMoves.get(lastMoves.size() - 1).moveDone(getRowPosition(col), col)
            );
        gameBoard[getRowPosition(col)][col] = letter;
        lastLetterPlayed = letter;
    }//end makeMove

    public void undoMove() {
        Log.v("TAGundo", "undoMove");
        if (!lastMoves.isEmpty()) {
            GamePlay lastMove = lastMoves.remove(lastMoves.size() - 1);
            gameBoard[lastMove.row][lastMove.col] = EMPTY;
            Log.v("TAGundo", "undoing row:"+lastMove.row+"col:"+lastMove.col);
            lastLetterPlayed = lastLetterPlayed == X ? O : X;

        }
    }

    //Checks whether a move is valid; whether a square is empty. Used only when we need to expand a movement
    private boolean isValidMove(int col) {
        int row = getRowPosition(col);
        if ((row <= -1) || (col <= -1) || (row >= rows) || (col >= cols)) {
            return false;
        }
        return gameBoard[row][col] == EMPTY;
    }//end isValidMove

    //Is used when we need to make a movement (Is possible to move the piece?)
    private boolean canMove(int row, int col) {
        //We evaluate mainly the limits of the board
        return (row > -1) && (col > -1) && (row < rows) && (col < cols);
    }//end CanMove

    //Is a column full?
    public boolean checkFullColumn(int col) {
        if (gameBoard[0][col] == EMPTY)
            return false;
        else {
            System.out.println("The column " + (col + 1) + " is full. Select another column.");
            return true;
        }
    }//end checkFullColumn

    //We search for a blank space in the board to put the piece ('X' or 'O')
    public int getRowPosition(int col) {
        int rowPosition = -1;
        for (int row = 0; row < rows; row++) {
            if (gameBoard[row][col] == EMPTY) {
                rowPosition = row;
            }
        }
        return rowPosition;
    }//end getRowPosition

    //This method help us to expand the board (it´s a board state given a move)
    State boardWithExpansion(State board) {
        State expansion = new State(rows, cols);
        expansion.lastMoves = new ArrayList<>(board.lastMoves);
        expansion.lastLetterPlayed = board.lastLetterPlayed;
        expansion.winner = board.winner;
        expansion.gameBoard = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                expansion.gameBoard[i][j] = board.gameBoard[i][j];
            }
        }
        return expansion;
    }//end boardWithExpansion

    //Generates the children of the state. The max number of the children is 7 because we have 7 columns
    LinkedList<State> getChildren(int letter) {
        LinkedList<State> children = new LinkedList<>();
        for (int col = 0; col < cols; col++) {
            if (isValidMove(col)) {
                State child = boardWithExpansion(this);
                child.makeMove(col, letter);
                children.add(child);
            }
        }
        return children;
    }//end getChildren

    int utilityFunction() {
        //MAX plays 'O'
        // +90 if 'O' wins, -90 'X' wins,
        // +10 if three 'O' in a row, -5 three 'X' in a row,
        // +4 if two 'O' in a row, -1 two 'X' in a row
        int Xlines = 0;
        int Olines = 0;
        if (checkWinState()) {
            if (winner == X) {
                Xlines = Xlines + 90;
            } else {
                Olines = Olines + 90;
            }
        }
        Xlines = Xlines + check3In(X) * 10 + check2In(X) * 4;
        Olines = Olines + check3In(O) * 5 + check2In(O);
        return Olines - Xlines;
    }//end utilityFunction

    //Is there a possible winner movement? (4In)
    private boolean checkWinState() {
        //Case if we have 4-row
        for (int i = rows - 1; i >= 0; i--) {
            for (int j = 0; j < cols - 3; j++) {
                if (gameBoard[i][j] == gameBoard[i][j + 1] && gameBoard[i][j] == gameBoard[i][j + 2] && gameBoard[i][j] == gameBoard[i][j + 3] && gameBoard[i][j] != EMPTY) {
                    setWinner(gameBoard[i][j]);
                    winningPositions[0][0] = i;
                    winningPositions[0][1] = j;
                    winningPositions[1][0] = i;
                    winningPositions[1][1] = j + 1;
                    winningPositions[2][0] = i;
                    winningPositions[2][1] = j + 2;
                    winningPositions[3][0] = i;
                    winningPositions[3][1] = j + 3;
                    return true;
                }
            }
        }

        //Case we have a 4-column
        for (int i = rows - 1; i >= 3; i--) {
            for (int j = 0; j < cols; j++) {
                if (gameBoard[i][j] == gameBoard[i - 1][j] && gameBoard[i][j] == gameBoard[i - 2][j] && gameBoard[i][j] == gameBoard[i - 3][j] && gameBoard[i][j] != EMPTY) {
                    setWinner(gameBoard[i][j]);
                    winningPositions[0][0] = i;
                    winningPositions[0][1] = j;
                    winningPositions[1][0] = i - 1;
                    winningPositions[1][1] = j;
                    winningPositions[2][0] = i - 2;
                    winningPositions[2][1] = j;
                    winningPositions[3][0] = i - 3;
                    winningPositions[3][1] = j;
                    return true;
                }
            }
        }

        //Case we have an ascendent 4-diagonal
        for (int i = 0; i < rows - 3; i++) {
            for (int j = 0; j < cols - 3; j++) {
                if (gameBoard[i][j] == gameBoard[i + 1][j + 1] && gameBoard[i][j] == gameBoard[i + 2][j + 2] && gameBoard[i][j] == gameBoard[i + 3][j + 3] && gameBoard[i][j] != EMPTY) {
                    setWinner(gameBoard[i][j]);
                    winningPositions[0][0] = i;
                    winningPositions[0][1] = j;
                    winningPositions[1][0] = i + 1;
                    winningPositions[1][1] = j + 1;
                    winningPositions[2][0] = i + 2;
                    winningPositions[2][1] = j + 2;
                    winningPositions[3][0] = i + 3;
                    winningPositions[3][1] = j + 3;
                    return true;
                }
            }
        }

        //Case we have an descendent 4-diagonal
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (canMove(i - 3, j + 3)) {
                    if (gameBoard[i][j] == gameBoard[i - 1][j + 1] && gameBoard[i][j] == gameBoard[i - 2][j + 2] && gameBoard[i][j] == gameBoard[i - 3][j + 3] && gameBoard[i][j] != EMPTY) {
                        setWinner(gameBoard[i][j]);
                        winningPositions[0][0] = i;
                        winningPositions[0][1] = j;
                        winningPositions[1][0] = i - 1;
                        winningPositions[1][1] = j + 1;
                        winningPositions[2][0] = i - 2;
                        winningPositions[2][1] = j + 2;
                        winningPositions[3][0] = i - 3;
                        winningPositions[3][1] = j + 3;
                        return true;
                    }
                }
            }
        }
        //There is no winner yet :(
        Log.v("TAGundo", "NO WINNER YET");
        setWinner(EMPTY);
        return false;
    }//end checkWinState

    //Checks if there are 3 pieces of a same player
    private int check3In(int player) {
        int times = 0;
        //In row
        for (int i = rows - 1; i >= 0; i--) {
            for (int j = 0; j < cols; j++) {
                if (canMove(i, j + 2)) {
                    if (gameBoard[i][j] == gameBoard[i][j + 1] && gameBoard[i][j] == gameBoard[i][j + 2] && gameBoard[i][j] == player) {
                        times++;
                    }
                }
            }
        }

        //In column
        for (int i = rows - 1; i >= 0; i--) {
            for (int j = 0; j < cols; j++) {
                if (canMove(i - 2, j)) {
                    if (gameBoard[i][j] == gameBoard[i - 1][j] && gameBoard[i][j] == gameBoard[i - 2][j] && gameBoard[i][j] == player) {
                        times++;
                    }
                }
            }
        }

        //In diagonal ascendent
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (canMove(i + 2, j + 2)) {
                    if (gameBoard[i][j] == gameBoard[i + 1][j + 1] && gameBoard[i][j] == gameBoard[i + 2][j + 2] && gameBoard[i][j] == player) {
                        times++;
                    }
                }
            }
        }

        //In diagonal descendent
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (canMove(i - 2, j + 2)) {
                    if (gameBoard[i][j] == gameBoard[i - 1][j + 1] && gameBoard[i][j] == gameBoard[i - 2][j + 2] && gameBoard[i][j] == player) {
                        times++;
                    }
                }
            }
        }
        return times;
    }//end check3In

    //Checks if there are 2 pieces of a same player
    private int check2In(int player) {
        int times = 0;
        //In a row
        for (int i = rows - 1; i >= 0; i--) {
            for (int j = 0; j < cols; j++) {
                if (canMove(i, j + 1)) {
                    if (gameBoard[i][j] == gameBoard[i][j + 1] && gameBoard[i][j] == player) {
                        times++;
                    }
                }
            }
        }

        //In a column
        for (int i = rows - 1; i >= 0; i--) {
            for (int j = 0; j < cols; j++) {
                if (canMove(i - 1, j)) {
                    if (gameBoard[i][j] == gameBoard[i - 1][j] && gameBoard[i][j] == player) {
                        times++;
                    }
                }
            }
        }

        //In a diagonal ascendent
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (canMove(i + 1, j + 1)) {
                    if (gameBoard[i][j] == gameBoard[i + 1][j + 1] && gameBoard[i][j] == player) {
                        times++;
                    }
                }
            }
        }

        //In a diagonal descendent
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (canMove(i - 1, j + 1)) {
                    if (gameBoard[i][j] == gameBoard[i - 1][j + 1] && gameBoard[i][j] == player) {
                        times++;
                    }
                }
            }
        }
        return times;
    }//end check2In

    public boolean checkGameOver() {
        //If there is a winner, we need to know it
        if (checkWinState()) {
            return true;
        }
        //Are there blank spaces in the board?
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (gameBoard[row][col] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }//end checkGameOver
}//end State
