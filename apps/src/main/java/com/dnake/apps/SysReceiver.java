package com.dnake.apps;

import com.dnake.v700.apps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SysReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent it) {
		String a = it.getAction();
		if (a.equals("android.intent.action.BOOT_COMPLETED")) {
			Intent intent = new Intent(ctx, apps.class);
			ctx.startService(intent);
		} else if (a.equals("com.dnake.broadcast")) {
			String e = it.getStringExtra("event");
			if (e.equals("com.dnake.boot"))
				apps.broadcast();
			else if (e.equals("com.dnake.talk.touch")) {
				WakeTask.refresh();
				AdLabel.bStart = false;
				if (AdLabel.mCtx != null && !AdLabel.mCtx.isFinishing()) {
					AdLabel.mCtx.adStop();
					AdLabel.mCtx.finish();
					AdLabel.mCtx = null;
				}
			}
		}
	}
}
