package com.loror.debuger.connector;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class Msg {

    private int type;
    private String message;
    private int number;
    private final boolean keepMessage;

    public Msg() {
        keepMessage = false;
    }

    public Msg(int type, String message, int number) {
        this(type, message, number, false);
    }

    public Msg(int type, String message, int number, boolean keepMessage) {
        this.type = type;
        this.message = message;
        this.number = number;
        this.keepMessage = keepMessage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @NotNull
    @Override
    public String toString() {
        if (keepMessage) {
            return "{\"type\":" + type +
                    ",\"message\":" + message +
                    ",\"number\":" + number +
                    '}';
        } else {
            return "{\"type\":" + type +
                    ",\"message\":\"" + message + '\"' +
                    ",\"number\":" + number +
                    '}';
        }
    }

    public static Msg fromJson(String json) {
        if (json == null || json.length() == 0) {
            return null;
        }
        Msg msg = new Msg();
        try {
            JSONObject object = new JSONObject(json);
            msg.type = object.getInt("type");
            msg.message = object.getString("message");
            msg.number = object.getInt("number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
