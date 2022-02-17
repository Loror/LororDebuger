package com.loror.debuger;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.loror.debuger.utils.ClipUtil;

public class DebugMsgActivity extends Activity {

    static String longErrorMsg;

    private String errorMsg;

    private  WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_msg);
        initView();
    }

    protected void initView() {
        webView = findViewById(R.id.webView);
        TextView textView = findViewById(R.id.textView);
        errorMsg = getIntent().getStringExtra("errorMsg");
        boolean webShow = getIntent().getBooleanExtra("webShow", false);
        setResult(RESULT_OK);
        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = longErrorMsg;
            longErrorMsg = null;
        }
        if (TextUtils.isEmpty(errorMsg)) {
            finish();
        }
        if (!webShow) {
            textView.setText(errorMsg);
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipUtil.copyToClip(v.getContext(), errorMsg);
                    Toast.makeText(v.getContext(), "已拷贝到粘贴板", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        } else {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDisplayZoomControls(true);
            webView.loadDataWithBaseURL(null, errorMsg, "text/html", getEncoding(errorMsg), null);
            webView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipUtil.copyToClip(v.getContext(), errorMsg);
                    Toast.makeText(v.getContext(), "已拷贝到粘贴板", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    public String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是GB2312
                String s = encode;
                return s;      //是的话，返回“GB2312“，以下代码同理
            }
        } catch (Exception ignored) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是ISO-8859-1
                String s1 = encode;
                return s1;
            }
        } catch (Exception ignored) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {   //判断是不是UTF-8
                String s2 = encode;
                return s2;
            }
        } catch (Exception ignored) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是GBK
                String s3 = encode;
                return s3;
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }

    public void onBack(View v) {
        finish();
    }
}
