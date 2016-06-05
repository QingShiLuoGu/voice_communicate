package ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.TextView;

import com.example.wifimaneger.R;

public class AboutPhone extends Activity {
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_phone_activity);
		textView = (TextView) findViewById(R.id.textView1);
		printPhoneInfo();
		 AppManager.getAppManager().addActivity(this);  
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 AppManager.getAppManager().finishActivity(this);  
	}

	private String getSystemAvaialbeMemorySize() {
		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		// 获得MemoryInfo对象
		MemoryInfo memoryInfo = new MemoryInfo();
		// 获得系统可用内存，保存在MemoryInfo对象上
		mActivityManager.getMemoryInfo(memoryInfo);
		long memSize = memoryInfo.availMem;

		// 字符类型转换
		String availMemStr = formateFileSize(memSize);

		return availMemStr;
	}

	private String formateFileSize(long size) {
		return Formatter.formatFileSize(AboutPhone.this, size);
	}

	private void printPhoneInfo() {
		String phoneName = android.os.Build.MODEL;
		String sysVersion = android.os.Build.VERSION.RELEASE;
		DisplayMetrics dm = new DisplayMetrics();

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		textView.setText("设备名词：" + phoneName + "\n" + "系统版本：" + sysVersion
				+ "\n" + "分辨率：" + width + " x " + height + "\n" + "可用内存："
				+ getSystemAvaialbeMemorySize() + "\n");
	}

}
