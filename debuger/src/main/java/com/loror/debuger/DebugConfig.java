package com.loror.debuger;

import android.os.Environment;
import android.view.View;

/**
 * 配置信息
 */
public class DebugConfig {

    private static boolean allowRemote;
    private static int icon;
    private static boolean exitWhenSelect;
    private static View.OnClickListener onSelectClick;
    private static String[] select;
    private static String version = "1.0.0";
    private static String device = "unkown";
    private static int sdk = 9;
    private static String saveDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String key = "lororDebuger";

    public static void setAllowRemote(boolean allowRemote) {
        DebugConfig.allowRemote = allowRemote;
    }

    public static void setIcon(int icon) {
        DebugConfig.icon = icon;
    }

    public static void setExitWhenSelect(boolean exitWhenSelect) {
        DebugConfig.exitWhenSelect = exitWhenSelect;
    }

    public static void setOnSelectClick(View.OnClickListener onSelectClick) {
        DebugConfig.onSelectClick = onSelectClick;
    }

    public static void setSelect(String[] select) {
        DebugConfig.select = select;
    }

    public static void setVersion(String version) {
        DebugConfig.version = version;
    }

    public static void setDevice(String device) {
        DebugConfig.device = device;
    }

    public static void setSdk(int sdk) {
        DebugConfig.sdk = sdk;
    }

    public static void setSaveDir(String saveDir) {
        DebugConfig.saveDir = saveDir;
    }

    public static void setKey(String key) {
        DebugConfig.key = key;
    }

    public static class Get {

        public static boolean isAllowRemote() {
            return allowRemote;
        }

        public static int getIcon() {
            return icon;
        }

        public static boolean isExitWhenSelect() {
            return exitWhenSelect;
        }

        public static View.OnClickListener getOnSelectClick() {
            return onSelectClick;
        }

        public static String[] getSelect() {
            return select == null ? new String[]{"请选择"} : select;
        }

        public static String getVersion() {
            return version;
        }

        public static String getDevice() {
            return device;
        }

        public static int getSdk() {
            return sdk;
        }

        public static String getSaveDir() {
            return saveDir;
        }

        public static String getKey() {
            return key;
        }
    }
}
