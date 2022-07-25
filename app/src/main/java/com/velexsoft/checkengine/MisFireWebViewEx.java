package com.velexsoft.checkengine;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.net.URL;

public class MisFireWebViewEx extends Activity {
    private WebView m_webView;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view_ex);

        m_webView =  (WebView)findViewById(R.id.webviewV);
        WebSettings webSettings = m_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);


        m_webView.setWebViewClient(new WebViewClient() {
            ProgressDialog prDialog = new ProgressDialog(context, R.style.MyTheme);

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                prDialog.setCancelable(false);
                prDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                prDialog.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                prDialog.dismiss();
                super.onPageFinished(view, url);
            }
        });
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/checkEngine/"+ "VehMon.pdf");
        System.out.println( "Checking if file exist???....");
        if (!file.exists() || !file.canRead()) {
            System.out.println( "File does not exist???....Proceed");
        } else {
            System.out.println( "File is found!!!!");
            Uri uri = Uri.fromFile(file);
            m_webView.loadUrl(uri.toString());
        }

    }

    @Override
    public void onBackPressed()
    {
        /*
        Intent intent = new Intent(context, ConnectionScreen.class);
        intent.setFlags( intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(intent);
        */
        finish();
    }
}
