package com.example.sensorgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class result extends AppCompatActivity {
    TextView txtYourTime;
    TextView txtYourHeight;
    TextView txtBestTime;
    TextView txtBestHeight;

    Button btnReturn;
    Button btnReplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        txtYourTime = findViewById(R.id.txtYourTime);
        txtYourHeight = findViewById(R.id.txtYourHeight);
        txtBestTime = findViewById(R.id.txtBestTime);
        txtBestHeight = findViewById(R.id.txtBestHeight);

        btnReturn =findViewById(R.id.btnReturn);
        btnReplay =findViewById(R.id.btnReplay);

        //This is the logic that will display the time that the user just earned
        SharedPreferences sp = getSharedPreferences("ResultsInfo", MODE_PRIVATE);

        setUpTimeScore(sp);
        setUpHeightScore(sp);

        btnReturn.setOnClickListener(view -> {
            Intent intent = new Intent(this, StartPage.class);
            startActivity(intent);
        });
        btnReplay.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }

    private void setUpHeightScore(SharedPreferences sp) {
        SharedPreferences.Editor edit = sp.edit();
        float heightRecorded = sp.getFloat("jumpHeight", 0);
        txtYourHeight.setText(String.format("%.2f",heightRecorded)+" meters");

               /*
        if there is no high score OR if the user's score is better than the high score
        update the high score TextField and put high score into SharePreferences
         */
        if (sp.getFloat("BestHeightScore", 0) == 0 || sp.getFloat("BestHeightScore", 0) < heightRecorded){
            //Set the users score as the high score and update text
            edit.putFloat("BestHeightScore",heightRecorded);
            edit.apply();
            txtBestHeight.setText(String.format("%.2f",heightRecorded)+" meters");
        }
        else{
            //Use the high score currently in SharedPreferences to update TextField
            float bestHeight = sp.getFloat("BestHeightScore", 0);
            txtBestHeight.setText(String.format("%.2f",bestHeight)+" meters");
        }
    }

    private void setUpTimeScore(SharedPreferences sp) {
        long initialTime = sp.getLong("initialTime", 0);
        long endTime = sp.getLong("endTime", 0);

        //Total time taken to perform actions
        long totalTime = endTime - initialTime;

        //Convert time to minutes and seconds, then update the text fields
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60;

        //Set the text field with the users earned time
        txtYourTime.setText("0" + minutes + " minutes " + seconds + " seconds");

        //Check the time earned against the high score
        checkTimeHighScore(totalTime, sp);
    }

    //Method will check the time earned against the best time earned and set the text accordingly
    public void checkTimeHighScore(long timeEarned,  SharedPreferences sp){
        SharedPreferences.Editor edit = sp.edit();
        long minutes;
        long seconds;
        Log.i("sensorAPP", sp.getLong("timeHighScore", 0) + " ");

        /*
        if there is no high score OR if the user's score is better than the high score
        update the high score TextField and put high score into SharePreferences
         */
        if (sp.getLong("timeHighScore", 0) == 0 || sp.getLong("timeHighScore", 0) > timeEarned){
            //Set the users score as the high score and update text
            edit.putLong("timeHighScore", timeEarned);
            edit.apply();
            //Convert time to minutes and seconds, then update the text fields
            minutes = TimeUnit.MILLISECONDS.toMinutes(timeEarned);
            seconds = TimeUnit.MILLISECONDS.toSeconds(timeEarned) % 60;
            txtBestTime.setText("0" + minutes  + " minutes " +  seconds + "  seconds");
        }
        else{
            //Use the high score currently in SharedPreferences to update TextField
            long totalTime = sp.getLong("timeHighScore", 0);

            //Convert time to minutes and seconds, then update the text fields
            minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime);
            seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60;

            txtBestTime.setText("0" + minutes + " minutes " + seconds + " seconds");
        }


    }
}
