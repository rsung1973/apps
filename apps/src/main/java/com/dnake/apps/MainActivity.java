package com.dnake.apps;

//import com.dnake.v700.apps;
//import com.dnake.widget.Button2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dnake.v700.apps;

public class MainActivity extends BaseLabel {

    private WebView web = null;
    private String browserUrl = "http://211.23.68.235:8188/pad/ws_quick_login.aspx?p=eyJpZCI6ImF3dGVrIiwicHN3ZCI6ImF3dGVrIiwiZGF0ZSI6IjIwNDgvMDYvMjIgMDA6MDA6MDAifQ==";    //http://211.23.68.235:8188/pad/index.html";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.brower);

        bFinish = false;

        web = (WebView) findViewById(R.id.browser_webview);
        web.addJavascriptInterface(this, "webTalk");
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
//		web.getSettings().setPluginsEnabled(true);

        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        web.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) { //表示按返回键时的操作
                        web.goBack();
                        return true;
                    }
                }
                return false;
            }
        });

        // 创建WebViewClient对象
        WebViewClient wvc = new WebViewClient() {
            public Boolean sys_ok = true;

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                sys_ok = false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (sys_ok)
                    view.setVisibility(WebView.VISIBLE);
            }
        };

        //设置WebViewClient对象
        web.setWebViewClient(wvc);
        web.setVisibility(WebView.GONE);


//		setContentView(R.layout.main);
//
//		Button2 btn;
//		btn = (Button2) this.findViewById(R.id.main_btn_logger);
//		btn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Intent intent = new Intent(MainActivity.this, LoggerLabel.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//			}
//		});
//
//		btn = (Button2) this.findViewById(R.id.main_btn_apps);
//		btn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				BrowerLabel.brower_url = apps.brower;
//
//				Intent intent = new Intent(MainActivity.this, BrowerLabel.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//			}
//		});
//
//		btn = (Button2) this.findViewById(R.id.main_btn_cook);
//		btn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				BrowerLabel.brower_url = apps.cook;
//
//				Intent intent = new Intent(MainActivity.this, BrowerLabel.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//			}
//		});
//
//		btn = (Button2) this.findViewById(R.id.main_btn_stock);
//		btn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				BrowerLabel.brower_url = apps.stock;
//
//				Intent intent = new Intent(MainActivity.this, BrowerLabel.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//			}
//		});
//
//		btn = (Button2) this.findViewById(R.id.main_btn_map);
//		btn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				BrowerLabel.brower_url = apps.map;
//
//				Intent intent = new Intent(MainActivity.this, BrowerLabel.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//			}
//		});
//
//		btn = (Button2) this.findViewById(R.id.main_btn_mall);
//		btn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				BrowerLabel.brower_url = apps.mall;
//
//				Intent intent = new Intent(MainActivity.this, BrowerLabel.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//			}
//		});

		Intent intent = new Intent(this, apps.class);
		this.startService(intent);
    }

    public void onStart() {
        super.onStart();
        if (apps.browser != null && apps.browser.length() > 0) {
            web.loadUrl(apps.browser);
        }
//		web.loadUrl(browserUrl);
    }

}
