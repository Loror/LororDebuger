package com.loror.debuger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loror.debuger.utils.DebugUtils;

import java.util.ArrayList;

/**
 * Created by Loror on 2017/10/23.
 */
public class DebugDialog extends Dialog {

    private final Context context;

    public DebugDialog(Context context) {
        super(context);
        this.context = context;
    }

    private final ArrayList<Debug> debugs = new ArrayList<>();
    private DebugAdapter adapter;
    private int debugType;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (debugType == 0) {
                refreshApis();
            } else {
                refreshLogs();
            }
        }
    };

    private void refreshApis() {
        debugs.clear();
        debugs.addAll(DebugUtils.getAllDesc(context));
        adapter.notifyDataSetChanged();
    }

    private void refreshLogs() {
        debugs.clear();
        debugs.addAll(DebugUtils.getAllBugsDesc(context));
        adapter.notifyDataSetChanged();
    }

    /**
     * 列表
     */
    private void dialogList() {
        final String[] items = DebugConfig.Get.getSelect();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, 3);
        builder.setTitle("选择");
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, (dialog, which) -> {
            DebugUtils.saveType(context, which);
            Toast.makeText(context, "请重启app", Toast.LENGTH_SHORT).show();
            View.OnClickListener onSelectClick = DebugConfig.Get.getOnSelectClick();
            if (onSelectClick != null) {
                onSelectClick.onClick(null);
            }
            if (DebugConfig.Get.isExitWhenSelect()) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder.setPositiveButton("取消", (dialog, which) -> dialog.dismiss());
        Dialog dialog = builder.create();
        if (!(context instanceof Activity)) {
            if (Build.VERSION.SDK_INT >= 26) {
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
            } else {
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
            }
        }
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_debug);
        adapter = new DebugAdapter(context, debugs, this);
        ListView listView = (ListView) findViewById(R.id.list);
        final TextView tv_type = (TextView) findViewById(R.id.tv_type);
        findViewById(R.id.tv_clear).setOnClickListener(v -> DebugUtils.clear(context));
        findViewById(R.id.tv_debug).setOnClickListener(v -> dialogList());
        tv_type.setOnClickListener(v -> {
            if (debugType == 0) {
                tv_type.setText("·崩溃日志");
                debugType = 1;
                adapter.setDebugType(debugType);
                refreshLogs();
            } else {
                tv_type.setText("·接口");
                debugType = 0;
                adapter.setDebugType(debugType);
                refreshApis();
            }
        });
        listView.setAdapter(adapter);
        refreshApis();
        getContext().registerReceiver(receiver, new IntentFilter(DebugUtils.FILTER));
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            getContext().unregisterReceiver(receiver);
        } catch (Exception ignored) {

        }
    }

}
