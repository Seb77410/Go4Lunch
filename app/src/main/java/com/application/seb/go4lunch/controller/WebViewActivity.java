package com.application.seb.go4lunch.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.utils.Constants;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Reference
        WebView webView = findViewById(R.id.webView);
        // New webView client
        webView.setWebViewClient(new WebViewClient());
        // Get url
        Intent intent = getIntent();
        String url =  intent.getStringExtra(Constants.URL);
        // Show url
        webView.loadUrl(url);
    }
}
