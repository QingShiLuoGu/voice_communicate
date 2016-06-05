package ui;

import java.util.List;

import com.example.wifimaneger.R;

import Bean.InfoBean;
import GlobalData.Globaldata;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InfoListActivity extends Activity {
	private ListView listView;
	private List<InfoBean> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_list_activity_layout);
		list = Globaldata.list;
		listView = (ListView) findViewById(R.id.listView1);
		 listView.setAdapter(adapter);
		 listView.setOnItemClickListener(itemClickListener);
//		Toast.makeText(InfoListActivity.this, list.size() + "",
//				Toast.LENGTH_SHORT).show();
	}
	
	protected void onResume() {
		super.onResume();
		if(adapter!=null)
		{			
			adapter.notifyDataSetChanged();
		}
	};

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(InfoListActivity.this,ModifyInfoActivity.class);
			intent.addFlags(arg2);
			startActivity(intent);
		}
	};

	BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.list_item,
						null);
				viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.textView1);
				convertView.setTag(viewHolder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.textView.setText(list.get(position).getTitle());
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {

			return list.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
	};

	class ViewHolder {
		TextView textView;
	}

}
