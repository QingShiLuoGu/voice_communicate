package ui;

import java.util.ArrayList;
import java.util.List;

import com.example.wifimaneger.R;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity2 extends FragmentActivity implements
		OnPageChangeListener, OnTabChangeListener {

	private FragmentTabHost mTabHost;
	private LayoutInflater layoutInflater;
	private Class fragmentArray[] = { Fragment1.class, Fragment.class,
			Fragment3.class };
	private int imageArrayNormal[] = { R.drawable.ic_tab_bar_home_normal,
			R.drawable.ic_tab_bar_doctor_normal,
			R.drawable.ic_tab_bar_me_normal };
	private int imageArrayPressed[] = { R.drawable.ic_tab_bar_home_pressed,
			R.drawable.ic_tab_bar_doctor_pressed,
			R.drawable.ic_tab_bar_me_pressed };
	private String textViewArray[] = { "��ҳ", "��ѯ", "��" };
	private List<Fragment> list = new ArrayList<Fragment>();
	private ViewPager viewPager;
	private int indexSelectedPre;// ��¼�ϴ�ѡ���λ��

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_layout);
		initAll();
	}

	/**
	 * ���ø���ʼ���ӷ�����������г�ʼ������
	 */
	private void initAll() {
		initView();
		initPage();
		onPageSelected(1);
	}

	/**
	 * �ؼ���ʼ��
	 */
	private void initView() {
		indexSelectedPre = 0;
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setOnPageChangeListener(this);
		layoutInflater = LayoutInflater.from(this);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);
		mTabHost.setOnTabChangedListener(this);

		int count = textViewArray.length;

		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(textViewArray[i])
					.setIndicator(getTabItemViewNormal(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			mTabHost.setTag(i);
		}
		// ���ν�����ҳʱ���Զ�ѡ���0��tabSpec,����ɫ
		ImageView imageView = (ImageView) mTabHost.getTabWidget().getChildAt(0)
				.findViewById(R.id.tab_imageview);
		imageView.setImageDrawable(getResources().getDrawable(
				imageArrayPressed[0]));

	}

	/**
	 * ��ʼ��Fragment
	 */
	private void initPage() {
		Fragment1 fragment1 = new Fragment1();
		Fragment2 fragment2 = new Fragment2();
		Fragment3 fragment3 = new Fragment3();
		list.add(fragment1);
		list.add(fragment2);
		list.add(fragment3);
		viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(),
				list));
	}

	private View getTabItemViewNormal(int i) {
		View view = layoutInflater.inflate(R.layout.tab_content, null);
		ImageView mImageView = (ImageView) view
				.findViewById(R.id.tab_imageview);
		TextView mTextView = (TextView) view.findViewById(R.id.tab_textview);
		mImageView.setBackgroundResource(imageArrayNormal[i]);
		mTextView.setText(textViewArray[i]);
		return view;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		TabWidget widget = mTabHost.getTabWidget();
		int oldFocusability = widget.getDescendantFocusability();
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);// ������view��ȡ����
		mTabHost.setCurrentTab(arg0);
		widget.setDescendantFocusability(oldFocusability);
		// �����һ��ѡ���tabSpec����ɫ
		ImageView imageView2 = (ImageView) mTabHost.getTabWidget()
				.getChildAt(indexSelectedPre).findViewById(R.id.tab_imageview);
		imageView2.setImageDrawable(getResources().getDrawable(
				imageArrayNormal[indexSelectedPre]));
		indexSelectedPre = arg0;
		// ���ñ���ѡ���tabSpec����ɫ
		ImageView imageView = (ImageView) mTabHost.getTabWidget()
				.getChildAt(arg0).findViewById(R.id.tab_imageview);
		imageView.setImageDrawable(getResources().getDrawable(
				imageArrayPressed[arg0]));

	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		viewPager.setCurrentItem(position);
		//new Thread(networkTask).start();
	}

}
