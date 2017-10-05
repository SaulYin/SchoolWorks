package edu.purdue.yin93.cs180;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    TicTacToeView view;
    TicTacToe myObj;
    Context context;
    String colorOfO = "blue";
    String colorOfX = "red";


    //Consider onCreate the public static void main in Android. This gets called when you run your application
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); //Default onCreate statement which is a MUST to be specified
        setContentView(R.layout.activity_main); //Sets the layout of the application as to the one specified in activity_main
        Button b00 = (Button) findViewById(R.id.button00); //Declares and initializes button at (0, 0)
        Button b01 = (Button) findViewById(R.id.button01); //Declares and initializes button at (0, 1)
        Button b02 = (Button) findViewById(R.id.button02); //Declares and initializes button at (0, 2)
        Button b10 = (Button) findViewById(R.id.button10); //Declares and initializes button at (1, 0)
        Button b11 = (Button) findViewById(R.id.button11); //Declares and initializes button at (1, 1)
        Button b12 = (Button) findViewById(R.id.button12); //Declares and initializes button at (1, 2)
        Button b20 = (Button) findViewById(R.id.button20); //Declares and initializes button at (2, 0)
        Button b21 = (Button) findViewById(R.id.button21); //Declares and initializes button at (2, 1)
        Button b22 = (Button) findViewById(R.id.button22); //Declares and initializes button at (2, 2)
        Button newGame = (Button) findViewById(R.id.newGame); //Declares and initializes the New Game button
        Button X_red = (Button) findViewById(R.id.X_Red);
        Button O_red = (Button) findViewById(R.id.O_Red);
        Button X_blue = (Button) findViewById(R.id.X_Blue);
        Button O_blue = (Button) findViewById(R.id.O_Blue);
        Button[][] myButtons = {{b00, b01, b02}, {b10, b11, b12}, {b20, b21, b22}}; //Creates a 2d array of buttons
        Button[] choices = {X_blue,X_red,O_blue,O_red};

        context = this;

        view = new TicTacToeView(myButtons, newGame,context,choices); //Initializes the TicTacToeView object
        myObj = new TicTacToe(view); //Initializes the TicTacToe object

    }

    /**
     * Gets called whenever a button is clicked as we had specified in the activity_main layout file
     * @param v : The current view
     */
    public void buttonClicked(View v) {
        switch (v.getId()) { //Create a switch for all the IDs we have in the view
            case R.id.button00: //If the element clicked was button at (0, 0)
                myObj.updateGameBoard(0, 0); //Call updateGameBoard with (0, 0) as arguments
                break;
            case R.id.button01:
                myObj.updateGameBoard(0, 1);
                break;
            case R.id.button02:
                myObj.updateGameBoard(0, 2);
                break;
            case R.id.button10:
                myObj.updateGameBoard(1, 0);
                break;
            case R.id.button11:
                myObj.updateGameBoard(1, 1);
                break;
            case R.id.button12:
                myObj.updateGameBoard(1, 2);
                break;
            case R.id.button20:
                myObj.updateGameBoard(2, 0);
                break;
            case R.id.button21:
                myObj.updateGameBoard(2, 1);
                break;
            case R.id.button22:
                myObj.updateGameBoard(2, 2);
                break;
            case R.id.newGame:
                myObj.newGame();
                break;
        }
    }


    public void setORed(View v){
        view.setColorOfO("red");
        Button button1 = (Button) findViewById(R.id.O_Red);
        Button button2 = (Button) findViewById(R.id.O_Blue);
        button1.setTextColor(Color.RED);
        button2.setTextColor(Color.BLACK);



    }
    public void setXRed(View v){
        view.setColorOfX("red");
        Button X_red = (Button) findViewById(R.id.X_Red);
        Button button = (Button) findViewById(R.id.X_Blue);
        X_red.setTextColor(Color.RED);
        button.setTextColor(Color.BLACK);

    }
    public void setOBlue(View v){
        view.setColorOfO("blue");
        Button button1 = (Button) findViewById(R.id.O_Red);
        Button button2 = (Button) findViewById(R.id.O_Blue);
        button2.setTextColor(Color.BLUE);
        button1.setTextColor(Color.BLACK);


    }
    public void setXBlue(View v){
        view.setColorOfX("blue");
        Button X_red = (Button) findViewById(R.id.X_Red);
        Button button = (Button) findViewById(R.id.X_Blue);
        X_red.setTextColor(Color.BLACK);
        button.setTextColor(Color.BLUE);

    }


}
