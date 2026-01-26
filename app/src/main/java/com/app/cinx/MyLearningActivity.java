package com.app.cinx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.app.cinx.util.NavHelper;

public class MyLearningActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_learning);

        initViews();
        setupWebView();
        setupNavigation();
        setupBackPressed();
    }

    private void setupBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void initViews() {
        webView = findViewById(R.id.webView);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        // Improve performance
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Hide the HTML's built-in navigation bar since we are using the native one
                // The nav bar in HTML has class 'fixed bottom-6 ...' and is inside a div
                // We can hide the parent div of the glass-nav
                String js = "var nav = document.querySelector('.fixed.bottom-6'); " +
                            "if(nav) { nav.style.display = 'none'; } " +
                            // Also adjust padding of body to avoid excessive empty space if needed, 
                            // but existing pb-32 is fine for our native nav overlay.
                            "document.body.style.paddingBottom = '100px';"; 
                
                webView.evaluateJavascript(js, null);
            }
        });

        webView.loadUrl("file:///android_asset/my_learning.html");
    }

    private void setupNavigation() {
        // R.id.navCourses is the ID for the book icon in layout_floating_nav.xml
        NavHelper.setupNavigation(this, R.id.navCourses);
    }
}
