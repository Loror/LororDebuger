package com.loror.example;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.loror.debuger.CmdHandler;
import com.loror.debuger.CrashHandler;
import com.loror.debuger.DebugConfig;
import com.loror.debuger.DebugService;
import com.loror.debuger.OnCmdListener;
import com.loror.debuger.utils.FileUtil;
import com.loror.debuger.utils.SensorManagerUtil;
import com.loror.debugerExample.BuildConfig;

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
        DebugConfig.setOnCmdListener(new OnCmdListener() {

            @Override
            public void onCmd(CmdHandler handler) {

            }

            @Override
            public void openFile(File file) {
                Log.e("DEBUG", "open file " + file.getName());
                if (file.getName().endsWith(".apk")) {
                    File down = new File("sdcard");
                    Log.e("DEBUG", "copy file " + down.getAbsolutePath());
                    if (FileUtil.copy(file, new File(down, file.getName()))) {
                        Log.e("DEBUG", "copy success");
                        FileUtils.goInstall(App.this, new File(down, file.getName()));
                    }
                }
            }
        });
    }
}
