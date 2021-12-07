package com.loror.example;

import android.app.Application;
import android.content.Intent;

import com.loror.debuger.BLog;
import com.loror.debuger.CrashHandler;
import com.loror.debuger.SensorManagerUtil;
import com.loror.debuger.ViewService;

import java.io.File;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        File dir = getExternalCacheDir();
        if (dir != null) {
            BLog.setSaveDir(dir.getAbsolutePath() + File.separator + "crash" + File.separator);
            CrashHandler.getInstance().init(this);
        }
        SensorManagerUtil sensor = new SensorManagerUtil(this);
        sensor.setOnShakeListener(() -> {
            startService(new Intent(this, ViewService.class));
            sensor.stop();
        });
        sensor.start();
    }
}
