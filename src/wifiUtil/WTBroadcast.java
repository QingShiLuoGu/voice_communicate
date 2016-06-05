package wifiUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.ArrayList;

public class WTBroadcast extends BroadcastReceiver {
	public static ArrayList<EventHandler> ehList = new ArrayList();

	public void onReceive(Context paramContext, Intent paramIntent) {
		if (paramIntent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
			Log.i("WTScanResults", "android.net.wifi.SCAN_RESULTS");
			for (int j = 0; j < ehList.size(); j++)
				((EventHandler) ehList.get(j)).scanResultsAvailable();
		} else if (paramIntent.getAction().equals(
				"android.net.wifi.WIFI_STATE_CHANGED")) {
			Log.e("WTScanResults", "android.net.wifi.WIFI_STATE_CHANGED");
			for (int j = 0; j < ehList.size(); j++)
				((EventHandler) ehList.get(j)).wifiStatusNotification();
		} else if (paramIntent.getAction().equals(
				"android.net.wifi.STATE_CHANGE")) {
			Log.e("WTScanResults", "android.net.wifi.STATE_CHANGE");
			for (int i = 0; i < ehList.size(); i++)
				((EventHandler) ehList.get(i)).handleConnectChange();
		}
	}

	public static abstract interface EventHandler {
		public abstract void handleConnectChange();

		public abstract void scanResultsAvailable();

		public abstract void wifiStatusNotification();
	}
}