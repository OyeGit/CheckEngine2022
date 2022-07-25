package com.velexsoft.checkengine;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.velexsoft.checkengine.ConnectionScreen;
import com.velexsoft.checkengine.R;

public class WebViewExGoogle extends Activity {
    private WebView m_webView;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        String pidCode = bundle.getString("PIDCode");

        setContentView(R.layout.activity_web_view_ex);

        m_webView =  (WebView)findViewById(R.id.webviewV);
        WebSettings webSettings = m_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        m_webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                  super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        String uRl = "https://www.google.com/search?embedded=true&q="+pidCode+"&hreflang=en";
        m_webView.loadUrl( uRl );
    }

    @Override
    public void onBackPressed()
    {
       finish();
    }
}
