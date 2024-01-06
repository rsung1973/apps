package com.dnake.apps;

import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;

public class WakeTask {
	public static long ts = 0;
	public static int timeout = 30*1000;

	public static void refresh() {
		ts = System.currentTimeMillis();
	}

	public static Boolean timeout() {
		if (Math.abs(System.currentTimeMillis()-ts) < timeout)
			return false;
		return true;
	}

	public static void acquire() {
		if (Math.abs(System.currentTimeMillis()-ts) >= 1*1000) {
			ts = System.currentTimeMillis();

			dmsg req = new dmsg();
			dxml p = new dxml();
			p.setInt("/params/data", 0);
			p.setInt("/params/apk", 1);
			req.to("/ui/touch/event", p.toString());
		}
	}
}
