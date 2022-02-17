package com.loror.debuger;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Loror on 2017/10/23.
 */
public class DebugAdapter extends BaseAdapter {

    private final Context context;
    private final DebugDialog debugDialog;
    private final ArrayList<Debug> debugs;
    private int debugType;

    public DebugAdapter(Context context, ArrayList<Debug> debugs, DebugDialog debugDialog) {
        this.context = context;
        this.debugs = debugs;
        this.debugDialog = debugDialog;
    }

    public void setDebugType(int debugType) {
        this.debugType = debugType;
    }

    public void bindView(ViewHolder holder, final int position) {
        holder.tv_url.setText(debugs.get(position).url);
        holder.tv_param.setText(debugs.get(position).parmas);
        holder.tv_code.setText(debugs.get(position).code + "");
        if (debugType == 1) {
            holder.tv_url_name.setText("创建时间：");
            holder.tv_param_name.setText("文件名：");
            holder.tv_result_name.setText("崩溃日志：");
            holder.ll_code.setVisibility(View.GONE);
        } else {
            holder.tv_url_name.setText("url地址：");
            holder.tv_param_name.setText("参数：");
            holder.tv_result_name.setText("返回结果：");
            holder.ll_code.setVisibility(View.VISIBLE);
        }
        holder.tv_result.setText(TextUtils.isEmpty(debugs.get(position).result) ? "无" : debugs.get(position).result.length() > 150 ? debugs.get(position).result.substring(0, 150) : debugs.get(position).result);
        holder.tv_url.setOnClickListener(v -> {
            context.startActivity(new Intent(context, DebugMsgActivity.class)
                    .putExtra("errorMsg", debugs.get(position).url)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            debugDialog.dismiss();
        });
        holder.tv_param.setOnClickListener(v -> {
            context.startActivity(new Intent(context, DebugMsgActivity.class)
                    .putExtra("errorMsg", debugs.get(position).parmas)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            debugDialog.dismiss();
        });
        holder.tv_result.setOnClickListener(v -> {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(debugs.get(position).result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String errorMsg = debugs.get(position).result;
            if (errorMsg != null && errorMsg.length() > 300000) {
                DebugMsgActivity.longErrorMsg = errorMsg;
                errorMsg = null;
            }
            context.startActivity(new Intent(context, DebugMsgActivity.class)
                    .putExtra("errorMsg", errorMsg)
                    .putExtra("webShow", jsonObject == null)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            debugDialog.dismiss();
        });
    }

    @Override
    public int getCount() {
        return debugs.size();
    }

    @Override
    public Object getItem(int position) {
        return debugs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_debug, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        bindView(holder, position);
        return convertView;
    }

    static class ViewHolder {
        TextView tv_url;
        TextView tv_param;
        TextView tv_code;
        TextView tv_result;
        TextView tv_url_name;
        TextView tv_param_name;
        TextView tv_result_name;
        LinearLayout ll_code;

        ViewHolder(View view) {
            tv_url = view.findViewById(R.id.tv_url);
            tv_param = view.findViewById(R.id.tv_param);
            tv_code = view.findViewById(R.id.tv_code);
            tv_result = view.findViewById(R.id.tv_result);
            tv_url_name = view.findViewById(R.id.tv_url_name);
            tv_param_name = view.findViewById(R.id.tv_param_name);
            tv_result_name = view.findViewById(R.id.tv_result_name);
            ll_code = view.findViewById(R.id.ll_code);
        }
    }
}
