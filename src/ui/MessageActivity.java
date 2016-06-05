package ui;

import com.example.wifimaneger.R;

import GlobalData.Globaldata;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends Activity implements
		android.view.View.OnClickListener {
	public final int requestCode = 0;
	public final int RESULT_OK = -1;

	private EditText edittext1;
	private EditText edittext2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_activity_layout);
		initView();
		AppManager.getAppManager().addActivity(this);  
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);  
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){  
            Intent myIntent = new Intent();  
            myIntent = new Intent(MessageActivity.this, MainActivity.class);  
            startActivity(myIntent);  
            this.finish();  
        }  
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//AppManager.getAppManager().addActivity(this); 
		String phonenumber = null;
		String Name = null;
		//AppManager.getAppManager().addActivity(this);  
		if (resultCode == RESULT_OK) {
			finishActivity(resultCode);
			switch (reqCode) {
			case requestCode:
				if (data == null) {
					return;
				}
				Log.d("data数据是：>>", data + "");
				Uri contactData = data.getData();
				if (contactData == null) {
					return;
				}
				@SuppressWarnings("deprecation")
				Cursor cursor = this.managedQuery(contactData, null, null,
						null, null);
				if (cursor.moveToFirst()) {
					// String name = cursor.getString(cursor
					// .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					String hasPhone = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					String id = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
					if (hasPhone.equalsIgnoreCase("1")) {
						hasPhone = "true";
					} else {
						hasPhone = "false";
					}
					if (Boolean.parseBoolean(hasPhone)) {
						Cursor phones = this
								.getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = " + id, null, null);
						while (phones.moveToNext()) {
							Name = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
							phonenumber = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						}
						phones.close();
					}
				}
				edittext1.setText(Name);
				edittext2.setText(phonenumber);
				Globaldata.number = phonenumber;
				Globaldata.name = Name;
				break;

			default:
				break;
			}
		}
	}

	private void initView() {
		findViewById(R.id.select_contact).setOnClickListener(this);
		findViewById(R.id.use_voice).setOnClickListener(this);
		findViewById(R.id.use_wifi).setOnClickListener(this);
		findViewById(R.id.backimage).setOnClickListener(this);
		edittext1 = (EditText) findViewById(R.id.editText1);
		edittext2 = (EditText) findViewById(R.id.editText2);
		edittext1.setText(Globaldata.name);
		edittext2.setText(Globaldata.number);
	}

	//
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.select_contact:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setData(ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, requestCode);
			break;
		case R.id.use_voice:
			if(edittext1.getText().toString().isEmpty()||edittext2.getText().toString().isEmpty())
			{
				Toast.makeText(this, "输入为空！", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent2 = new Intent();
			intent2.setClass(getApplicationContext(), VoiceSendActivity.class);
			startActivity(intent2);
			break;
		case R.id.use_wifi:
			if(edittext1.getText().toString().isEmpty()||edittext2.getText().toString().isEmpty())
			{
				Toast.makeText(this, "输入为空！", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent3 = new Intent();
			intent3.setClass(getApplicationContext(), SendByApActivity.class);
			startActivity(intent3);
			break;
		case R.id.backimage:
			Intent intent4 = new Intent();
			intent4.setClass(getApplicationContext(), MainActivity.class);
			startActivity(intent4);
			this.finish();
			break;
		default:
			break;
		}
	}
}
