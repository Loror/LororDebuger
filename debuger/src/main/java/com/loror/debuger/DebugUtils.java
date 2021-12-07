package com.loror.debuger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

    /**
     * 读取文件
     */
    public static String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * 获取所有崩溃日志
     */
    public static List<Debug> getAllBugsDesc(Context context) {
        File dirs = new File(Environment.getExternalStorageDirectory(), "crash");
        List<Debug> all = new ArrayList<>();
        if (dirs.exists()) {
            File[] files = dirs.listFiles();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss", Locale.CHINA);
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        Debug debug = new Debug();
                        debug.url = format.format(new Date(files[i].lastModified()));
                        debug.parmas = files[i].getName();
                        debug.result = readFile(files[i]);
                        all.add(debug);
                    }
                }
            }
            Collections.reverse(all);
        }
        return all;
    }

    private static List<Debug> debugs = new CopyOnWriteArrayList<>();

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
        context.sendBroadcast(new Intent("new_debug"));
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
        context.sendBroadcast(new Intent("new_debug"));
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
