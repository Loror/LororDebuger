package com.loror.debuger;

import com.loror.debuger.connector.Msg;
import com.loror.debuger.connector.UDP;

public class CmdHandler {

    private final String ip;
    private final int type;
    private final int number;

    public CmdHandler(String ip, int type, int number) {
        this.ip = ip;
        this.type = type;
        this.number = number;
    }

    /**
     * 响应远程
     */
    public void reply(String message, boolean keepMessage) {
        UDP.send(ip, new Msg(type, message, number, keepMessage));
    }

    public void reply(String message) {
        reply(message, false);
    }
}
