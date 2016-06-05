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
	private String textViewArray[] = { "首页", "咨询", "我" };
	private List<Fragment> list = new ArrayList<Fragment>();
	private ViewPager viewPager;
	private int indexSelectedPre;// 记录上次选择的位置

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_layout);
		initAll();
	}

	/**
	 * 调用各初始化子方法，完成所有初始化工作
	 */
	private void initAll() {
		initView();
		initPage();
		onPageSelected(1);
	}

	/**
	 * 控件初始化
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
		// 初次进入首页时，自动选择第0项tabSpec,并上色
		ImageView imageView = (ImageView) mTabHost.getTabWidget().getChildAt(0)
				.findViewById(R.id.tab_imageview);
		imageView.setImageDrawable(getResources().getDrawable(
				imageArrayPressed[0]));

	}

	/**
	 * 初始化Fragment
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
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);// 不让子view获取焦点
		mTabHost.setCurrentTab(arg0);
		widget.setDescendantFocusability(oldFocusability);
		// 清除上一次选择的tabSpec的颜色
		ImageView imageView2 = (ImageView) mTabHost.getTabWidget()
				.getChildAt(indexSelectedPre).findViewById(R.id.tab_imageview);
		imageView2.setImageDrawable(getResources().getDrawable(
				imageArrayNormal[indexSelectedPre]));
		indexSelectedPre = arg0;
		// 设置本次选择的tabSpec的颜色
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
