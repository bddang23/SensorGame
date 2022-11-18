package com.example.sensorgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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
    }
}