package com.loror.example;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static boolean copy(File from, File to) {
        try {
            FileInputStream inputStream = new FileInputStream(from);
            FileOutputStream outputStream = new FileOutputStream(to);
            byte[] temp = new byte[1024 * 100];
            int total;
            while ((total = inputStream.read(temp)) != -1) {
                outputStream.write(temp, 0, total);
            }
            inputStream.close();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void goInstall(Context context, File file) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT > 23) { // 7.0+以上版本
                Uri apkUri = FileProvider.getUriForFile(context, "com.loror.debugerExample.fileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DEBUG", "e:", e);
        }
    }
}
