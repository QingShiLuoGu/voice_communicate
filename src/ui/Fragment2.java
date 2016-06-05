/**
 * 
 */
package ui;

import log.LogHelper;
import wifiUtil.WifiAdmin;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifimaneger.R;

import dialog.SpotsDialog;

public class Fragment2 extends Fragment implements OnClickListener {
	private String TAG = "Fragment2";
	public static final int SERARCH_TIME_OUT = 0;// 搜索超时
	public static final int SCAN_RESULT = 1;// 搜索到wifi返回结果
	public static final int CONNECT_RESULT = 2;// 连接上wifi热点
	public static final int CREATE_AP_RESULT = 3;// 创建热点结果
	public static final int USER_RESULT = 4;// 用户上线人数更新命令(待定)
	public static final int CONNECTED = 5;// 点击连接后断开wifi，3.5秒后刷新adapter
	public static final String PACKAGE_NAME = "com.way.wifi";
	public static final String FIRST_OPEN_KEY = "version";
	public static final String WIFI_AP_HEADER = "way_";
	public static final String WIFI_AP_PASSWORD = "way12345";

	private Button createAPBtn;
	private Button searchWifiBtn;
	private WifiAdmin m_wiFiAdmin;
	private CreateAPResult createAPResult;
	private JudgeSearchWfTimeout judgeWifiSearchTimeoutProcess;
	BroadcastReceiver mItemViewListClickReceiver;
	AlertDialog dialog;

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CREATE_AP_RESULT:
				createAPResult.stop();
				// 建立热点成功
				if (((m_wiFiAdmin.getWifiApState() == 3) || (m_wiFiAdmin
						.getWifiApState() == 13))
						&& (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {
					Toast.makeText(getActivity().getApplicationContext(),
							"热点创建成功", Toast.LENGTH_SHORT).show();
				}
				// 建立热点失败
				else {
					Toast.makeText(getActivity().getApplicationContext(),
							"热点创建失败", Toast.LENGTH_SHORT).show();
				}
				if(dialog!=null)
				{	
					dialog.hide();
					dialog = null;
				}
				break;
			case SERARCH_TIME_OUT:
				judgeWifiSearchTimeoutProcess.stop();
				Toast.makeText(getActivity().getApplicationContext(), "搜索超时！",
						Toast.LENGTH_SHORT).show();
				if(dialog!=null)
				{	
					dialog.hide();
					dialog = null;
				}
				break;
			case SCAN_RESULT:
				LogHelper.d(TAG, "case  scan result");
				if (m_wiFiAdmin.mWifiManager.getScanResults() != null) {
					String str = "";
					int result = m_wiFiAdmin.mWifiManager.getScanResults()
							.size();
					int i = 0;
					for (i = 0; i < result; ++i) {
						if (m_wiFiAdmin.mWifiManager.getScanResults().get(i).SSID
								.startsWith(WIFI_AP_HEADER))
							;
						str += m_wiFiAdmin.mWifiManager.getScanResults().get(i).SSID
								+ "\n";
					}
					TextView textView = (TextView) getView().findViewById(
							R.id.textView1);
					textView.setText(str);
					judgeWifiSearchTimeoutProcess.stop();// 停止该线程
				}
				if(dialog!=null)
				{	
					dialog.hide();
					dialog = null;
				}
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_item2, null);
		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().unregisterReceiver(mItemViewListClickReceiver);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getActivity().registerReceiver(mItemViewListClickReceiver,
				new IntentFilter("android.net.wifi.SCAN_RESULTS"));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		judgeWifiSearchTimeoutProcess = new JudgeSearchWfTimeout();// 新线程用于实现检测是否搜索超时
		createAPResult = new CreateAPResult();// 检测AP创建的结果
		m_wiFiAdmin = WifiAdmin.getInstance(getActivity()
				.getApplicationContext());
		initView();
		initBroadcastReceiver();
	}

	private void initBroadcastReceiver() {
		mItemViewListClickReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context paramContext, Intent paramIntent) {
				if (paramIntent.getAction().equals(
						"android.net.wifi.SCAN_RESULTS")) {
					Log.i("WTScanResults", "android.net.wifi.SCAN_RESULTS");
					Message msg = handler.obtainMessage(SCAN_RESULT);
					handler.sendMessage(msg);
				} else if (paramIntent.getAction().equals(
						"android.net.wifi.WIFI_STATE_CHANGED")) {
					Log.e("WTScanResults",
							"android.net.wifi.WIFI_STATE_CHANGED");

				} else if (paramIntent.getAction().equals(
						"android.net.wifi.STATE_CHANGE")) {
					Log.e("WTScanResults", "android.net.wifi.STATE_CHANGE");
				}
			}
		};
		getActivity().registerReceiver(mItemViewListClickReceiver,
				new IntentFilter("android.net.wifi.SCAN_RESULTS"));
	}

	private void createAPBtn_onClick(View v) {
		// 如果设备不支持创建热点
		if (m_wiFiAdmin.getWifiApState() == 4) {
			Toast.makeText(getActivity().getApplicationContext(),
					R.string.not_create_ap, Toast.LENGTH_SHORT).show();
			return;
		}
		if(dialog==null)
		{	
			dialog = new SpotsDialog(getActivity(), R.style.Custom);
			dialog.show();
		}
		// 如果当时wifi已经打开
		if (m_wiFiAdmin.mWifiManager.isWifiEnabled()) {
			m_wiFiAdmin.closeWifi();
		}

		m_wiFiAdmin.createWiFiAP(m_wiFiAdmin.createWifiInfo(WIFI_AP_HEADER
				+ "sinvoice", WIFI_AP_PASSWORD, 3, "ap"), true);
		createAPResult.start();
	}

	public boolean isWifiConnect() {
		boolean isConnect = true;
		if (!((ConnectivityManager) (getActivity().getApplicationContext())
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected())
			isConnect = false;
		return isConnect;
	}

	public boolean getWifiApState() {
		try {
			WifiManager localWifiManager = (WifiManager) (getActivity()
					.getApplicationContext()).getSystemService("wifi");
			int i = ((Integer) localWifiManager.getClass()
					.getMethod("getWifiApState", new Class[0])
					.invoke(localWifiManager, new Object[0])).intValue();
			return (3 == i) || (13 == i);
		} catch (Exception localException) {
		}
		return false;
	}

	private void searchWifiBtn_OnClick(View v) {

		// LogHelper.d(TAG, "in onclick");
		// // m_wiFiAdmin.startScan();
		if ((judgeWifiSearchTimeoutProcess.running) || (createAPResult.running))
			return;
		// 如果wifi 没有打开
		if (!judgeWifiSearchTimeoutProcess.running) {// 如果搜索线程没有启动
			if (m_wiFiAdmin.getWifiApState() == 13
					|| m_wiFiAdmin.getWifiApState() == 3) {
				return;
			}
			if(dialog==null)
			{	
				dialog = new SpotsDialog(getActivity(), R.style.Custom);
				dialog.show();
			}
			if (!m_wiFiAdmin.mWifiManager.isWifiEnabled()) {// 如果wifi打开着的
				m_wiFiAdmin.OpenWifi();
			}
			m_wiFiAdmin.startScan();
			judgeWifiSearchTimeoutProcess.start();
		} else {
			if(dialog==null)
			{	
				dialog = new SpotsDialog(getActivity(), R.style.Custom);
				dialog.show();
			}
			// 重新启动一下
			judgeWifiSearchTimeoutProcess.stop();
			m_wiFiAdmin.startScan();
			judgeWifiSearchTimeoutProcess.start();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.createAPBtn:
			createAPBtn_onClick(v);
			break;
		case R.id.searchWifi:
			searchWifiBtn_OnClick(v);
			break;
		}
	}

	private void initView() {
		createAPBtn = (Button) getView().findViewById(R.id.createAPBtn);
		createAPBtn.setOnClickListener(this);
		searchWifiBtn = (Button) getView().findViewById(R.id.searchWifi);
		searchWifiBtn.setOnClickListener(this);
	}

	class CreateAPResult implements Runnable {
		public boolean running = false;
		private long startTime = 0L;
		private Thread thread = null;

		CreateAPResult() {
		}

		public void run() {
			while (true) {
				if (!this.running)
					return;
				if ((m_wiFiAdmin.getWifiApState() == 3)
						|| (m_wiFiAdmin.getWifiApState() == 13)
						|| (System.currentTimeMillis() - this.startTime >= 30000L)) {
					Message msg = handler.obtainMessage(CREATE_AP_RESULT);
					handler.sendMessage(msg);
				}
				try {
					Thread.sleep(5L);
				} catch (Exception localException) {
				}
			}
		}

		public void start() {
			try {
				thread = new Thread(this);
				running = true;
				startTime = System.currentTimeMillis();
				thread.start();
			} finally {
			}
		}

		public void stop() {
			try {
				this.running = false;
				this.thread = null;
				this.startTime = 0L;
			} finally {
			}
		}
	}

	class JudgeSearchWfTimeout implements Runnable {
		public boolean running = false;
		private long startTime = 0L;
		private Thread thread = null;

		JudgeSearchWfTimeout() {
		}

		public void run() {
			while (true) {
				if (!this.running)
					return;
				if (System.currentTimeMillis() - this.startTime >= 30000L) {
					Message localMessage = Message.obtain(handler);
					localMessage.what = 0;
					localMessage.sendToTarget();
					Message msg = handler.obtainMessage(SERARCH_TIME_OUT);
					handler.sendMessage(msg);
				}
				try {
					Thread.sleep(10L);
				} catch (Exception localException) {
				}
			}
		}

		public void start() {
			try {
				this.thread = new Thread(this);
				this.running = true;
				this.startTime = System.currentTimeMillis();
				this.thread.start();
			} finally {
			}
		}

		public void stop() {
			try {
				this.running = false;
				this.thread = null;
				this.startTime = 0L;
			} finally {
			}
		}
	}

}
