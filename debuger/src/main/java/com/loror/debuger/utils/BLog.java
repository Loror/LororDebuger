package com.loror.debuger.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.loror.debuger.DebugConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Loror on 2017/8/18.
 */

public class BLog {

    private BLog() {

    }

    //可以全局控制是否打印log日志
    private static boolean isPrintLog = true;
    private static String dir;
    //用于格式化日期,作为日志文件名的一部分
    private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);

    public static void setPrintLog(boolean isPrintLog) {
        BLog.isPrintLog = isPrintLog;
    }

    public static void setSaveDir(String dir) {
        BLog.dir = dir;
    }

    public static String getSaveDir() {
        if (TextUtils.isEmpty(dir)) {
            return DebugConfig.Get.getSaveDir();
        }
        return dir;
    }

    /**
     * 写出错误日志到sd卡
     */
    public static boolean e(String message) {
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = getSaveDir();
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(message.getBytes());
                fos.close();
                return true;
            }
        } catch (Exception e) {
            Log.e("BLog", "an error occured while writing file...", e);
        }
        return false;
    }

    /**
     * 写出日志到sd卡
     */
    public static boolean d(String message) {
        if (!isPrintLog) {
            return false;
        }
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "log-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = getSaveDir();
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(message.getBytes());
                fos.close();
                return true;
            }
        } catch (Exception e) {
            Log.e("BLog", "an error occured while writing file...", e);
        }
        return false;
    }
}
