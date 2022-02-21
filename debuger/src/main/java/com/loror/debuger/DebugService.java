package com.loror.debuger;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.loror.debuger.connector.CmdMsg;
import com.loror.debuger.connector.UDP;
import com.loror.debuger.connector.UdpConnectorListener;
import com.loror.debuger.utils.DebugUtils;
import com.loror.debuger.utils.FileUtil;

import org.json.JSONArray;

import java.io.File;
import java.util.List;

/**
 * 用于显示悬浮球的Service
 */
public class DebugService extends Service {

    private WindowManager windowManager;
    private DebugView floatBall;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showDebugIcon();
        }
    };

    private static final String FILTER = "DebugService.REC";

    /**
     * 显示icon
     */
    public static void showIcon(Context context) {
        context.sendBroadcast(new Intent(FILTER));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        registerReceiver(receiver, new IntentFilter(FILTER));
        if (DebugConfig.Get.isAllowRemote()) {
            Handler handler = new Handler();
            UDP.start(new UdpConnectorListener() {

                @Override
                public void onFileReady(String ip) {
                    //远程暂不支持主动发送
                }

                @Override
                public void onFileOpen(File file) {
                    handler.post(() -> {
                        if (file.getName().endsWith(".ldata")) {
                            String msg = FileUtil.readFile(file);
                            msg = TextUtils.isEmpty(msg) ? "无" : msg;
                            if (msg.length() > 300000) {
                                DebugMsgActivity.longErrorMsg = msg;
                                msg = null;
                            }
                            startActivity(new Intent(DebugService.this, DebugMsgActivity.class)
                                    .putExtra("errorMsg", msg)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            return;
                        }
                        OnCmdListener listener = DebugConfig.Get.getOnCmdListener();
                        if (listener != null) {
                            listener.openFile(file);
                        }
                    });
                }

                @Override
                public String apis() {
                    JSONArray jsonArray = new JSONArray();
                    List<Debug> debugs = DebugUtils.getAllDesc(DebugService.this);
                    for (Debug debug : debugs) {
                        jsonArray.put(debug.toJSONObject());
                    }
                    return jsonArray.toString();
                }

                @Override
                public String cmd(String ip, String cmd, int number) {
                    handler.post(() -> {
                        if ("icon".equals(cmd)) {
                            showDebugIcon();
                        } else {
                            OnCmdListener listener = DebugConfig.Get.getOnCmdListener();
                            if (listener != null) {
                                listener.onCmd(new CmdHandler(ip, CmdMsg.TYPE_CMD_R, number, cmd));
                            }
                        }
                    });
                    return null;
                }

                @Override
                public void onDevice(String ip, String info) {

                }

                @Override
                public void onUrlOpen(String url) {
                    handler.post(() -> {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                    });
                }

                @Override
                public void onEnv(int select) {
                    handler.post(() -> {
                        DebugUtils.saveType(DebugService.this, select);
                        Toast.makeText(DebugService.this, "环境已切换，请重启app", Toast.LENGTH_SHORT).show();
                        View.OnClickListener onSelectClick = DebugConfig.Get.getOnSelectClick();
                        if (onSelectClick != null) {
                            onSelectClick.onClick(null);
                        }
                        if (DebugConfig.Get.isExitWhenSelect()) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
                }

                @Override
                public void onAlert(String message) {
                    if (!TextUtils.isEmpty(message)) {
                        handler.post(() -> {
                            Toast.makeText(DebugService.this, message, Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onConnect() {
                    Log.e("DEBUG", "onConnect");
                }

                @Override
                public void onDisConnect() {
                    Log.e("DEBUG", "onDisConnect");
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (floatBall != null) {
                windowManager.removeView(floatBall);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void showDebugIcon() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        floatBall = new DebugView(this);
        try {
            try {
                int icon = DebugConfig.Get.getIcon();
                floatBall.setImageResource(icon != 0 ? icon : R.drawable.huaji);
            } catch (Exception e) {
                floatBall.setImageResource(R.drawable.huaji);
            }
            floatBall.setBackgroundColor(0x00000000);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= 26) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.width = Dp2Px(this, 30);
            layoutParams.height = Dp2Px(this, 30);
            layoutParams.flags = 40;
            layoutParams.x = screenWidth / 2 - Dp2Px(this, 20);
            layoutParams.y = -screenHeight / 2 + Dp2Px(this, 100);
            layoutParams.format = PixelFormat.RGBA_8888 | PixelFormat.TRANSLUCENT;
            windowManager.addView(floatBall, layoutParams);
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(this, "请先开启系统弹窗权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * DP->PX
     */
    private static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
