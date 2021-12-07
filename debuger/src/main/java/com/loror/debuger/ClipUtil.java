package com.loror.debuger;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Date: 2019/3/26 18:05
 * Description: ${DESCRIPTION}
 */
public class ClipUtil {
    /**
     * 拷贝到剪切板
     */
    public static void copyToClip(Context context, String text) {
        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
    }
}
