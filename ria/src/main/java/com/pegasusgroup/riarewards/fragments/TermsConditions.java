package com.pegasusgroup.riarewards.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.pegasusgroup.riarewards.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TermsConditions extends BaseFragment {

    private WebView webView;

    public TermsConditions() {
        // Required empty public constructor
    }


    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_terms_conditions, container, false);
    }

    @Override
    protected void initComponents(View view) {
        webView = findViewById(R.id.webView);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void setListeners() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
                super.onPageFinished(view, url);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://ria.myrewards.com.au/ria/terms_and_conditions.html");
    }
}