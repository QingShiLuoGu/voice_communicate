package ui;

import java.util.ArrayList;
import java.util.List;

import utils.Helper;
import Bean.InfoBean;
import GlobalData.Globaldata;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.wifimaneger.R;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.send).setOnClickListener(this);
		findViewById(R.id.receiv).setOnClickListener(this);
		findViewById(R.id.Imageset).setOnClickListener(this);
		AppManager.getAppManager().addActivity(this);
		getDataFromcache();
	}

	private void getDataFromcache() {
		Helper helper = new Helper(MainActivity.this);
		// helper.writeToCache(list);
		List<InfoBean> mList = helper.getBeansFromCache();
		Globaldata.list = mList;
//		Toast.makeText(MainActivity.this,
//				mList.size()+"", Toast.LENGTH_SHORT).show();
	}

	private void showTips() {

		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提醒")
				.setMessage("是否退出程序")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// ExitApplication.getInstance().exit(MainActivity.this);
						AppManager.getAppManager().AppExit(MainActivity.this);
					}

				}).setNegativeButton("取消",

				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).create(); // 创建对话框
		alertDialog.show(); // 显示对话框
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showTips();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.send:

			Intent intent = new Intent();
			intent.setClass(MainActivity.this, MessageActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.receiv:
			Intent intent2 = new Intent();
			intent2.setClass(MainActivity.this, ReceiveMessageActivity.class);
			startActivity(intent2);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;
		case R.id.Imageset:
//			Intent intent3 = new Intent();
//			intent3.setClass(MainActivity.this, AboutPhone.class);
//			startActivity(intent3);
//			overridePendingTransition(android.R.anim.slide_in_left,
//					android.R.anim.slide_out_right);
			Intent intent4 = new Intent();
			intent4.setClass(MainActivity.this, InfoListActivity.class);
			startActivity(intent4);
			break;
		default:
			break;
		}
	}

}
