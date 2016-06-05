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
		// ���MemoryInfo����
		MemoryInfo memoryInfo = new MemoryInfo();
		// ���ϵͳ�����ڴ棬������MemoryInfo������
		mActivityManager.getMemoryInfo(memoryInfo);
		long memSize = memoryInfo.availMem;

		// �ַ�����ת��
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

		textView.setText("�豸���ʣ�" + phoneName + "\n" + "ϵͳ�汾��" + sysVersion
				+ "\n" + "�ֱ��ʣ�" + width + " x " + height + "\n" + "�����ڴ棺"
				+ getSystemAvaialbeMemorySize() + "\n");
	}

}
