package com.example.kimsoohyeong.week10;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class WebViewActivity extends AppCompatActivity {
    ListView listView;
    WebView webView;
    LinearLayout linear;
    EditText et;
    ArrayAdapter<Data> adapter;
    Animation animTop, animMid;
    ArrayList<Data> data = new ArrayList<>();
    boolean isLinearShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        init();
    }

    private void init() {
        listView = (ListView)findViewById(R.id.listview);
        webView = (WebView)findViewById(R.id.webview);
        linear = (LinearLayout)findViewById(R.id.linear);
        et = (EditText)findViewById(R.id.et);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        webView.addJavascriptInterface(new JavaScriptMethods(),
                "MyApp");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        final ProgressDialog dialog = new ProgressDialog(this);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.setMessage("Loading...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                et.setText(url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) dialog.dismiss();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });

        animTop = AnimationUtils.loadAnimation(this, R.anim.translate_top);
        animTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linear.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animMid = AnimationUtils.loadAnimation(this, R.anim.translate_mid);
        animMid.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                linear.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showWebView();
                Log.d("DEBUG", "애니메이션 실행 콜");
                enableAnim();
                webView.loadUrl(data.get(position).getUrl());
            }
        });

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "즐겨찾기추가");
        menu.add(0, 2, 0, "즐겨찾기목록");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            // 즐겨찾기 추가
            showWebView();
            webView.loadUrl("file:///android_asset/urladd.html");
        } else if (item.getItemId() == 2) {
            // 즐겨찾기 목록
            showListView();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showWebView() {
        Log.d("DEBUG", "애니메이션 종료 콜");
        disableAnim();
        listView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    private void showListView() {
        Log.d("DEBUG", "애니메이션 종료 콜");
        disableAnim();
        webView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    private void disableAnim() {
        if (isLinearShow) {
            isLinearShow = false;
            Log.d("DEBUG", "애니메이션 종료됐음");
            linear.setAnimation(animTop);
            animTop.start();
        }
    }

    private void enableAnim() {
        if (!isLinearShow) {
            isLinearShow = true;
            Log.d("DEBUG", "애니메이션 실행됐음");
            linear.setAnimation(animMid);
            animMid.start();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn1) {
            Log.d("DEBUG", et.getText().toString());
            webView.loadUrl(et.getText().toString());
        }
    }

    Handler myHandler = new Handler();

    class JavaScriptMethods {

        @JavascriptInterface
        public void setData(String siteName, String url) {
            final String strName = siteName;
            final String strUrl = url;
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    boolean flag = true;
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getUrl().equals(strUrl)) {
                            flag = false;
                            break;
                        }
                    }
                    Log.d("ASD", "setData 호출");
                    if (flag) {
                        data.add(new Data(strName, strUrl));
                        adapter.notifyDataSetChanged();
                        Log.d("ASD", "등록 완료");
                    } else {
                        Log.d("ASD", "이미 등록된 URL1");
                        webView.loadUrl("javascript:displayMsg()");
                        Log.d("ASD", "이미 등록된 URL2");
                    }
                }
            });
        }

        @JavascriptInterface
        public void showAnim() {
            Log.d("DEBUG", "애니메이션 실행 핸들러 콜");
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("DEBUG", "애니메이션 실행 콜");
                    enableAnim();
                }
            });
        }
    }
}
