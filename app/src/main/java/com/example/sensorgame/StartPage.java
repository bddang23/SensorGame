package com.example.sensorgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class StartPage extends AppCompatActivity {

    Button btnStart;
    Button btnResetStats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);

        btnStart = findViewById(R.id.btnStart);
        btnResetStats = findViewById(R.id.btnReset);

        btnStart.setOnClickListener(v->{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        btnResetStats.setOnClickListener(v->{
            ResetStats();
        });
    }

    private void ResetStats() {
        //This is the logic that will display the time that the user just earned
        SharedPreferences sp = getSharedPreferences("ResultsInfo", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong("timeHighScore", 0);
        edit.putFloat("BestHeightScore",0);
        edit.apply();

        Toast.makeText(this, "Successfully Reset Statistics", Toast.LENGTH_SHORT).show();
    }
}