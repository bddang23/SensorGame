package com.example.sensorgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        btnReturn.setOnClickListener(view -> {
            Intent intent = new Intent(this, StartPage.class);
            startActivity(intent);
        });
        btnReplay.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }
}