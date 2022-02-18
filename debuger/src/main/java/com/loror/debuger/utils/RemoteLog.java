package com.loror.debuger.utils;

import android.util.Log;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 远程log
 */
public class RemoteLog {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final List<String> logs = new ArrayList<>();
    private static OnLog onLog;

    public interface OnLog {
        void onLog(String log);
    }

    /**
     * 内部使用，请注意是否必须调用
     */
    @Deprecated
    public static void setOnLog(OnLog onLog) {
        RemoteLog.onLog = onLog;
    }

    public static void d(String tag, String message) {
        Log.d(tag, message);
        append(tag + ":" + message);
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
        append(tag + ":" + message);
    }

    public static void e(String tag, String message, Throwable e) {
        Log.e(tag, message, e);
        append(tag + ":" + message + " " + Log.getStackTraceString(e));
    }

    private static void append(String log) {
        String time = format.format(new Date());
        if (onLog != null) {
            try {
                onLog.onLog(time + " " + log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            synchronized (RemoteLog.class) {
                if (logs.size() > 5) {
                    logs.remove(0);
                }
                if (log.length() > 200) {
                    log = log.substring(0, 200) + "...";
                }
                logs.add(time + " " + log);
            }
        }
    }

    public static class Get {
        public static List<String> pop() {
            List<String> l = new ArrayList<>(logs.size() + 1);
            synchronized (RemoteLog.class) {
                l.addAll(logs);
                logs.clear();
            }
            return l;
        }

        public static String popToString() {
            JSONArray jsonArray = new JSONArray();
            List<String> logs = pop();
            for (String l : logs) {
                jsonArray.put(l);
            }
            return jsonArray.toString();
        }
    }
}
