package com.dnake.apps;

import com.dnake.v700.apps;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.v700.sys;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class AdLabel extends BaseLabel {
	public static Boolean bStart = true;
	public static AdLabel mCtx = null;
	public static String mWakeupUrl = null;
	public static int mWakeupTimeout = 0;

	private WebView mWeb = null;
	private long mTs;
	private int sMode = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad);
		this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		WakeTask.acquire();
		for(int i=0; i<10; i++) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			if (apps.isScreenOn())
				break;
		}

		sys.load();
		String url = apps.ad.url;
		if (mWakeupUrl != null)
			url = mWakeupUrl;

		if (url.startsWith("rtsp://")) {
			sMode = 1;
			this.playUrl(url);
		} else {
			sMode = 0;
			mWeb = (WebView) findViewById(R.id.ad_webview);
			mWeb.getSettings().setJavaScriptEnabled(true);
			mWeb.getSettings().setSupportZoom(true);
			mWeb.getSettings().setBuiltInZoomControls(true);
			mWeb.setBackgroundColor(Color.BLACK);
			mWeb.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});

			// 创建WebViewClient对象
			WebViewClient wvc = new WebViewClient() {
				public Boolean sys_ok = true;
				public Boolean sys_finish = true;

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

					if (sys_finish) {
						sys_finish = false;
						if (sys_ok)
							view.setVisibility(WebView.VISIBLE);
						else {
							adStop();
							finish();
						}
					}
				}
			};

			// 设置WebViewClient对象
			mWeb.setWebViewClient(wvc);
			mWeb.setVisibility(WebView.GONE);
		}

		bFinish = false;
	}

	private long rTs = 0;

	@Override
	public void onTimer() {
		super.onTimer();

		if (bStart == false) {
			this.adStop();
			if (!this.isFinishing())
				this.finish();
		} else {
			int timeout = apps.ad.timeout;
			if (mWakeupUrl != null)
				timeout = mWakeupTimeout;
			if (Math.abs(System.currentTimeMillis() - mTs) < timeout * 1000) {
				if (sMode == 1 && Math.abs(System.currentTimeMillis()-rTs) > 2000) {
					rTs = System.currentTimeMillis();
					dmsg req = new dmsg();
					if (req.to("/media/rtsp/length", null) != 200) { // 播放异常或结束
						this.playUrl(apps.ad.url);
					}
				}
				WakeTask.acquire();
			} else {
				this.adStop();
				this.finish();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mTs = System.currentTimeMillis();
		if (mWeb != null) {
			String url = apps.ad.url;
			if (mWakeupUrl != null)
				url = mWakeupUrl;
			if (url.contains("?"))
				url = url+"&smode=normal&building="+sys.talk.building+"&unit="+sys.talk.unit+"&index="+sys.panel.index;
			else
				url = url+"?smode=normal&building="+sys.talk.building+"&unit="+sys.talk.unit+"&index="+sys.panel.index;
			mWeb.loadUrl(url);
		}
		mCtx = this;
	}

	@Override
	public void onRestart() {
		super.onRestart();
		mCtx = this;
	}

	@Override
	public void onResume() {
		super.onResume();
		mCtx = this;
	}

	@Override
	public void onStop() {
		super.onStop();
		this.adStop();
		mCtx = null;
		mWakeupUrl = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.adStop();
		mCtx = null;
	}

	public void adStop() {
		this.tStop();
		if (sMode == 1) {
			dmsg req = new dmsg();
			req.to("/media/rtsp/stop", null);
		}
	}

	private void screen() {
		DisplayMetrics dm = new DisplayMetrics(); 
		this.getWindowManager().getDefaultDisplay().getMetrics(dm); 

		int w = dm.widthPixels;
		int h = dm.heightPixels;
		if (w == 800)
			h = 480;
		else if (w == 1024)
			h = 600;
		else if (w == 1280)
			h = 800;
		else if (w <= 480) {
			w = 480;
			h = 272;
		}

		dxml p = new dxml();
		dmsg req = new dmsg();
		p.setInt("/params/x", 0);
		p.setInt("/params/y", 0);
		p.setInt("/params/w", w);
		p.setInt("/params/h", h);
		req.to("/media/rtsp/screen", p.toString());
	}

	private void playUrl(String url) {
		this.screen();

		dmsg req = new dmsg();
		dxml p = new dxml();
		p.setText("/params/url", url);
		if (sys.limit() >= 970 && sys.limit() < 1000) {
			p.setInt("/params/audio", 1);
		}
		req.to("/media/rtsp/play", p.toString());

		p.setInt("/params/val", 8);
		p.setInt("/params/max", 15);
		req.to("/media/rtsp/volume", p.toString());
	}
}
