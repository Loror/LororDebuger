package com.loror.example;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.loror.debuger.DebugService;
import com.loror.debugerExample.R;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, DebugService.class));
        findViewById(R.id.text).setOnClickListener(v -> {
            DebugService.showIcon(this);
        });
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
    }
}