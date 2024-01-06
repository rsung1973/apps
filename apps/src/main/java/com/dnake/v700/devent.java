package com.dnake.v700;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.dnake.apps.AdLabel;
import com.dnake.apps.WakeTask;
import com.dnake.logger.TextLogger;

public class devent {
	private static List<devent> elist = null;
	public static Boolean boot = false;

	public String url;

	public devent(String url) {
		this.url = url;
	}

	public void process(String xml) {
	}

	public static void event(String url, String xml) {
		Boolean err = true;
		if (boot && elist != null) {
			Iterator<devent> it = elist.iterator();
			while (it.hasNext()) {
				devent e = it.next();
				if (url.equals(e.url)) {
					e.process(xml);
					err = false;
					break;
				}
			}
		}
		if (err)
			dmsg.ack(480, null);
	}

	public static void setup() {
		elist = new LinkedList<devent>();

		devent de;

		de = new devent("/apps/run") {
			@Override
			public void process(String body) {
				dmsg.ack(200, null);
			}
		};
		elist.add(de);

		de = new devent("/apps/version") {
			@Override
			public void process(String body) {
				dxml p = new dxml();
				String v = String.valueOf(apps.version_major)+"."+apps.version_minor+"."+apps.version_minor2;
				v = v+" "+apps.version_date+" "+apps.version_ex;
				p.setText("/params/version", v);
				dmsg.ack(200, p.toString());
			}
		};
		elist.add(de);

		de = new devent("/apps/device/query") {
			@Override
			public void process(String body) {
				dmsg.ack(200, null);
				if (apps.qResult.d600.host == null) {
					dxml p = new dxml();
					p.parse(body);
					apps.qResult.d600.host = p.getText("/params/name");
					apps.qResult.d600.ip = p.getText("/params/ip");
				}
			}
		};
		elist.add(de);

		de = new devent("/apps/sip/query") {
			@Override
			public void process(String body) {
				dmsg.ack(200, null);
				if (apps.qResult.sip.url == null) {
					dxml p = new dxml();
					p.parse(body);
					apps.qResult.sip.url = new String(p.getText("/params/url"));
				}
			}
		};
		elist.add(de);

		de = new devent("/apps/sip/result") {
			@Override
			public void process(String body) {
				dmsg.ack(200, null);

				dxml p = new dxml();
				p.parse(body);
				apps.qResult.result = p.getInt("/params/result", 0);
			}
		};
		elist.add(de);

		de = new devent("/apps/center/text") {
			@Override
			public void process(String body) {
				dmsg.ack(200, null);

				dxml p = new dxml();
				p.parse(body);

				int seq = p.getInt("/params/seq", 0);
				int type = p.getInt("/params/type", 0);
				String text = p.getText("/params/data");
				if (text != null) {
					TextLogger.insert(seq, type, text);
					apps.notifyText(text);
				}
			}
		};
		elist.add(de);

		de = new devent("/apps/ad/wakeup") {
			@Override
			public void process(String body) {
				dmsg.ack(200, null);

				dxml p = new dxml();
				p.parse(body);
				AdLabel.mWakeupUrl = p.getText("/params/url");
				AdLabel.mWakeupTimeout = p.getInt("/params/timeout", 0);
				AdLabel.bStart = false;
				WakeTask.refresh();
			}
		};
		elist.add(de);

		de = new devent("/apps/web/webkit/read") {
			@Override
			public void process(String body) {
				dxml p = new dxml();
				p.setInt("/params/ad/enable", apps.ad.enable);
				p.setText("/params/ad/url", apps.ad.url);
				p.setInt("/params/ad/timeout", apps.ad.timeout);

				p.setText("/params/app/url", apps.browser);
				p.setText("/params/mall/url", apps.mall);
				p.setText("/params/stock/url", apps.stock);
				p.setText("/params/cook/url", apps.cook);
				p.setText("/params/map/url", apps.map);
				dmsg.ack(200, p.toString());
			}
		};
		elist.add(de);

		de = new devent("/apps/web/webkit/write") {
			@Override
			public void process(String body) {
				dmsg.ack(200, null);

				dxml p = new dxml();
				p.parse(body);

				apps.ad.enable = p.getInt("/params/ad/enable", 0);
				if (p.getText("/params/ad/url") != null)
					apps.ad.url = p.getText("/params/ad/url");
				apps.ad.timeout = p.getInt("/params/ad/timeout", 5*60);

				if (p.getText("/params/app/url") != null)
					apps.browser = p.getText("/params/app/url");
				if (p.getText("/params/mall/url") != null)
					apps.mall = p.getText("/params/mall/url");
				if (p.getText("/params/stock/url") != null)
					apps.stock = p.getText("/params/stock/url");
				if (p.getText("/params/cook/url") != null)
					apps.cook = p.getText("/params/cook/url");
				if (p.getText("/params/map/url") != null)
					apps.map = p.getText("/params/map/url");
				apps.save();
			}
		};
		elist.add(de);
	}
}
