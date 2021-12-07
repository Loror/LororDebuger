package com.loror.debuger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 用于显示悬浮球的Service
 */
public class ViewService extends Service {

    private static int icon;
    protected static boolean exitWhenSelect;
    protected static View.OnClickListener onSelectClick;
    protected static String[] select;

    private WindowManager windowManager;
    private DebugView floatBall;

    public static void setIcon(int icon) {
        ViewService.icon = icon;
    }

    public static void setExitWhenSelect(boolean exitWhenSelect) {
        ViewService.exitWhenSelect = exitWhenSelect;
    }

    public static void setOnSelectClick(View.OnClickListener onSelectClick) {
        ViewService.onSelectClick = onSelectClick;
    }

    public static void setSelect(String[] select) {
        ViewService.select = select;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        floatBall = new DebugView(this);

        try {
            try {
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
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        try {
            windowManager.removeView(floatBall);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * DPתPX
     */
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
