package ui;

import security.WenDesUtil;
import log.LogHelper;
import wifiUtil.WifiAdmin;
import GlobalData.Globaldata;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifimaneger.R;

import dialog.SpotsDialog;

public class SendByApActivity extends Activity implements OnClickListener {

	public static final int CONNECT_RESULT = 2;// 连接上wifi热点
	public static final int CREATE_AP_RESULT = 3;// 创建热点结果
	public static final int SHOW_DIALOG = 4;
	public static final int DIS_DIALOG = 5;
	public static final int START_ANIMATION = 6;
	public static final String PACKAGE_NAME = "com.way.wifi";
	public static final String FIRST_OPEN_KEY = "version";
	public static final String WIFI_AP_HEADER = "@";
	public static final String WIFI_AP_PASSWORD = "way12345";

	private TextView textView;
	private WifiAdmin m_wiFiAdmin;
	private CreateAPResult createAPResult;
	private CreateAPProcess createApProcess;
	AlertDialog dialog;
	private ui_effect.WTSearchAnimationFrameLayout sendFrameLayout;
//	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sendbyap_activity_layout);
//		mContext = getApplicationContext();
		initView();
		createApProcess = new CreateAPProcess();
		createAPResult = new CreateAPResult();// 检测AP创建的结果
		m_wiFiAdmin = WifiAdmin.getInstance(getApplicationContext());
		createApProcess.start();
		 AppManager.getAppManager().addActivity(this); 
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		sendFrameLayout.stopAnimation();
		String info = '@'+Globaldata.name+','+Globaldata.number+'=';
		m_wiFiAdmin.closeWiFiAP(m_wiFiAdmin.createWifiInfo(info, WIFI_AP_PASSWORD, 3, "ap"));
		AppManager.getAppManager().finishActivity(this);  
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sendFrameLayout.stopAnimation();
		String info = '@'+Globaldata.name+','+Globaldata.number+'=';
		m_wiFiAdmin.closeWiFiAP(m_wiFiAdmin.createWifiInfo(info, WIFI_AP_PASSWORD, 3, "ap"));
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		sendFrameLayout.stopAnimation();
		String info = '@'+Globaldata.name+','+Globaldata.number+'=';
		m_wiFiAdmin.closeWiFiAP(m_wiFiAdmin.createWifiInfo(info, WIFI_AP_PASSWORD, 3, "ap"));
	}
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){  
            Intent myIntent = new Intent();  
            myIntent = new Intent(SendByApActivity.this, MessageActivity.class);  
            startActivity(myIntent);  
            this.finish();  
        }  
		return super.onKeyDown(keyCode, event);
	}

	private void createAP() {
		// 如果设备不支持创建热点
		if (m_wiFiAdmin.getWifiApState() == 4) {
//			Toast.makeText(this, R.string.not_create_ap, Toast.LENGTH_SHORT)
//					.show();
			return;
		}
		if (dialog == null) {
			Message msg = handler.obtainMessage(SHOW_DIALOG);
			handler.sendMessage(msg);
		}
		// 如果当时wifi已经打开
		if (m_wiFiAdmin.mWifiManager.isWifiEnabled()) {
			m_wiFiAdmin.closeWifi();
		}

		String jiamiNumber = WenDesUtil.getRandomNumber(4);
		textView.setText(textView.getText() +"\n"+"请让接收方输入密码： "+jiamiNumber);
		String info = WenDesUtil.jiami(Globaldata.name+','+Globaldata.number,jiamiNumber);
		createAPResult.start();
		 info = '@'+info+'=';
		m_wiFiAdmin.createWiFiAP(m_wiFiAdmin.createWifiInfo(info, WIFI_AP_PASSWORD, 3, "ap"), true);

	}

	private void initView() {
		findViewById(R.id.backimage3).setOnClickListener(this);
		sendFrameLayout = ((ui_effect.WTSearchAnimationFrameLayout) findViewById(R.id.search_animation_wt_main));// 搜索时的动画
		textView = (TextView)findViewById(R.id.textView1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backimage3:
			Intent intent2 = new Intent();
			intent2.setClass(SendByApActivity.this, MessageActivity.class);
			startActivity(intent2);
			this.finish();
			break;

		default:
			break;
		}
	}

	public  Handler handler = new Handler() {
		AlertDialog dialog;
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case CREATE_AP_RESULT:
				createAPResult.stop();
				// 建立热点成功
				if (((m_wiFiAdmin.getWifiApState() == 3) || (m_wiFiAdmin
						.getWifiApState() == 13))
						&& (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {
					Toast.makeText(getApplicationContext(), "热点创建成功",
							Toast.LENGTH_SHORT).show();
					
				}
				// 建立热点失败
				else {
					Toast.makeText(getApplicationContext(), "热点创建失败",
							Toast.LENGTH_SHORT).show();
				}
				if (dialog != null) {
					dialog.dismiss();
					//sendFrameLayout.startAnimation();
				}
				//sendFrameLayout.startAnimation();
				break;
			case SHOW_DIALOG:
				sendFrameLayout.startAnimation();
				dialog = new SpotsDialog(SendByApActivity.this,R.style.Custom);
				dialog.show();
				break;
			case START_ANIMATION:
				LogHelper.v("cccccccccccccccc", "fsnfkjns");
				//sendFrameLayout.startAnimation();
				break;
			}
		}
	};

	class CreateAPProcess implements Runnable {
		Thread thread;

		public void start() {
			if(thread!=null)
				return;
			try {
				thread = new Thread(this);
				thread.start();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		public void stop() {
			try {
				thread = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			createAP();
		}

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

}
