package com.loror.debuger.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.loror.debuger.Debug;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Loror on 2017/10/23.
 */
public class DebugUtils {

    public static final String FILTER = "loror.new_debug";

    /**
     * 获取所有崩溃日志
     */
    public static List<Debug> getAllBugsDesc(Context context) {
        File dirs = new File(BLog.getSaveDir());
        List<Debug> all = new ArrayList<>();
        if (dirs.exists()) {
            File[] files = dirs.listFiles();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss", Locale.CHINA);
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".log")) {
                        Debug debug = new Debug();
                        debug.url = format.format(new Date(file.lastModified()));
                        debug.parmas = file.getName();
                        debug.result = FileUtil.readFile(file);
                        all.add(debug);
                    }
                }
            }
            Collections.reverse(all);
        }
        return all;
    }

    private static final List<Debug> debugs = new CopyOnWriteArrayList<>();

    /**
     * 获取所有debug信息
     */
    public static List<Debug> getAllDesc(Context context) {
        return debugs;
    }

    /**
     * 清除所有debug信息
     */
    public static void clear(Context context) {
        debugs.clear();
        File dirs = new File(Environment.getExternalStorageDirectory(), "crash");
        File[] files = dirs.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    files[i].delete();
                }
            }
        }
        context.sendBroadcast(new Intent(FILTER));
    }

    /**
     * 插入debug信息
     */
    public static void insert(Context context, String url, String params, int code, String result) {
        if (debugs.size() > 20) {
            debugs.remove(0);
        }
        Debug debug = new Debug();
        debug.url = url;
        debug.parmas = params;
        debug.code = code;
        debug.result = result;
        debug.tag = context.getClass().getSimpleName();
        debugs.add(0, debug);
        context.sendBroadcast(new Intent(FILTER));
    }

    /**
     * 存储调试类型
     */
    public static void saveType(Context context, int type) {
        SharedPreferences preferences = context.getSharedPreferences("DEBUG", Context.MODE_PRIVATE);
        preferences.edit().putInt("debug_type", type).apply();
    }

    /**
     * 获取调试类型
     */
    public static int getType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("DEBUG", Context.MODE_PRIVATE);
        return preferences.getInt("debug_type", 0);
    }

}
