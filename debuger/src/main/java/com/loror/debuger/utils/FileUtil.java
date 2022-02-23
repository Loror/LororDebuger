package com.loror.debuger.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

    /**
     * 读取文件
     */
    public static String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * 拷贝文件
     */
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
}
