package com.loror.debuger;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Loror on 2017/10/23.
 */

public class Debug {
    public long id;
    public String url;
    public String result;
    public int code;
    public String parmas;
    public String tag;

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("url", url);
            object.put("result", result);
            object.put("code", code);
            object.put("params", parmas);
            object.put("tag", tag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    @NotNull
    @Override
    public String toString() {
        JSONObject object = toJSONObject();
        return object.toString();
    }
}
