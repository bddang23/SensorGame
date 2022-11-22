package com.example.sensorgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ImageButton btnHint;
    TextView txtCommand;
    ImageView ivStatus;
    SensorManager sensorManager;
    Sensor accSensor;
    Sensor lightSensor;
    long initialTime;
    long endTime;

    boolean jumpStarted;
    boolean jumpReleased;
    long jumpStartTime;
    double v0;
    public static final double G = -10;
    public static final int ERROR_MAX = 7;  // Error counter limit. Higher the MAX, more likely to enter error state
    int errorCounter;
    double errorCheck;
    Set<Double> vSet;
    public static final int V0_CHECK = 5; // Number of times to record acceleration values to average out for v0
    int v0Counter;
    Set<Double> aSet;

    public static final String TAG = "sensorAPP";
    public static int STAGE =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHint = findViewById(R.id.btnHint);
        txtCommand = findViewById(R.id.txtCommand);
        ivStatus = findViewById(R.id.ivStatus);

        // Initial values for jump logic
        jumpStarted = false;
        jumpReleased = false;
        jumpStartTime = 0;
        v0 = 0;
        errorCounter = ERROR_MAX;
        errorCheck = 0;
//        vSet = new HashSet<>();
//        v0Counter = V0_CHECK;
        aSet = new HashSet<>();


        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener((SensorEventListener) this,accSensor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener((SensorEventListener) this,lightSensor,SensorManager.SENSOR_DELAY_UI);
        startGame();
        checkForSleep();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch(sensorEvent.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                if (STAGE == 2){
                    checkForShake();
                }else if (STAGE ==3){
                    checkForJump(sensorEvent);
                }
                break;
            case Sensor.TYPE_LIGHT:
                if (STAGE == 1){
                    checkForSleep();
                }
                break;
        }
    }

    // Logic for checking for jumps. Slightly busted
    private void checkForJump(SensorEvent sensorEvent) {
        // Get values xyz, and combine them to approximate acceleration + gravity
        float[] values = sensorEvent.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];
//        double combined = Math.abs(G + /*Math.abs(x) +*/ (Math.abs(y) + Math.abs(z)));
//        double combined = Math.abs(G+ Math.sqrt(z*z + y*y));   // Hypotenuse of Z and Y axes
        double combined = Math.abs(G+ Math.abs(z));

        // Track in between values (Debug)
//        if (jumpStarted /*&& !jumpReleased*/)
//            Log.d(TAG,/*"x:"+x+" y:"+y+*/" z:"+z+" combined:"+combined + " time: " + (System.currentTimeMillis() - jumpStartTime) / 1000.0);

        // Add acceleration data to set
        if (jumpStarted)
            aSet.add(combined);

//        if (jumpStarted && v0Counter >= 0){
//            vSet.add(Math.abs(G + combined));
//            v0Counter--;
//        }
//        if (v0Counter <= 0 && v0 == 0){
//            double sum = 0;
//            for (Double d : vSet){
//                sum+=d;
//                Log.d(TAG,"v0 set: "+d);
//            }
//            v0 = sum / (double)vSet.size();
//        }

        // If acceleration is strong enough, either consider jump started or ended / released
        if (combined > 5){
            if (!jumpStarted){
                jumpStartTime = System.currentTimeMillis();
//                v0 = Math.abs(G + combined) / 60.0;
                Log.d(TAG,"Jump started...");
                txtCommand.setText("Jumping...?");

                jumpStarted = true;
            }
        }
        if (combined < 5 && jumpStarted){
            if (jumpReleased){
                double sum = 0;
                for (Double d : aSet){
                    Log.d(TAG,"individual A: " + d);
                    sum+=d;
                }
                double avgAcc = (sum / (double)aSet.size());

                // Get full air time in seconds, t.
                double t = ((System.currentTimeMillis() - jumpStartTime) / 1000.0);

                // Get v0 for height equation
                v0 = (avgAcc * t)/2.0;

                // Plug in values to height equation.
                double height = getHeight(v0,avgAcc);

                // If jump is too short (in either height or time) cancel it
                Log.d(TAG,"A: " + avgAcc + "T: "+ t + " V0: " + v0 + " H: " + height + "m");
                if (height > .13 && t > .3){
                    txtCommand.setText("Jump complete!");
                    Log.d(TAG,"FINISHED JUMP");

                    sensorManager.unregisterListener(this);

                    // TODO: Pass end height
                    endGame();
                }
                else {
                    cancelJump("bad jump");
                }
            }
        }
        // Logic to check for "error" state in which jump state is entered by mistake.
        // This is triggered if phone appears to be sitting in the same state too long
        // If counter trips too many times, cancel jump.
//        else if (jumpStarted && (Math.abs(combined) < 1.5 /*|| Math.abs(combined - errorCheck) < 2.5)*/)){
        else if (jumpStarted && (/*y < 1 ||*/ Math.abs(G + Math.abs(z)) < 1.5)){
//            Log.d(TAG,"errorCounter: " + errorCounter + " gravCheck:" + (Math.abs(combined)) + " errorCheck:"+ Math.abs(combined - errorCheck));
            errorCounter--;
            errorCheck = combined;
            if (errorCounter <= 0){
                cancelJump("error state");
                errorCounter = ERROR_MAX;
            }
        }
        // If jump has started, assume jump has been "released" / phone was let go
        else if (jumpStarted && !jumpReleased){
            jumpReleased = true;
            errorCounter = ERROR_MAX;
        }
    }

    // Logic for calculating approx. height of jump based on assumed initial velocity, time (from release to peak) and gravity
    private double getHeight(double v0y, double t, double g){
        return (v0y * t) - (.5 * g * (t * t));
    }

    private double getHeight(double v0, double g){
        return (v0 * v0) / (2 * g);
    }

    // Cancel the jump and reset the level
    private void cancelJump(String reason){
        Log.d(TAG,"jump cancelled: " + reason);
        jumpStarted = false;
        jumpReleased = false;
        txtCommand.setText("Tim is energetic and \nwant to jump ...");
//        v0Counter = V0_CHECK;
        v0 = 0;
        aSet = new HashSet<>();
    }

    private void checkForSleep() {
        STAGE = 2;
    }

    private void checkForShake() {
        STAGE = 3;
        txtCommand.setText("Tim is energetic and \nwant to jump ...");
        ivStatus.setImageResource(R.drawable.jump);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        switch(sensor.getType()){
            case Sensor.TYPE_LIGHT:
                if (STAGE == 1){
                    checkForSleep();
                }
                break;
        }
    }

    private void startGame() {
        STAGE = 1;
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
        sensorManager.unregisterListener(this);
    }
}
