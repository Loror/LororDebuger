package com.loror.example;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.loror.debuger.DebugService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, DebugService.class));
        findViewById(R.id.text).setOnClickListener(v -> {
            DebugService.showIcon(this);
        });
    }
}