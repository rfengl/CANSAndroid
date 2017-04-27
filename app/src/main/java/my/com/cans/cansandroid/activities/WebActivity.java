package my.com.cans.cansandroid.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import my.com.cans.cansandroid.R;

/**
 * Created by developer on 27/04/2017.
 */

public class WebActivity extends BaseActivity {

    private String title() {
        return getIntent().getStringExtra("title");
    }

    private String url() {
        return getIntent().getStringExtra("url");
    }

    private WebView webView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //setContentView(R.layout.activity_web);
        super.onCreate(savedInstanceState);

        setTitle(title());
        webView = new WebView(this);
        //webView = (WebView) findViewById(R.id.web_view);

        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

        //setContentView(webView);
        final Activity activity = this;

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl(url());
        setContentView(webView);

        reload();

    }

    public void reload() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                webView.loadUrl(url());
                setContentView(webView);
                Toast.makeText(WebActivity.this, "refresh", Toast.LENGTH_SHORT).show();
                reload();
                // mWebview.loadUrl("http://www.google.com");
            }
        }, 60 * 1000);
    }
}
