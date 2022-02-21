package com.loror.debuger;

import java.io.File;

public interface OnCmdListener {

    /**
     * cmd
     */
    void onCmd(CmdHandler handler);

    /**
     * 开启文件
     */
    void openFile(File file);
}
