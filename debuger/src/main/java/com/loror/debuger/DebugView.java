package com.loror.debuger;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by Loror on 2017/10/27.
 * 悬浮球控件
 */
public class DebugView extends ImageView {
    private int startX, startY;

    private long times;
    private final WindowManager windowManager;
    private final Context context;

    public DebugView(Context context) {
        super(context);
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取手指按下的坐标
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                times = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                // 获取手指移动到了哪个点的坐标
                int movingX = (int) event.getRawX();
                int movingY = (int) event.getRawY();
                // 相对于上一个点，手指在X和Y方向分别移动的距离
                int dx = movingX - startX;
                int dy = movingY - startY;
                // 设置本次TextView的上 下 左 右各边与父控件的距离
//                layout(left + dx, top + dy, right + dx, bottom + dy);
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
                layoutParams.x += (int) (movingX - startX);
                layoutParams.y += (int) (movingY - startY);
                windowManager.updateViewLayout(this, layoutParams);
                // 本次移动的结尾作为下一次移动的开始
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (System.currentTimeMillis() - times < 200) {
                    final DebugDialog dialog = new DebugDialog(context);
                    //在dialog  show方法之前添加如下代码，表示该dialog是一个系统的dialog**
                    if (Build.VERSION.SDK_INT >= 26) {
                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
                    } else {
                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                    }
                    dialog.show();
                }
                break;
        }
        return true;//如果返回true,从手指接触屏幕到手指离开屏幕，将不会触发点击事件。
    }
}
