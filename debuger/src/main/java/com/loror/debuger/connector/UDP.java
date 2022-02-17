package com.loror.debuger.connector;

import android.util.Log;

import com.loror.debuger.DebugConfig;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Loror on 2017/9/30.
 */
public class UDP {

    private static final String TAG = "UDP";

    public static int PORT = 20685;

    private static DatagramSocket socket;
    private static ExecutorService server;

    /**
     * 发送UDP信息
     */
    public static void send(String ip, final Msg message) {
        send(ip, message, null);
    }

    /**
     * 发送UDP信息
     */
    public static void send(String ip, final Msg message, Runnable success) {
        if (socket == null || server == null) {
            throw new IllegalStateException("you must call openService before!");
        }
        if (message == null) {
            return;
        }
        final String sendIp = ip;
        server.execute(() -> {
            try {
                InetAddress address = InetAddress.getByName(sendIp);
                byte[] temp = Code.code(message, DebugConfig.Get.getKey());
                DatagramPacket dp = new DatagramPacket(temp, temp.length, address, PORT);
                socket.send(dp);
                if (success != null) {
                    success.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * UDP接收信息
     */
    private static void openService(UdpConnectorListener listener, String key) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (socket != null && !socket.isClosed()) {
            Log.e(TAG, "UDP service is running");
            return;
        }
        final byte[] temp = new byte[1024 * 50];
        final DatagramPacket dp = new DatagramPacket(temp, temp.length);
        try {
            socket = new DatagramSocket(PORT);
            listener.onConnect();
        } catch (SocketException e) {
            Log.e(TAG, "open udp fail:", e);
            listener.onDisConnect();
            return;
        }
        Log.e(TAG, "UDP service start");
        try {
            for (; ; ) {
                socket.receive(dp);
                final Msg ms = Code.decode(temp, 0, dp.getLength(), key);
                if (ms == null) {
                    Log.e(TAG, "receive an unexpected message");
                    continue;
                }
                String fromIP = dp.getAddress().getHostAddress();
                if (!innerIP(fromIP)) {
                    //禁止远程调用
                    continue;
                }
                server.execute(() -> {
                    CmdMsg.cmd(fromIP, ms, listener);
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "udp socket exception:", e);
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                Log.e(TAG, "udp socket already closed");
            }
        }
        socket = null;
    }

    private static boolean innerIP(String ip) {
        Pattern reg = Pattern.compile("^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$");
        Matcher match = reg.matcher(ip);
        return match.find();
    }

    /**
     * 启动UDP
     */
    public static synchronized void start(UdpConnectorListener listener) {
        if (server != null) {
            return;
        }
        server = Executors.newFixedThreadPool(1);
        new Thread(() -> openService(listener, DebugConfig.Get.getKey())).start();
    }

    /**
     * 停止UDP
     */
    public static synchronized void stop() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            socket = null;
        }
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }
}
