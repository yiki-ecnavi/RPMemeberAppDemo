/**
 * Copyright © VOYAGE GROUP, Inc.
 * Author: yiki
 * Last modified: $date$
 */
package jp.researchpanel.rpmemberapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final Pattern sTitlePattern = Pattern.compile("(<title.*?>)(.+?)(</title>)");

//    private static final String HOME_PAGE = "ecnavi.jp";
//    private static final String LOGIN_URL = "https://" + HOME_PAGE + "/login/";

    private static final String HOME_PAGE = "research-panel.jp";
    private static final String LOGIN_URL = "https://" + HOME_PAGE + "/login/";

    private WebView mWebView;
    private ProgressBar mProgressBar;

    // Do not show progress bar seeking when a javascript request(ad) or
    // loading iframe.
    private boolean mIsMainRequestLoadingFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWebView();
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setBackgroundColor(Color.TRANSPARENT);

        mProgressBar = (ProgressBar) findViewById(R.id.webviewProgressbar);

        mProgressBar.setVisibility(View.GONE);

        mWebView.getSettings().setJavaScriptEnabled(true);


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view,
                                                    final String url) {
                boolean result = true;
                if (url.contains(HOME_PAGE)) {
                    result = super.shouldOverrideUrlLoading(view, url);
                } else {
                    // open external browser & load url
                    Log.d(MainActivity.TAG, "Load external URL:" + url);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(browserIntent);
                }

                return result;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "PageStarted: " + url);
                mIsMainRequestLoadingFinished = false;
                super.onPageStarted(view, url, favicon);

                RPMemberAppHttpClient.get(MainActivity.this, url, null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, String response) {
                        super.onSuccess(code, response);
                        Log.d(TAG, response);

                        // Get page title..
                        Matcher matcher = sTitlePattern.matcher(response);
                        if (matcher.find()) {
                            String title = matcher.group(2);
                            Log.i(TAG, "Title: " + title);
                            Toast.makeText(MainActivity.this, "Title: " + title, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "PageFinished: " + url);
                mIsMainRequestLoadingFinished = true;

                // if (url.equals(view.getUrl())) {
                // MainActivity mainActivity = (MainActivity) MainWebView.this
                // .getContext();
                mProgressBar.setProgress(mProgressBar.getMax());
                mProgressBar.setVisibility(View.GONE);
                // }

                super.onPageFinished(view, url);
            }


            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (mIsMainRequestLoadingFinished) {
                    super.onProgressChanged(view, newProgress);
                    return;
                }

                // Log.d(TAG, "ProgressChanged: " + mWebView.getUrl() + " "
                // + newProgress);

                mProgressBar.setProgress(newProgress);
                if (mProgressBar.getVisibility() == View.GONE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                if (mProgressBar.getProgress() >= mProgressBar.getMax()) {
                    mProgressBar.setVisibility(View.GONE);
                }

                //setTitle(mWebView.getTitle());

                super.onProgressChanged(view, newProgress);
            }
        });

        mWebView.loadUrl(LOGIN_URL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, R.menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            // browser back to previous page
            mWebView.goBack();
        } else {
            // application exit
            DialogHelper.showDialog(DialogHelper.DialogId.EXIT_APPLICATION, this,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );
        }
    }

}
