//@formatter:off
package com.iot.volunteer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.iot.volunteer.activity.ActivityMain;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.jpush.PushMessageReceiver;
import com.iot.volunteer.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class App extends Application {
	public static final String TAG = App.class.getSimpleName();
	private RequestQueue mRequestQueue;

	private static App		m_instance;

	private Prefs		m_prefs;

	private static Context context;

	private static Activity mActivity = null;

	public static boolean PUSH_INIT_FLAG = false;
	public static int 	PUSH_LAST_ID = 0;

	private final Handler notificationHandler = new Handler();

	private final Runnable runnableNotification = new Runnable() {
		@Override
		public void run() {
			try {
				getAllNotifications();
			}catch(Exception ex){

			}
			notificationHandler.postDelayed(runnableNotification, 10000);
		}
	};


	public static App Instance() {
		return m_instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		m_instance = this;
		App.context = getApplicationContext();
		Init();
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				mActivity = activity;
			}

			@Override
			public void onActivityDestroyed(Activity activity) {
//				mActivity = null;
			}

			/** Unused implementation **/
			@Override
			public void onActivityStarted(Activity activity) {}

			@Override
			public void onActivityResumed(Activity activity) {
				mActivity = activity;
			}
			@Override
			public void onActivityPaused(Activity activity) {}

			@Override
			public void onActivityStopped(Activity activity) {}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
		});

		PUSH_INIT_FLAG = false;
		PUSH_LAST_ID = 0;
		if (android.os.Build.DEVICE.contains("HW")) {
			notificationHandler.postDelayed(runnableNotification, 10000);
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		m_instance = null;
	}

	public static Context getAppContext() {
		return App.context;
	}

	public Activity getCurrentActivity() {
		return mActivity;
	}



	private void Init() {
		m_prefs = new Prefs(this);
		// Baidu
		SDKInitializer.initialize(this);
		SDKInitializer.setCoordType(CoordType.BD09LL);
		// JPush
		JPushInterface.setDebugMode(true);
		JPushInterface.init(getBaseContext());

	}

	public Prefs GetPrefs() {
		return m_prefs;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	private void getAllNotifications() {
		if (Prefs.Instance().getUserToken().isEmpty() || (App.PUSH_INIT_FLAG == false)) {
			return;
		}
		HttpAPI.getAllNotificationList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(),  PUSH_LAST_ID, new VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsonObject = new JSONObject(result);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						return;
					}

					JSONArray jsonArray = jsonObject.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						String dataString = object.getString("dataJson");

						if (PUSH_INIT_FLAG) {
							int noti_id = object.getInt("id");
							if (noti_id > PUSH_LAST_ID){
								PUSH_LAST_ID = noti_id;
							}
							PushMessageReceiver.processNotification(getAppContext(), dataString, object.optString("title"),
									object.optString("msg"), false);
						}
					}
				}
				catch (Exception ignore) {
				}
			}

			@Override
			public void onError(Object error) {
			}
		}, ActivityMain.class.getSimpleName());
	}
}
