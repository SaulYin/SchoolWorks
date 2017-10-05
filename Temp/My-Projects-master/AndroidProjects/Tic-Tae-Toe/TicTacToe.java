package edu.purdue.yin93.cs180;



/**
 * Created by Saul Yin on 4/4/2016.
 */
public class TicTacToe {

    TicTacToeViewInterface view; //The view interface used to make modifications to the view
    char[][] board; //The 3x3 board represented with characters
    char currentPlayer; //Keeps track of whose turn it is currently
    int cellsOccupied; //Keeps track of how many cells on the board are occupied
    char winner; //Keeps track of who won

    /**
     * Constructor. Initializes the instance variables.
     */
    public TicTacToe(TicTacToeViewInterface view) {
        this.view = view;
        this.board = new char[3][3];
        this.currentPlayer = 'X';
        this.cellsOccupied = 0;
        this.winner = ' ';
    }

    /**
     * This function is called to start a new game.
     */
    public void newGame() {
        this.reset();
    }

    /**
     * This function should reset each button on the grid.
     */
    public void reset() {
        //TODO: Complete this method.
        //Every time a new game begins, make sure to reset the view as well
        //as the board and the other instance variables.
        view.resetButtons();
        this.board = new char[3][3];
        this.currentPlayer = 'X';
        this.cellsOccupied = 0;
        this.winner = ' ';
    }


    public boolean checkWinner(char player) {
        //TODO: Complete this method.

        if (checkLeftDia(player) || checkCol(player) || checkRow(player) || checkRightDia(player)) {
            view.disableButtons();
            view.showWinner("" + this.winner);
            return true;
        }
        return false;
    }

    public boolean checkRow(char player) {
        boolean result = false;

        for (int i = 0; i < this.board.length; i++) {
            char check = board[i][0];
            int count = 0;
            if (check == player) {
                for (int j = 1; j < this.board.length; j++) {

                    if (board[i][j] == check) {
                        count = count + 1;
                    } else {
                        break;
                    }

                    if (count == 2) {
                        this.winner = player;
                        result = true;

                    }
                }
            }
        }

        return result;
    }

    public boolean checkCol(char player) {
        boolean result = false;

        for (int i = 0; i < board.length; i++) {
            char check = board[0][i];
            int count = 0;
            for (int j = 1; j < board[0].length; j++) {

                if (check == player) {
                    if (board[j][i] != check) {
                        break;
                    }
                    count = count + 1;

                    if (count == 2) {
                        this.winner = player;
                        result = true;

                    }
                }
            }
        }

        return result;
    }

    public boolean checkLeftDia(char player) {
        boolean result = false;
        char check = board[0][0];
        int count = 0;
        for (int j = 1; j < board[0].length; j++) {
            if (check == player) {
                if (board[j][j] != check) {
                    break;
                }
                count = count + 1;
                if (count == 2) {
                    this.winner = player;
                    result = true;

                }
            }
        }

        return result;
    }

    public boolean checkRightDia(char player) {
        boolean result = false;
        char check = board[0][board.length - 1];
        int count = 0;
        for (int j = 1; j < board[0].length; j++) {
            if (check == player) {
                if (board[j][board.length - 1 - j] != check) {
                    break;
                }
                count = count + 1;
                if (count == 2) {
                    this.winner = player;
                    result = true;
                }
            }
        }

        return result;
    }


    public void updateGameBoard(int x, int y) {

        if (!checkWinner(this.currentPlayer)) {

            if (board[x][y] != 'X' && board[x][y] != 'O') {
                view.update(x, y, this.currentPlayer);
                this.board[x][y] = this.currentPlayer;
                this.cellsOccupied++;

                boolean winLastMin = checkWinner(this.currentPlayer);
                if (cellsOccupied == 9 && winLastMin == false) {
                    view.gameOver();
                }

                if (this.currentPlayer == 'X') {
                    this.currentPlayer = 'O';

                } else if (this.currentPlayer == 'O') {
                    this.currentPlayer = 'X';
                }
            }

        }


    }
}