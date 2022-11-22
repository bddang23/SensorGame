package com.example.sensorgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ImageButton btnHint;
    TextView txtCommand;
    ImageView ivStatus;
    SensorManager sensorManager;
    Sensor accSensor;
    Sensor lightSensor;
    long initialTime;
    long endTime;
    //used to store the light levels on create as the most bright the room will be
    int loadTimeLight = 0;
    public static final String TAG = "sensorAPP";
    public static int STAGE =1;

    int shakes = 1;

    public static final String TAG = "sensorAPP";
    public static int STAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHint = findViewById(R.id.btnHint);
        txtCommand = findViewById(R.id.txtCommand);
        ivStatus = findViewById(R.id.ivStatus);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        //when the hint button is clicked, give a toast hint on what to do for the current stage of the game (using switch case)
        btnHint.setOnClickListener(view -> {
            switch (STAGE) {
                case 1:
                    Toast.makeText(getApplicationContext(), "Put your device in low light to make Tim go to sleep", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "Shake your phone three separate times to make Tim wake up", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "Throw your phone up in a flat position (screen side up) to make Tim jump", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener((SensorEventListener) this,accSensor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this,lightSensor,1);

        startGame();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch(sensorEvent.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                if (STAGE == 2){
                    checkForShake(sensorEvent);
                }else if (STAGE ==3){
                    checkForJump();
                }
                break;
            case Sensor.TYPE_LIGHT:
                if (STAGE == 1){
                    checkForSleep(sensorEvent);
                }
                break;
        }
    }

    private void checkForJump() {
        //endGame();
    }

    private void checkForSleep(SensorEvent sensorEvent) {
        //get the light levels on first load
        //this will be brightest the room will be
        if (loadTimeLight == 0){
            loadTimeLight = (int) sensorEvent.values[0];
            Log.d(TAG,loadTimeLight + "");
        }
        //if the current light is half of the light on load time the character is now asleep
        if((loadTimeLight/2) > (int)sensorEvent.values[0]){
            Log.d(TAG,"sleeping");
            ivStatus.setImageResource(R.drawable.sleep);
            STAGE = 2;
            txtCommand.setText("Tim is going to miss his class, Find a way to wake Tim up!");
        }
    }

    private void checkForShake(SensorEvent sensorEvent) {
        //get the x, y, and z values for the event
        float xValue = sensorEvent.values[0];
        float yValue = sensorEvent.values[1];
        float zValue = sensorEvent.values[2];
        float acceleration = 10f;
        //calculate the acceleration
        float currAcceleration = (float) Math.sqrt((double) (xValue * xValue + yValue * yValue + zValue * zValue));
        float deltaValue = currAcceleration - SensorManager.GRAVITY_EARTH;
        acceleration = acceleration * 0.9f + deltaValue;
        //if acceleration of the move/shake is greater than 15
        if (acceleration > 15) {
            //then it is a good shake, 3 shakes total are needed and for each one set a new text and image
            switch (shakes) {
                case 1:
                    txtCommand.setText("Tim is in a deep sleep, it will take multiple shakes to wake him up");
                    break;
                case 2:
                    ivStatus.setImageResource(R.drawable.wake_up);
                    txtCommand.setText("Tim is starting to wake up, but is still very drowsy...");
                    break;
                case 3:
                    ivStatus.setImageResource(R.drawable.jump);
                    txtCommand.setText("Tim is awake and energetic now, he wants to jump!");
                    //after the 3rd shake move to the next stage
                    STAGE++;
                    break;
            }
            shakes++;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        switch(sensor.getType()){
            case Sensor.TYPE_LIGHT:
                if (STAGE == 1){
                    //checkForSleep();
                }
                break;
        }
    }

    private void startGame() {
        initialTime = System.currentTimeMillis();
        Log.i("sensorGame", initialTime + "");
    }

    /*
    Method should be called when the final action is performed.
    Method uses Intent to go to results page and saves time values with sharedPreferences
     */
    public void endGame(){
        endTime = System.currentTimeMillis();
        Log.i(TAG, endTime + " " + initialTime);

        Intent i = new Intent(this, result.class);

        //Create sharedPreferences object
        SharedPreferences sharedPreferences = getSharedPreferences("ResultsInfo", MODE_PRIVATE);
        //Create editor for shared preferences
        SharedPreferences.Editor edit = sharedPreferences.edit();
        //Save time information
        edit.putLong("initialTime", initialTime);
        edit.putLong("endTime", endTime);

        edit.apply();

        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //sensorManager.unregisterListener((SensorListener) this);
    }
}
