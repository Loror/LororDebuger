package com.loror.debuger.connector;

import java.io.File;

/**
 * UDP监听
 */
public interface UdpConnectorListener extends ConnectorListener {

    void onFileReady(String ip);

    void onFileOpen(File file);

    String apis();

    void onDevice(String ip, String info);

    void onUrlOpen(String url);

    void onEnv(int select);
}
