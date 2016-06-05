package ui;

import utils.Helper;
import Bean.InfoBean;
import GlobalData.Globaldata;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wifimaneger.R;

public class ModifyInfoActivity extends Activity {
	private EditText text1;
	private EditText text2;
	private Button button;
	private Button buttonDuan;
	private Button buttonDian;
	int pos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_info_activity_layout);
		text1 = (EditText) findViewById(R.id.edittext1);
		text2 = (EditText) findViewById(R.id.edittext2);
		init();
		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(listener);

		buttonDian = (Button) findViewById(R.id.buttonDian);
		buttonDian.setOnClickListener(listener);

		buttonDuan = (Button) findViewById(R.id.buttonDuan);
		buttonDuan.setOnClickListener(listener);

	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button: {
				if (text1.getText().toString().trim() == ""
						|| text2.getText().toString().trim() == "") {
					Toast.makeText(ModifyInfoActivity.this, "不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				InfoBean bean = Globaldata.list.get(pos);
				bean.setTitle(text1.getText().toString());
				bean.setStr(text2.getText().toString());
				Helper helper = new Helper(ModifyInfoActivity.this);
				helper.clear();
				helper.writeToCache(Globaldata.list);
				Toast.makeText(ModifyInfoActivity.this, "修改成功",
						Toast.LENGTH_SHORT).show();
				finish();
			}

				break;
			case R.id.buttonDian:
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ text2.getText().toString().trim()));
				startActivity(intent);
				break;
			case R.id.buttonDuan:
				Uri smsToUri = Uri.parse("smsto:"
						+ text2.getText().toString().trim());

				Intent intent2 = new Intent(Intent.ACTION_SENDTO, smsToUri);

				intent2.putExtra("sms_body", "");

				startActivity(intent2);
				break;
			default:
				break;
			}

		}
	};

	private void init() {
		Intent intent = getIntent();
		pos = intent.getFlags();
		InfoBean bean = Globaldata.list.get(pos);
		text1.setText(bean.getTitle());
		text2.setText(bean.getStr());
	}
}
