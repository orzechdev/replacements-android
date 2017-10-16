package com.studytor.app.helpers;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by Dawid on 2015-12-31.
 */
public class AppWebViewClients extends WebViewClient {
    private ProgressBar progressBar;

    public AppWebViewClients(ProgressBar progressBar) {
        this.progressBar=progressBar;
        progressBar.setVisibility(View.VISIBLE);
        Log.i("rrrrrrrr", "nnnnnnnnnn");
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        progressBar.setVisibility(View.VISIBLE);
        Log.i("eeeeeee","nnnnnnnnnn");
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.GONE);
        Log.i("fffffff", "nnnnnnnnnn");
    }
}