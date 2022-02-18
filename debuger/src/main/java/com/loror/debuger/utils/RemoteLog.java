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

    private static final int LOG_MAX_LENGTH = 2000;
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
        log("D", tag, message);
        append(tag + ":" + message);
    }

    public static void e(String tag, String message) {
        log("E", tag, message);
        append(tag + ":" + message);
    }

    public static void e(String tag, String message, Throwable e) {
        message = message + " " + Log.getStackTraceString(e);
        log("E", tag, message);
        append(tag + ":" + message);
    }

    private static void log(String level, String tag, String message) {
        if (message == null) {
            return;
        }
        if (message.length() < LOG_MAX_LENGTH) {
            switch (level) {
                case "E":
                    Log.e(tag, message);
                    break;
                case "D":
                    Log.d(tag, message);
                    break;
            }
        } else {
            int strLength = message.length();
            int start = 0;
            int end = LOG_MAX_LENGTH;
            for (int i = 0; i < 500; i++) {
                if (strLength > end) {
                    Log.e(tag + i, message.substring(start, end));
                    start = end;
                    end = end + LOG_MAX_LENGTH;
                } else {
                    switch (level) {
                        case "E":
                            Log.e(tag + i, message.substring(start, strLength));
                            break;
                        case "D":
                            Log.d(tag + i, message.substring(start, strLength));
                            break;
                    }
                    break;
                }
            }
        }
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
