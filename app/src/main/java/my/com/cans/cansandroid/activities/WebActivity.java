package my.com.cans.cansandroid.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;

import my.com.cans.cansandroid.R;

/**
 * Created by developer on 27/04/2017.
 */

public class WebActivity extends BaseActivity {

    protected String title() {
        return getIntent().getStringExtra("title");
    }

    protected String url() {
        return getIntent().getStringExtra("url");
    }

    protected WebView webView;

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
        final BaseActivity activity = this;
        activity.showProgress();
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                activity.hideProgress();
            }
        });

        String url = url();
        if (url.isEmpty() == false)
            webView.loadUrl(url);

        setContentView(webView);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.close, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_close) {
//            this.finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
