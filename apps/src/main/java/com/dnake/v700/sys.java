package com.dnake.v700;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class sys {
	public static float scaled = 1.0f;
	public static int sLimit = -1;

	public static int limit() {
		if (sLimit != -1)
			return sLimit;

		int limit = 0;
		try {
			FileInputStream in = new FileInputStream("/dnake/bin/limit");
			byte [] data = new byte[256];
			int ret = in.read(data);
			if (ret > 0) {
				String s = new String();
				char [] d = new char[1];
				for(int i=0; i<ret; i++) {
					if (data[i] >= '0' && data[i] <= '9') {
						d[0] = (char) data[i];
						s += new String(d);
					} else
						break;
				}
				limit = Integer.parseInt(s);
			}
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		sLimit = limit;
		return limit;
	}

	public static String url = "/dnake/cfg/sys.xml";
	private static String url_b = "/dnake/data/sys.xml";

	public static final class talk {
		public static int building = 1;
		public static int unit = 1;
		public static int floor = 11;
		public static int family = 11;
	}

	public static final class panel {
		public static int mode = 0; // 0: 单元门口机 1:围墙机 2:小门口机
		public static int index = 1; // 编号
	}

	public static void load() {
		dxml p = new dxml();

		boolean result = p.load(url);
		if (!result)
			result = p.load(url_b);

		if (result) {
			talk.building = p.getInt("/sys/talk/building", 1);
			talk.unit = p.getInt("/sys/talk/unit", 1);
			talk.floor = p.getInt("/sys/talk/floor", 1);
			talk.family = p.getInt("/sys/talk/family", 1);

			panel.mode = p.getInt("/sys/panel/mode", 0);
			panel.index = p.getInt("/sys/panel/index", 1);
		}
		if (panel.mode == 1) {
			talk.building = 0;
			talk.unit = 0;
		}
	}
}
