package ui;

import log.LogHelper;
import security.WenDesUtil;
import sinvoice_lib.Common;
import sinvoice_lib.MyTextUtil;
import sinvoice_lib.SinVoicePlayer;
import sinvoice_lib.SinVoiceRecognition;
import ui.Fragment2.JudgeSearchWfTimeout;
import GlobalData.Globaldata;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifimaneger.R;

public class VoiceSendActivity extends Activity implements
		SinVoiceRecognition.Listener, SinVoicePlayer.Listener,
		android.view.View.OnClickListener {
	public static final int SEND_TIME_OUT = 0;// 搜索超时

	private String sText;
	private String TAG = "VoiceSendActivity";
	// private Handler mHanlder;
	private final static int MSG_SET_RECG_TEXT = 1;
	private final static int MSG_RECG_START = 2;
	private final static int MSG_RECG_END = 3;
	private SinVoicePlayer mSinVoicePlayer;
	private SinVoiceRecognition mRecognition;
	private ui_effect.WTSearchAnimationFrameLayout searchFrameLayout;
	private JudgeTimeout judgeTimeoutProcess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.sendvoice_activity_layout);
		initView();
		searchFrameLayout = ((ui_effect.WTSearchAnimationFrameLayout) findViewById(R.id.search_animation_wt_main));// 搜索时的动画
		searchFrameLayout.startAnimation();
		mSinVoicePlayer = new SinVoicePlayer(Common.CODEBOOK);
		mSinVoicePlayer.setListener(this);
		mRecognition = new SinVoiceRecognition(Common.CODEBOOK);
		mRecognition.setListener(this);
		sText = Globaldata.name + "," + Globaldata.number;	
		LogHelper.v(TAG, "正在发送---" + sText);
		sText = MyTextUtil.encodeText(sText, "@", "#");
		mSinVoicePlayer.play(sText, true, Common.DUR);
		judgeTimeoutProcess = new JudgeTimeout();// 新线程用于实现检测是否搜索超时
		judgeTimeoutProcess.start();
		 AppManager.getAppManager().addActivity(this);  
	}

	private void initView() {
		findViewById(R.id.backimage).setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mSinVoicePlayer.stop();
		mRecognition.stop();
		judgeTimeoutProcess.stop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSinVoicePlayer.stop();
		mRecognition.stop();
		judgeTimeoutProcess.stop();
		AppManager.getAppManager().finishActivity(this);  
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSinVoicePlayer.stop();
		mRecognition.stop();
		judgeTimeoutProcess.stop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent myIntent = new Intent();
//			myIntent = new Intent(VoiceSendActivity.this, MessageActivity.class);
//			startActivity(myIntent);
//			this.finish();
//		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPlayStart() {
		// TODO Auto-generated method stub
		LogHelper.d(TAG, "start play");
	}

	@Override
	public void onPlayEnd() {
		// TODO Auto-generated method stub
		LogHelper.d(TAG, "stop play");
	}

	@Override
	public void onRecognitionStart() {
		// TODO Auto-generated method stub
		// mHanlder.sendEmptyMessage(MSG_RECG_START);
	}

	@Override
	public void onRecognition(char ch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRecognitionEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backimage:
//			Intent myIntent = new Intent();
//			myIntent = new Intent(getApplicationContext(),
//					MessageActivity.class);
//			startActivity(myIntent);
//			this.finish();
			break;

		default:
			break;
		}
	}

	class JudgeTimeout implements Runnable {
		public boolean running = false;
		private long startTime = 0L;
		private Thread thread = null;

		JudgeTimeout() {
		}

		public void run() {
			while (true) {
				if (!this.running)
					return;
				if (System.currentTimeMillis() - this.startTime >= 30000L) {
					Message localMessage = Message.obtain(handler);
					localMessage.what = 0;
					localMessage.sendToTarget();
					Message msg = handler.obtainMessage(SEND_TIME_OUT);
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

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 发送超时了
			case SEND_TIME_OUT:
				judgeTimeoutProcess.stop();
				mSinVoicePlayer.stop();
				mRecognition.stop();
				searchFrameLayout.stopAnimation();
				Toast.makeText(getApplicationContext(), "发送超时！",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
