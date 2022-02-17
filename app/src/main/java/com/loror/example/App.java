package com.loror.example;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import com.loror.debuger.DebugConfig;
import com.loror.debuger.utils.BLog;
import com.loror.debuger.CrashHandler;
import com.loror.debuger.utils.SensorManagerUtil;
import com.loror.debuger.DebugService;

import java.io.File;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugConfig.setDevice(Build.DEVICE);
        DebugConfig.setSdk(Build.VERSION.SDK_INT);
        DebugConfig.setVersion(BuildConfig.VERSION_NAME);
        DebugConfig.setAllowRemote(true);
        File dir = getExternalCacheDir();
        if (dir != null) {
            File debug = new File(dir, "cache/debug");
            if (!debug.exists()) {
                debug.mkdirs();
            }
            DebugConfig.setSaveDir(debug.getAbsolutePath());
            CrashHandler.getInstance().init(this);
        }
        SensorManagerUtil sensor = new SensorManagerUtil(this);
        sensor.setOnShakeListener(() -> {
            DebugService.showIcon(this);
            sensor.stop();
        });
        sensor.start();
    }
}
