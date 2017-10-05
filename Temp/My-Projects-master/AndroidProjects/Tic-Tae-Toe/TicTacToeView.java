package edu.purdue.yin93.cs180;

/**
 * Created by Saul Yin on 4/4/2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.preference.DialogPreference;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TicTacToeView implements TicTacToeViewInterface {

    Button[][] myButtons; //The 3x3 matrix of buttons
    Button newGameButton; //The "New Game" button
    Button[] choices;
    Context context;
    MediaPlayer winSong;
    String colorOfO = "blue";
    String colorOfX = "red";

    public void setColorOfO(String color) {
        colorOfO = color;
    }

    public void setColorOfX(String color) {
        colorOfX = color;
    }

    /**
     * Constructor. Initializes the instance variables.
     */
    public TicTacToeView(Button[][] myButtons, Button newGameButton, Context context,Button[] choices) {
        this.myButtons = myButtons;
        this.newGameButton = newGameButton;
        this.context = context;
        this.choices = choices;

    }

    @Override
    public void update(int x, int y, char player) {

        for(int i = 0;i<choices.length;i++) {
            choices[i].setEnabled(false);
        }
        this.myButtons[x][y].setText("" + player);
        this.myButtons[x][y].setEnabled(false);
        if (player == 'O') {
            if (this.colorOfO == null){
                this.myButtons[x][y].setTextColor(Color.BLUE);
            }
            else if (this.colorOfO.equals("red")) {
                this.myButtons[x][y].setTextColor(Color.RED);
            }
            else {
                this.myButtons[x][y].setTextColor(Color.BLUE);
            }

        }
        if (player == 'X') {
            if (this.colorOfX == null){
                this.myButtons[x][y].setTextColor(Color.RED);
            }
            else if (this.colorOfX.equals("blue")) {
                this.myButtons[x][y].setTextColor(Color.BLUE);
            }
            else {
                this.myButtons[x][y].setTextColor(Color.RED);
            }

        }
    }


    public void showWinner(String winner) {
        winSong = MediaPlayer.create(context,R.raw.winsong);
        winSong.start();


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(winner + " wins");
        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        winSong.release();
                    }
                });
        AlertDialog winning = builder.create();
        winning.show();


    }

    @Override
    public void resetButtons() {
        //TODO: Complete this method.
        //Step 1: Iterate through the myButton matrix.
        //Step 2: Reset the text of the current button.
        //Example usage : myButtons[i][j].setText("");
        //Step 3: Reset the color of text for the current button
        //Example usage : myButtons[x][y].setTextColor(Color.BLACK);
        //Step 4: Make the button clickable again
        //Example usage : myButtons[x][y].setEnabled(true);
        for (int x = 0; x < myButtons.length; x++) {
            for (int y = 0; y < myButtons.length; y++) {
                myButtons[x][y].setText("");
                myButtons[x][y].setTextColor(Color.BLACK);
                myButtons[x][y].setEnabled(true);
            }
        }
        for(int i = 0;i<choices.length;i++) {
            choices[i].setEnabled(true);
        }



    }

    @Override
    public void disableButtons() {
        //TODO: Complete this method.
        //Step 1: Iterate through the myButton matrix.
        //Step 2: Make the current button un-clickable
        //Example usage : myButtons[x][y].setEnabled(false);
        for (int i = 0; i < myButtons.length; i++) {
            for (int j = 0; j < myButtons.length; j++) {
                myButtons[i][j].setEnabled(false);
            }
        }
    }

    @Override
    public void gameOver() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Game over, no one wins");
        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog draw = builder.create();
        draw.show();
    }


}
