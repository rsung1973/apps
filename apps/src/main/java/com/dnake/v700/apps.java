package com.dnake.v700;

import com.dnake.apps.AdLabel;
import com.dnake.apps.LoggerLabel;
import com.dnake.apps.R;
import com.dnake.apps.WakeTask;
import com.dnake.logger.TextLogger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;

@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class apps extends Service {
	public static int version_major = 1; // 主版本
	public static int version_minor = 0; // 次版本
	public static int version_minor2 = 1; // 次版本2

	public static String version_date = "20160330"; // 日期

	public static String version_ex = "(std)"; // 扩展标注

	public static String url = "/dnake/cfg/apps.xml";

	public static PowerManager mPM = null;

	public static final class ad { //在线广告
		public static int enable = 0;
		public static String url = "http://192.168.12.40/ad.html";
		public static int timeout = 5*60;
	}

	public static String browser = "http://192.168.68.100/MessageCenter/InfoCenter/SurroundingIndex";
	public static String mall = "http://www.jd.com";
	public static String stock = "http://www.xm.gov.cn/zwgk/";
	public static String cook = "http://www.xiachufang.com";
	public static String map = "http://ditu.google.cn/maps";

	public class qResult {
		public sip sip = new sip();
		public d600 d600 = new d600();
		public int result = 0;

		public class sip {
			public String url = null;
			public int proxy;
		}

		public class d600 {
			public String ip = null;
			public String host = null;
		}
	}

	public static qResult qResult = null;

	public static void load() {
		dxml p = new dxml();
		if (p.load(url)) {
			ad.enable = p.getInt("/apps/ad/enable", 0);
			ad.url = p.getText("/apps/ad/url", ad.url);
			ad.timeout = p.getInt("/apps/ad/timeout", 5*60);

			browser = p.getText("/apps/brower", browser);
			mall = p.getText("/apps/mall", mall);
			stock = p.getText("/apps/stock", stock);
			cook = p.getText("/apps/cook", cook);
			map = p.getText("/apps/map", map);
		} else
			save();
	}

	public static void save() {
		dxml p = new dxml();

		p.setInt("/apps/ad/enable", ad.enable);
		p.setText("/apps/ad/url", ad.url);
		p.setInt("/apps/ad/timeout", ad.timeout);

		p.setText("/apps/brower", apps.browser);
		p.setText("/apps/mall", apps.mall);
		p.setText("/apps/stock", apps.stock);
		p.setText("/apps/cook", apps.cook);
		p.setText("/apps/map", apps.map);

		p.save(url);
	}

	public static Boolean isScreenOn() {
		if (mPM != null)
			return mPM.isScreenOn();
		return false;
	}

	public static class ProcessThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (ad.enable != 0 && AdLabel.bStart == false) {
					int st = 0;
					if (sys.limit() >= 970 && sys.limit() < 980) {
					} else {
						dmsg req = new dmsg();
						dxml p = new dxml();
						req.to("/security/alarm", null);
						p.parse(req.mBody);
						st = p.getInt("/params/have", 0);
					}
					if (st == 0 && isScreenOn() == false) {
						AdLabel.bStart = true;
						Intent it = new Intent(ctx, AdLabel.class);
						it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						ctx.startActivity(it);
					}
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public static NotificationManager nManager;
	private static Handler e_notify = null;
	public static String notifyText = null;

	public static void notifyText(String text) {
		notifyText = text;
		if (e_notify != null)
			e_notify.sendMessage(e_notify.obtainMessage());
	}

	public static void broadcast() {
		if (apps.ctx != null) {
			Intent it = new Intent("com.dnake.broadcast");
			it.putExtra("event", "com.dnake.apps.sms");
			it.putExtra("nRead", TextLogger.nRead());
			ctx.sendBroadcast(it);
		}
	}

	public static void notifyCancel() {
		nManager.cancelAll();
	}

	public static Context ctx = null;

	@Override
	public void onCreate() {
		super.onCreate();

		apps.ctx = this;
		apps.qResult = new qResult();

		dmsg.start("/apps");
		devent.setup();
		apps.load();
		sys.load();

		TextLogger.load();

		nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		e_notify = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				if (notifyText == null)
					return;

				WakeTask.acquire();

				Intent i = new Intent(ctx, LoggerLabel.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);

				String title = ctx.getString(R.string.logger_notify_title);

				Notification n = new Notification();
				n.icon = android.R.drawable.ic_dialog_email;
				n.tickerText = title;
				n.defaults |= Notification.DEFAULT_SOUND;
				n.when = System.currentTimeMillis();
				n.setLatestEventInfo(ctx, title, notifyText, pi);
				nManager.notify(0, n);

				notifyText = null;
			}
		};

		mPM = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

		ProcessThread pt = new ProcessThread();
		Thread t = new Thread(pt);
		t.start();

		apps.broadcast();

		devent.boot = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		nManager.cancelAll();
	}
}
