package com.loror.debuger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;

/**
 * Created by Loror on 2017/10/23.
 */

public class DebugDialog extends Dialog {

    private Context context;

    public DebugDialog(Context context) {
        super(context);
        this.context = context;
    }

    private final ArrayList<Debug> debugs = new ArrayList<>();
    private DebugAdapter adapter;
    private int debugType;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (debugType == 0) {
                refreshApis();
            } else {
                refreshlogs();
            }
        }
    };

    private void refreshApis() {
        debugs.clear();
        debugs.addAll(DebugUtils.getAllDesc(context));
        adapter.notifyDataSetChanged();
    }

    private void refreshlogs() {
        debugs.clear();
        debugs.addAll(DebugUtils.getAllBugsDesc(context));
        adapter.notifyDataSetChanged();
    }

    /**
     * 列表
     */
    private void dialogList() {
        final String[] items = ViewService.select;

        AlertDialog.Builder builder = new AlertDialog.Builder(context, 3);
        builder.setTitle("选择");
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DebugUtils.saveType(context, which);
                Toast.makeText(context, "请重启app", Toast.LENGTH_SHORT).show();
                if (ViewService.onSelectClick != null) {
                    ViewService.onSelectClick.onClick(null);
                }
                if (ViewService.exitWhenSelect) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        if (Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
        } else {
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
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
        findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugUtils.clear(context);
            }
        });
        findViewById(R.id.tv_debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogList();
            }
        });
        tv_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (debugType == 0) {
                    tv_type.setText("·崩溃日志");
                    debugType = 1;
                    adapter.setDebugType(debugType);
                    refreshlogs();
                } else {
                    tv_type.setText("·接口");
                    debugType = 0;
                    adapter.setDebugType(debugType);
                    refreshApis();
                }
            }
        });
        listView.setAdapter(adapter);
        refreshApis();
        getContext().registerReceiver(receiver, new IntentFilter("new_debug"));
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
