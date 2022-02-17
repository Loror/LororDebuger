package com.loror.debuger.connector;

import android.util.Log;

import com.loror.debuger.DebugConfig;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCP {

    private static final String TAG = "TCP";

    private static ServerSocket socket;
    private static ExecutorService server;

    public static boolean isBusy() {
        return socket != null;
    }

    /**
     * 发送数据
     */
    public static void send(String ip, InputStream data) {
        send(ip, data, null);
    }

    /**
     * 发送数据
     */
    public static void send(String ip, InputStream data, Runnable success) {
        synchronized (TCP.class) {
            if (server == null) {
                server = Executors.newFixedThreadPool(1);
            }
        }
        server.execute(() -> {
            try {
                Socket socket = new Socket(ip, DebugConfig.Get.getPort() + 1);
                byte[] temp = new byte[1024 * 30];
                int total;
                OutputStream outputStream = socket.getOutputStream();
                while ((total = data.read(temp)) != -1) {
                    outputStream.write(temp, 0, total);
                }
                data.close();
                socket.close();
                if (success != null) {
                    success.run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void openService(String name, ConnectorListener listener) {
        if (socket != null && !socket.isClosed()) {
            Log.e(TAG, "TCP service is running");
            return;
        }
        try {
            socket = new ServerSocket(DebugConfig.Get.getPort() + 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "open tcp fail:", e);
            listener.onDisConnect();
        }
        try {
            socket.setSoTimeout(5000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        listener.onConnect();
        Log.e(TAG, "TCP service start");
        try {
            Socket so = socket.accept();
            InputStream inputStream = so.getInputStream();
            byte[] temp = new byte[1024 * 30];
            int total;
            File dir = new File(DebugConfig.Get.getSaveDir());
            boolean ready = true;
            if (!dir.exists()) {
                ready = dir.mkdirs();
            }
            if (ready) {
                OutputStream outputStream = new FileOutputStream(new File(dir, name));
                while ((total = inputStream.read(temp)) != -1) {
                    outputStream.write(temp, 0, total);
                }
                outputStream.close();
            }
            so.close();
            Log.e(TAG, "finish receive file => " + name);
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                System.err.println("wait timed out");
            } else {
                e.printStackTrace();
            }
        }
        stop();
    }

    /**
     * 启动TCP
     */
    public static synchronized void start(String name, ConnectorListener listener) {
        if (server != null) {
            return;
        }
        server = Executors.newFixedThreadPool(1);
        new Thread(() -> openService(name, listener)).start();
    }

    /**
     * 停止TCP
     */
    public static synchronized void stop() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }
}
