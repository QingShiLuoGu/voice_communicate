package ui;

import java.util.ArrayList;
import java.util.List;

import log.LogHelper;
import mydialog.MyDialog;
import security.WenDesUtil;
import sinvoice_lib.Common;
import sinvoice_lib.MyTextUtil;
import sinvoice_lib.SinVoicePlayer;
import sinvoice_lib.SinVoiceRecognition;
import utils.Helper;
import wifiUtil.WifiAdmin;
import Bean.InfoBean;
import GlobalData.Globaldata;
import alter_dialog.SweetAlertDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifimaneger.R;

@SuppressLint("HandlerLeak")
public class ReceiveMessageActivity extends Activity implements
		SinVoiceRecognition.Listener, SinVoicePlayer.Listener, OnClickListener {

	private static String TAG = "ReceiveMessageActivity";
	private final static int MSG_SET_RECG_TEXT = 1;
	private final static int MSG_RECG_START = 2;
	private final static int MSG_RECG_END = 3;

	public static final int SERARCH_TIME_OUT = 0;// 搜索超时
	public static final int SCAN_RESULT = 6;// 搜索到wifi返回结果
	public static final int CONNECT_RESULT = 7;// 连接上wifi热点
	public static final int CREATE_AP_RESULT = 8;// 创建热点结果
	public static final int USER_RESULT = 4;// 用户上线人数更新命令(待定)
	public static final int CONNECTED = 5;// 点击连接后断开wifi，3.5秒后刷新adapter
	private boolean showShow = true;
	List<String> list;
	private static String WIFI_AP_HEADER = "@";
	private static String WIFI_AP_H = "=";

	private static Context context;
	private Handler mHanlder;
	private WifiAdmin m_wiFiAdmin;
	private SinVoicePlayer mSinVoicePlayer;
	private SinVoiceRecognition mRecognition;
	private ui_effect.WTSearchAnimationFrameLayout2 searchFrameLayout;
	private TextView textView;
	BroadcastReceiver mItemViewListClickReceiver;
	AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.receive_activity_layout);
		initView();
		searchFrameLayout = ((ui_effect.WTSearchAnimationFrameLayout2) findViewById(R.id.search_animation_wt_main));// 搜索时的动画

		mSinVoicePlayer = new SinVoicePlayer(Common.CODEBOOK);
		mSinVoicePlayer.setListener(this);
		mRecognition = new SinVoiceRecognition(Common.CODEBOOK);
		mRecognition.setListener(this);
		context = getApplicationContext();
		m_wiFiAdmin = WifiAdmin.getInstance(getApplicationContext());
		openwifi();
		mHanlder = new RegHandler();
		
		initBroadcastReceiver();
		list = new ArrayList<String>();
		// addContact("文小龙,13320941403");
		// receivedMessage("文小龙,13320941403");
		AppManager.getAppManager().addActivity(this);
	}

	private void openwifi() {
		if (!m_wiFiAdmin.mWifiManager.isWifiEnabled()) {// 如果wifi打开着的
			m_wiFiAdmin.OpenWifi();
		}
		m_wiFiAdmin.startScan();
	}

	private void initView() {
		findViewById(R.id.backimage2).setOnClickListener(this);

		textView = (TextView) findViewById(R.id.textView1);
	}

	private void initBroadcastReceiver() {
		mItemViewListClickReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context paramContext, Intent paramIntent) {
				if (paramIntent.getAction().equals(
						"android.net.wifi.SCAN_RESULTS")) {
					Log.i("WTScanResults", "android.net.wifi.SCAN_RESULTS");
					mHanlder.sendEmptyMessage(SCAN_RESULT);
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
		getApplication().registerReceiver(mItemViewListClickReceiver,
				new IntentFilter("android.net.wifi.SCAN_RESULTS"));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSinVoicePlayer.stop();
		mRecognition.stop();
		searchFrameLayout.stopAnimation();
		m_wiFiAdmin.closeWifi();
		AppManager.getAppManager().finishActivity(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSinVoicePlayer.stop();
		mRecognition.stop();
		searchFrameLayout.stopAnimation();
		m_wiFiAdmin.closeWifi();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(searchFrameLayout!=null)
			searchFrameLayout.startAnimation();
		if(mRecognition!=null)
			mRecognition.start();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mSinVoicePlayer.stop();
		mRecognition.stop();
		searchFrameLayout.stopAnimation();
		m_wiFiAdmin.closeWifi();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		list.clear();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent myIntent = new Intent();
//			myIntent = new Intent(ReceiveMessageActivity.this,
//					MainActivity.class);
//			startActivity(myIntent);
//			this.finish();
//		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean addContact(String name, String number) {

		LogHelper.v("sss", name + " " + number);
		if (number.isEmpty() || name.isEmpty() || !number.matches("[0-9]+")) {
			Toast.makeText(getApplicationContext(), "添加联系人出现错误，请检查输入是否有错",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = this.getContentResolver();
		ContentValues values = new ContentValues();
		long contactId = ContentUris.parseId(resolver.insert(uri, values));

		/* 往 data 中添加数据（要根据前面获取的id号） */
		// 添加姓名
		uri = Uri.parse("content://com.android.contacts/data");
		values.put("raw_contact_id", contactId);
		values.put("mimetype", "vnd.android.cursor.item/name");
		values.put("data2", name);
		resolver.insert(uri, values);

		// 添加电话
		values.clear();
		values.put("raw_contact_id", contactId);
		values.put("mimetype", "vnd.android.cursor.item/phone_v2");
		values.put("data2", "2");
		values.put("data1", number);
		resolver.insert(uri, values);

		writeToCache(name,number);
		return true;
	}

	private void writeToCache(String name, String number) {
		Helper helper = new Helper(ReceiveMessageActivity.this);
		InfoBean b = new InfoBean(0, name, number);
		helper.writeToCache(b);
	}

	private class RegHandler extends Handler {
		private StringBuilder mTextBuilder = new StringBuilder();

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SET_RECG_TEXT:
				char ch = (char) msg.arg1;
				mTextBuilder.append(ch);
				//LogHelper.d("recognition", "come in" + " " + ch);
				break;

			case MSG_RECG_START:
				LogHelper.d(TAG, "recognition start");
				mTextBuilder.delete(0, mTextBuilder.length());
				break;

			case MSG_RECG_END:
				String str = MyTextUtil.decodeText(mTextBuilder.toString(),
						"@", "#");
				if (str != null) {
					// receivedMessage(str);
					// textView.setText(str);
					receivedMessage(str);
					// searchFrameLayout.startAnimation();
				}
				LogHelper.d(TAG, "recognition end");
				break;
			case SCAN_RESULT:
				onScanResult();
				// searchFrameLayout.startAnimation();
				break;
			}
			super.handleMessage(msg);
		}
	}

	private void onScanResult() {
		int size = -1;
		try
		{
			size = m_wiFiAdmin.mWifiManager.getScanResults().size();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if (size > 0) {
			for (int i = 0; i < size; ++i) {
				ScanResult scanResult = m_wiFiAdmin.mWifiManager
						.getScanResults().get(i);
				if (scanResult.SSID.startsWith(WIFI_AP_HEADER)
						&& scanResult.SSID.endsWith(WIFI_AP_H)) {
					Globaldata.str = scanResult.SSID.substring(1,
							scanResult.SSID.length() - 1);

					MyDialog dialog = new MyDialog(this, "请输入密码",
							new MyDialog.OnCustomDialogListener() {

								@Override
								public void back(String num) {
									if (num == null || num.length() != 4)
										return;
									final String number;
									final String name;
									String str = Globaldata.str;
									str = WenDesUtil.jiemi(str, num);
									try {

										number = str.substring(str.indexOf(',') + 1);
										name = str.substring(0,
												str.indexOf(','));
									} catch (Exception e) {
										// TODO: handle exception
										return;
									}
									if (list.contains(name))
										return;
//									if(showShow=false)
//										return;
									list.add(name);
									// searchFrameLayout.stopAnimation();
									new SweetAlertDialog(
											ReceiveMessageActivity.this,
											SweetAlertDialog.WARNING_TYPE)
											.setTitleText("确定添加到通讯录?")
											.setContentText(name + " " + number)
											.setCancelText("取消")
											.setConfirmText("确定")
											.showCancelButton(true)
											.setCancelClickListener(
													new SweetAlertDialog.OnSweetClickListener() {
														@Override
														public void onClick(
																SweetAlertDialog sDialog) {
															// reuse previous
															// dialog instance,
															// keep widget
															// user state, reset
															// them if you
															// need
															sDialog.setTitleText(
																	"已取消")
																	.setContentText(
																			"未添加该联系人")
																	.setConfirmText(
																			"OK")
																	.showCancelButton(
																			false)
																	.setCancelClickListener(
																			null)
																	.setConfirmClickListener(
																			null)
																	.changeAlertType(
																			SweetAlertDialog.ERROR_TYPE);
														}
													})
											.setConfirmClickListener(
													new SweetAlertDialog.OnSweetClickListener() {
														@Override
														public void onClick(
																SweetAlertDialog sDialog) {
															if (!addContact(
																	name,
																	number)) {
																sDialog.setTitleText(
																		"出错")
																		.setContentText(
																				"未添加该联系人")
																		.setConfirmText(
																				"OK")
																		.showCancelButton(
																				false)
																		.setCancelClickListener(
																				null)
																		.setConfirmClickListener(
																				null)
																		.changeAlertType(
																				SweetAlertDialog.ERROR_TYPE);
																return;
															}

															sDialog.setTitleText(
																	"成功")
																	.setContentText(
																			"已添加 "
																					+ name
																					+ " 到通讯录")
																	.setConfirmText(
																			"OK")
																	.showCancelButton(
																			false)
																	.setCancelClickListener(
																			null)
																	.setConfirmClickListener(
																			null)
																	.changeAlertType(
																			SweetAlertDialog.SUCCESS_TYPE);
														}
													}).show();

								}
							});
					try
					{
						dialog.show();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
				}
			}
		}
	}

	private void receivedMessage(String str) {
		LogHelper.v("receive", "in!!!!!!!!!!!!!");
		// searchFrameLayout.stopAnimation();
		final String number;
		final String name;
		try {

			number = str.substring(str.indexOf(',') + 1);
			name = str.substring(0, str.indexOf(','));
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}
		if (list.contains(name))
			return;
		list.add(name);
		// searchFrameLayout.stopAnimation();
		new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("确定添加到通讯录?")
				.setContentText(name + " " + number)
				.setCancelText("取消")
				.setConfirmText("确定")
				.showCancelButton(true)
				.setCancelClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								// reuse previous dialog instance, keep widget
								// user state, reset them if you need
								sDialog.setTitleText("已取消")
										.setContentText("未添加该联系人")
										.setConfirmText("OK")
										.showCancelButton(false)
										.setCancelClickListener(null)
										.setConfirmClickListener(null)
										.changeAlertType(
												SweetAlertDialog.ERROR_TYPE);
							}
						})
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								if (!addContact(name, number)) {
									sDialog.setTitleText("出错")
											.setContentText("未添加该联系人")
											.setConfirmText("OK")
											.showCancelButton(false)
											.setCancelClickListener(null)
											.setConfirmClickListener(null)
											.changeAlertType(
													SweetAlertDialog.ERROR_TYPE);
									return;
								}

								sDialog.setTitleText("成功")
										.setContentText("已添加 " + name + " 到通讯录")
										.setConfirmText("OK")
										.showCancelButton(false)
										.setCancelClickListener(null)
										.setConfirmClickListener(null)
										.changeAlertType(
												SweetAlertDialog.SUCCESS_TYPE);
							}
						}).show();

	}

	@Override
	public void onPlayStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRecognitionStart() {
		// TODO Auto-generated method stub
		mHanlder.sendEmptyMessage(MSG_RECG_START);
	}

	@Override
	public void onRecognition(char ch) {
		// TODO Auto-generated method stub
		mHanlder.sendMessage(mHanlder.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
	}

	@Override
	public void onRecognitionEnd() {
		// TODO Auto-generated method stub
		mHanlder.sendEmptyMessage(MSG_RECG_END);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backimage2:
//			Intent intent2 = new Intent();
//			intent2.setClass(ReceiveMessageActivity.this, MainActivity.class);
//			startActivity(intent2);
			break;

		default:
			break;
		}
	}
}
