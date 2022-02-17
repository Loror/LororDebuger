package com.loror.debuger.connector;

import java.nio.charset.StandardCharsets;

public class Code {

    /**
     * 加密
     */
    public static byte[] codeByte(byte[] message, String key) {
        if (message != null && key != null) {
            int[] codes = new int[key.length()];
            int i;
            for (i = 0; i < key.length(); ++i) {
                codes[i] = key.charAt(i) % 3 + 1;
            }

            for (i = 0; i < message.length; ++i) {
                message[i] = (byte) (message[i] + codes[i % codes.length]);
            }
            byte[] result = new byte[message.length + 3];
            result[0] = 0;
            result[1] = 1;
            result[2] = 0;
            System.arraycopy(message, 0, result, 3, message.length);
            return result;
        } else {
            return null;
        }
    }

    /**
     * 解密
     */
    public static byte[] decodeByte(byte[] message, String key) {
        if (message != null && key != null) {
            if (message.length > 3 && message[0] == 0 && message[1] == 1 && message[2] == 0) {
                byte[] result = new byte[message.length - 3];
                System.arraycopy(message, 3, result, 0, result.length);
                int[] codes = new int[key.length()];
                int i;
                for (i = 0; i < key.length(); ++i) {
                    codes[i] = key.charAt(i) % 3 + 1;
                }
                for (i = 0; i < result.length; ++i) {
                    result[i] = (byte) (result[i] - codes[i % codes.length]);
                }
                return result;
            }
        }
        return null;
    }

    public static byte[] code(Msg msg, String key) {
        if (msg != null && key != null) {
            String json = msg.toString();
            byte[] temp = json.getBytes(StandardCharsets.UTF_8);
            return codeByte(temp, key);
        } else {
            return null;
        }
    }

    public static Msg decode(byte[] res, int onset, int offset, String key) {
        if (res != null && key != null) {
            if (offset >= 1 && offset <= res.length) {
                if (onset > offset) {
                    return null;
                } else {
                    byte[] bytes = new byte[offset - onset];
                    System.arraycopy(res, onset, bytes, 0, bytes.length);
                    bytes = decodeByte(bytes, key);
                    String json;
                    try {
                        json = new String(bytes, StandardCharsets.UTF_8);
                        return Msg.fromJson(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String codeString(String msg, String key) {
        byte[] message = msg.getBytes(StandardCharsets.UTF_8);
        message = codeByte(message, key);
        return new String(message);
    }

    public static String decodeString(String msg, String key) {
        byte[] message = msg.getBytes();
        message = decodeByte(message, key);
        return new String(message, StandardCharsets.UTF_8);
    }
}
