package com.loror.debuger.connector;

import com.loror.debuger.DebugConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CmdMsg {

    public static final int TYPE_RESP = 0;//响应信息
    public static final int TYPE_INFO = 1;//获取设备信息
    public static final int TYPE_INFO_R = 2;//设备信息
    public static final int TYPE_RCMD = 3;//资源操作
    public static final int TYPE_RCMD_R = 4;//资源操作响应
    public static final int TYPE_SEND = 5;//发送数据
    public static final int TYPE_SEND_R = 6;//发送响应 0 禁止 1 允许
    public static final int TYPE_REC = 7;//接收数据
    public static final int TYPE_REC_R = 8;//接收响应
    public static final int TYPE_APIS = 9;//调试
    public static final int TYPE_APIS_R = 10;//接收调试数据
    public static final int TYPE_URL = 11;//开启浏览器

    /**
     * 执行命令
     */
    public static void cmd(String fromIP, Msg msg, UdpConnectorListener listener) {
        switch (msg.getType()) {
            case TYPE_INFO: {
                UDP.send(fromIP, new Msg(TYPE_INFO_R, "{\\\"version\\\":\\\"" + DebugConfig.Get.getVersion()
                        + "\\\",\\\"device\\\":\\\"" + DebugConfig.Get.getDevice()
                        + "\\\",\\\"sdk\\\":" + DebugConfig.Get.getSdk() + "}", msg.getNumber()));
            }
            break;
            case TYPE_RCMD: {
                String cmd = msg.getMessage();
                if (cmd != null) {
                    try {
                        rsCmd(fromIP, cmd, msg.getNumber(), listener);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        int number = msg.getNumber();
                        if (number > 0) {
                            UDP.send(fromIP, new Msg(TYPE_RESP, e.getMessage(), number));
                        }
                    }
                }
            }
            break;
            case TYPE_INFO_R: {
                listener.onDevice(fromIP, msg.getMessage());
            }
            break;
            case TYPE_SEND: {
                if (TCP.isBusy()) {
                    UDP.send(fromIP, new Msg(TYPE_SEND_R, "0", msg.getNumber()));
                    msg.setNumber(0);
                } else {
                    int number = msg.getNumber();
                    TCP.start(msg.getMessage(), new ConnectorListener() {
                        @Override
                        public void onConnect() {
                            UDP.send(fromIP, new Msg(TYPE_SEND_R, "1", number));
                        }

                        @Override
                        public void onDisConnect() {
                            if (number > 0) {
                                UDP.send(fromIP, new Msg(TYPE_RESP, "", number));
                            }
                        }
                    });
                }
            }
            break;
            case TYPE_SEND_R: {
                listener.onFileReady(fromIP);
            }
            break;
            case TYPE_REC: {
                String name = msg.getMessage();
                if (name != null) {
                    File file = new File(new File(DebugConfig.Get.getSaveDir()), name);
                    if (file.exists()) {
                        try {
                            TCP.send(fromIP, new FileInputStream(file));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        UDP.send(fromIP, new Msg(TYPE_REC_R, name + " not exits", msg.getNumber()));
                    }
                } else if (msg.getNumber() > 0) {
                    UDP.send(fromIP, new Msg(TYPE_REC_R, "empty name", msg.getNumber()));
                }
            }
            break;
            case TYPE_APIS: {
                UDP.send(fromIP, new Msg(TYPE_APIS_R, listener.apis(), msg.getNumber(), true));
            }
            break;
            case TYPE_URL: {
                listener.onUrlOpen(msg.getMessage());
            }
            break;
        }
    }

    /**
     * 执行命令
     */
    private static void rsCmd(String fromIP, String cmd, int number, UdpConnectorListener listener) throws Throwable {
        String[] args = cmd.split(" ");
        switch (args[0]) {
            case "ls": {
                File dir = new File(DebugConfig.Get.getSaveDir());
                File[] files = dir.listFiles();
                StringBuilder builder = new StringBuilder("[");
                assert files != null;
                for (File f : files) {
                    builder.append("\\\"").append(f.getName()).append("\\\",");
                }
                if (builder.length() > 1) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                builder.append("]");
                UDP.send(fromIP, new Msg(TYPE_RCMD_R, builder.toString(), number));
            }
            break;
            case "pwd": {
                UDP.send(fromIP, new Msg(TYPE_RCMD_R, DebugConfig.Get.getSaveDir(), number));
            }
            break;
            case "del": {
                if (args.length >= 2) {
                    String name = cmd.substring(args[0].length() + 1);
                    File file = new File(new File(DebugConfig.Get.getSaveDir()), name);
                    if (file.exists() && file.delete()) {
                        UDP.send(fromIP, new Msg(TYPE_RCMD_R, name + " removed", number));
                    } else {
                        UDP.send(fromIP, new Msg(TYPE_RCMD_R, name + " cannot be removed", number));
                    }
                }
            }
            break;
            case "open": {
                if (args.length >= 2) {
                    String name = cmd.substring(args[0].length() + 1);
                    File file = new File(new File(DebugConfig.Get.getSaveDir()), name);
                    if (file.exists()) {
                        UDP.send(fromIP, new Msg(TYPE_RCMD_R, name + " opened", number));
                        listener.onFileOpen(file);
                    } else {
                        UDP.send(fromIP, new Msg(TYPE_RCMD_R, name + " not exits", number));
                    }
                }
            }
            break;
        }
    }
}
