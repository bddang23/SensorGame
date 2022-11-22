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
                    checkForShake();
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

    private void checkForShake() {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        switch(sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                if (STAGE == 2){
                    checkForShake();
                }else if (STAGE ==3){
                    checkForJump();
                }
                break;
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
