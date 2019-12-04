package example.com.erp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import example.com.erp.R;
import example.com.erp.utility.Utils;

public class BillReceiptActivity extends AppCompatActivity {

    WebView webView;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_receipt);
        loadWebView();
    }

    // TODO :- Methods
    void loadWebView() {

        if (!getIntent().getStringExtra("url").equals("") && getIntent().getStringExtra("url") != null) {
            url = getIntent().getStringExtra("url");
        }

        webView = findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        // settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebChromeClient(new WebChromeClient());

        String[] extension = url.split("\\.");
        if (extension[extension.length - 1].equals("pdf")) {
            // webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            finish();
        } else {
            webView.loadUrl(url);
        }

    }

    // TODO :- Button Click
    public void clickToDownload(View view) {
        String[] extension = url.split("\\.");
        Utils.downloadTask(BillReceiptActivity.this, url, extension[extension.length - 1]);
    }

    public void clickToClose(View view) {
        finish();
    }
}
