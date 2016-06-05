package utils;

import java.util.ArrayList;
import java.util.List;

import Bean.InfoBean;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;

public class Helper {

	private static String ARRAY_NAME = "VoiceAndWifi";
	private ACache mCache;

	public Helper(Context context) {
		mCache = ACache.get(context);
	}

	public void clear() {
		mCache.remove(ARRAY_NAME);
	}

	public List<InfoBean> getBeansFromCache() {
		JSONArray testJsonArray = mCache.getAsJSONArray(ARRAY_NAME);
		List<InfoBean> list = new ArrayList<InfoBean>();
		if (testJsonArray != null) {
			for (int i = 0; i < testJsonArray.length(); i++) {
				try {
					JSONObject jsonObj = testJsonArray.getJSONObject(i);
					InfoBean bean = jsonObi2Bean(jsonObj);
					list.add(bean);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return list;
	}

	public void writeToCache(InfoBean bean) {
		JSONArray testJsonArray = mCache.getAsJSONArray(ARRAY_NAME);
		if (testJsonArray == null) {
			testJsonArray = new JSONArray();
		}

		JSONObject jsonObj = bean2JsonObj(bean);
		testJsonArray.put(jsonObj);

		mCache.put(ARRAY_NAME, testJsonArray);
	}

	public void writeToCache(List<InfoBean> list) {
		JSONArray testJsonArray = mCache.getAsJSONArray(ARRAY_NAME);
		if (testJsonArray == null) {
			testJsonArray = new JSONArray();
		}
		for (InfoBean bean : list) {
			JSONObject jsonObj = bean2JsonObj(bean);
			testJsonArray.put(jsonObj);
		}
		mCache.put(ARRAY_NAME, testJsonArray);
	}

	public void writeToCache(InfoBean[] beans) {
		JSONArray testJsonArray = mCache.getAsJSONArray(ARRAY_NAME);
		if (testJsonArray == null) {
			testJsonArray = new JSONArray();
		}
		for (InfoBean bean : beans) {
			JSONObject jsonObj = bean2JsonObj(bean);
			testJsonArray.put(jsonObj);
		}
		mCache.put(ARRAY_NAME, testJsonArray);
	}

	public static JSONObject bean2JsonObj(InfoBean bean) {
		JSONObject obj = new JSONObject();
		int type = bean.getType();
		String title = bean.getTitle();
		String str = bean.getStr();
		try {
			obj.put("type", type);
			obj.put("title", title);
			obj.put("str", str);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return obj;
	}

	public static InfoBean jsonObi2Bean(JSONObject obj) {
		InfoBean bean = new InfoBean();
		try {
			int type = obj.getInt("type");
			String title = obj.getString("title");
			String str = obj.getString("str");
			bean.setType(type);
			bean.setTitle(title);
			bean.setStr(str);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bean;
	}
}
