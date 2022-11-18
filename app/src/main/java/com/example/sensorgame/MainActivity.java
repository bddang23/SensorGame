package com.example.sensorgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ImageButton btnHint;
    TextView txtCommand;
    ImageView ivStatus;
    SensorManager sensorManager;
    Sensor accSensor;
    Sensor lightSensor;

    public static final String TAG = "sensorAPP";
    public static int STAGE =0;

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
        sensorManager.registerListener((SensorEventListener) this,lightSensor,SensorManager.SENSOR_DELAY_UI);

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
                    checkForSleep();
                }
                break;
        }
    }

    private void checkForJump() {
    }

    private void checkForSleep() {
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
                    checkForSleep();
                }
                break;

        }
    }

    private void startGame() {
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener((SensorListener) this);
    }
}