//@formatter:off
package com.iot.volunteer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.iot.volunteer.App;
import com.iot.volunteer.Global;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.database.IOTDBHelper;
import com.iot.volunteer.R;
import com.iot.volunteer.fragment.FragmentBankAccountAdd;
import com.iot.volunteer.fragment.FragmentHome;
import com.iot.volunteer.fragment.FragmentMission;
import com.iot.volunteer.fragment.FragmentPointLevel;
import com.iot.volunteer.fragment.FragmentUser;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.model.ItemNews;
import com.iot.volunteer.model.ItemNotification;
import com.iot.volunteer.util.AppConst;
import com.iot.volunteer.util.Util;
import com.iot.volunteer.view.DialogProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityMain extends FragmentActivity {

	public final static int		REQUEST_NOTIFICATION	= 101;

	private PopupWindow popupWindow = null;
	private boolean doubleBackToExitPressedOnce = false;

	// Receiver for JPush
	public class ReceiverJPushMessage extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
//				if (!intent.getAction().equals(AppConst.ACTION_PUSH_RECEIVED) || !IS_FOREGROUND )
//					return;

				if (intent.getAction().equals(AppConst.ACTION_PAY_RECEIVED)) {
					showDialog(intent.getStringExtra("pay_status"));
				} else if (intent.getAction().equals(AppConst.ACTION_PUSH_RECEIVED) && IS_FOREGROUND) {

					ItemNotification itemNotification = (ItemNotification) intent.getSerializableExtra("notification_data");
					if (itemNotification != null) {
						if (itemNotification.taskStatus.equals(ItemNotification.TYPE_ALLOCK_TASK)) {
//							showNotificaionPopup(itemNotification);
							gotoMission(itemNotification);
						} else if (itemNotification.taskStatus.equals(ItemNotification.TYPE_FINISH_TASK) || itemNotification.taskStatus.equals(ItemNotification.TYPE_CANCEL_TASK)) {
							setTaskStatus(TASK_STATUS_READY);
							if (curFragment != null && curFragment instanceof FragmentMission) {
								((FragmentMission) curFragment).setTaskStatus();
							}
							setUserStatusReady();
							Prefs.Instance().setTaskId("0");
							showNotificaionPopup(itemNotification);
						}
					}

					if (curFragment != null && curFragment instanceof FragmentHome) {
						((FragmentHome) curFragment).checkNotiAlarm();
					}
				} else if (intent.getAction().equals(AppConst.ACTION_NOTICE_RECEIVED)) {
					ItemNotification itemNotification = (ItemNotification) intent.getSerializableExtra("notice_status");
					showNotificaionPopup(itemNotification);
					if (curFragment != null && curFragment instanceof FragmentHome) {
						((FragmentHome) curFragment).checkNotiAlarm();
					}
				}
			}
			catch (Exception e) {
				Util.ShowDialogError(
						ActivityMain.this,
//							getResources().getString(R.string.str_api_failed)
						e.toString()
				);
				return;
			}
		}

	}

	// Static
	public static boolean				IS_FOREGROUND = false;
	public static final int				TASK_STATUS_DISABLE = 0;
	public static final int				TASK_STATUS_READY = 1;
	public static final int				TASK_STATUS_WORKING = 2;

	// View
	private FrameLayout		flContainer;
	private LinearLayout	llHome;
	private RelativeLayout	rlUserInfo;
	private ImageView		ivInfoNotification;
	private LinearLayout	llRoot;
	private RelativeLayout	rlMission;
	private ImageView		ivMission;

	private DialogProgress		m_dlgProgress;

	private Fragment		curFragment;
	private String			currentTag;

	// adapter
	public ArrayList<ItemNews> recommendedArray = new ArrayList<>();
	public ArrayList<ItemNews> knowledgeArray = new ArrayList<>();
	public ArrayList<ItemNews> recommendedPreviewArray = new ArrayList<>();
	public ArrayList<ItemNews> knowledgePreviewArray = new ArrayList<>();

//	private GeoCoder								geoCoder;
	private BaiduLocationListener					baiduLocationListener;
	private LocationClient							locationClient;
	private LocationClientOption					locationClientOption;

//	private Timer m_timer;

	private double m_dLat = 0;
	private double m_dLon = 0;
	private String m_province = "";
	private String m_city 	= "";
	private String m_district = "";
	private Long m_up_loc_time = null;

	// For JPush
	private ReceiverJPushMessage m_receiverJPushMsg;

	@SuppressLint("StaticFieldLeak")
	public static Context mainContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainContext = this;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();

		// Init JPush
		m_dlgProgress = new DialogProgress(this);
		m_dlgProgress.setCancelable(false);
		getAllNotifications();
		RegisterReceiverJPushMsg();

		//showInfoNotification();
		ivInfoNotification.setVisibility(View.GONE);

//		m_timer = new Timer();
//		m_timer.scheduleAtFixedRate(new TimerTask() {
//			@Override
//			public void run() {
//				tryUploadLocation();
//			}
//		}, 60000, 60000);
	}

	private void initControls() {
		flContainer = findViewById(R.id.flContainer);

		llHome = findViewById(R.id.llHome);
		rlUserInfo = findViewById(R.id.rlUserInfo);
		ivInfoNotification = findViewById(R.id.ivInfoNotification);
		llRoot = findViewById(R.id.llRoot);
		rlMission = findViewById(R.id.rlMission);
		ivMission = findViewById(R.id.ivMission);

		setTaskStatus(Prefs.Instance().getTaskStatus());

//		gotoHome();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				initLocation();
			}
		}, 8000);

	}

	private void initLocation() {
//		geoCoder = GeoCoder.newInstance();
//		geoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);

		locationClientOption = new LocationClientOption();
		locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		locationClientOption.setScanSpan(5000);
		locationClientOption.setCoorType("bd09ll");		// gcj02
		locationClientOption.setIsNeedAddress(true);
		locationClientOption.setOpenGps(true);
		locationClientOption.setLocationNotify(true);
		locationClientOption.setIgnoreKillProcess(false);
		locationClient = new LocationClient(this);
		locationClient.setLocOption(locationClientOption);
		baiduLocationListener = new BaiduLocationListener();
		locationClient.registerLocationListener(baiduLocationListener);
		locationClient.start();
	}

	public void showInfoNotification() {
		if (Prefs.Instance().getInfoNotification()) {
			ivInfoNotification.setVisibility(View.VISIBLE);
		} else {
			ivInfoNotification.setVisibility(View.GONE);
		}
	}

	private void setEventListener() {
		llHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoHome();
			}
		});
		rlUserInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoUserInfo();
			}
		});
		rlMission.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMission(null);
			}
		});
	}

	private void gotoHome() {
		if (curFragment != null && curFragment instanceof FragmentHome) {
			return;
		}
		llHome.setSelected(true);
		rlUserInfo.setSelected(false);
		FragmentHome fragmentHome = new FragmentHome();
		setChildFragment(fragmentHome, FragmentHome.class.getSimpleName());
		fragmentHome.setActivity(this);
	}

	private void gotoUserInfo() {
		llHome.setSelected(false);
		rlUserInfo.setSelected(true);
		FragmentUser fragmentUser = new FragmentUser();
		setChildFragment(fragmentUser, FragmentUser.class.getSimpleName());
		fragmentUser.setActivity(this);
	}

	public void setTaskStatus(int taskStatus) {
		Prefs.Instance().setTaskStatus(taskStatus);
		Prefs.Instance().commit();

		switch (taskStatus) {
			case TASK_STATUS_DISABLE:
				ivMission.setImageResource(R.drawable.mission_gray);
				Prefs.Instance().setTaskId("0");
				break;
			case TASK_STATUS_READY:
				ivMission.setImageResource(R.drawable.mission_green);
				Prefs.Instance().setTaskId("0");
				break;
			case TASK_STATUS_WORKING:
				ivMission.setImageResource(R.drawable.mission_red);
				break;
			default:
				break;
		}
		Prefs.Instance().setTaskStatus(taskStatus);
		Prefs.Instance().commit();
	}

	public void gotoMission(ItemNotification itemNotification) {
		if (curFragment instanceof FragmentMission) {
			if (itemNotification == null) {
				if (Prefs.Instance().getTaskStatus() == TASK_STATUS_READY) {
					setTaskStatus(TASK_STATUS_DISABLE);
					setUserStatusDisabled();
				} else if (Prefs.Instance().getTaskStatus() == TASK_STATUS_DISABLE) {
					setTaskStatus(TASK_STATUS_READY);
					setUserStatusReady();
				}
			} else {
				if (itemNotification.taskStatus.equals(ItemNotification.TYPE_ALLOCK_TASK)) {
					((FragmentMission) curFragment).setTaskId(itemNotification.taskId);
					((FragmentMission) curFragment).getTaskDetail();
				}
			}
			return;
		}

		if (Prefs.Instance().getTaskStatus() == TASK_STATUS_DISABLE) {
			setTaskStatus(TASK_STATUS_READY);
			setUserStatusReady();
		}

		llHome.setSelected(false);
		rlUserInfo.setSelected(false);

		FragmentMission fragmentMission = new FragmentMission();
		setChildFragment(fragmentMission, FragmentMission.class.getSimpleName());
		fragmentMission.setActivity(this);
		if (itemNotification != null) {
			fragmentMission.setTaskId(itemNotification.taskId);
		}
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			moveTaskToBack(true);
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, R.string.str_again_exit, Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 3000);
		//super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FragmentBankAccountAdd.REQUEST_CODE_IMPORT_PHOTO) {
			FragmentBankAccountAdd.getInstance().onActivityResult(requestCode, resultCode, data);
		} else if (requestCode == REQUEST_NOTIFICATION) {
			if (data != null) {
				ItemNotification taskData = (ItemNotification)data.getSerializableExtra("task_data");
				if (taskData.taskStatus.equals(ItemNotification.TYPE_ALLOCK_TASK)) {
					gotoMission(taskData);
				} else if (taskData.taskStatus.equals(ItemNotification.TYPE_FINISH_TASK) || taskData.taskStatus == ItemNotification.TYPE_CANCEL_TASK) {
					setTaskStatus(TASK_STATUS_READY);
					if (curFragment instanceof FragmentMission) {
						((FragmentMission) curFragment).setTaskStatus();
					}
					setUserStatusReady();
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		IS_FOREGROUND = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		IS_FOREGROUND = true;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
						AppConst.REQUEST_PERMISSION_LOCATION
				);
				return;
			}


			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
						AppConst.REQUEST_PERMISSION_STORAGE
				);
			}

			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.CAMERA },
						AppConst.REQUEST_PERMISSION_CAMERA
				);
				return;
			}

			if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.READ_PHONE_STATE },
						AppConst.REQUEST_PERMISSION_PHONE_STATE
				);
				return;
			}

			if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.CALL_PHONE },
						AppConst.REQUEST_PERMISSION_STORAGE
				);
			}
		}

		ItemNotification itemNotification = (ItemNotification)getIntent().getSerializableExtra("notification_data");
		if (itemNotification != null) {
			gotoMission(itemNotification);
			getIntent().removeExtra("notification_data");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		IS_FOREGROUND = false;

//		if (m_timer != null) {
//			m_timer.cancel();
//			m_timer = null;
//		}
		if (locationClient != null) {
			locationClient.stop();
			locationClient.unRegisterLocationListener(baiduLocationListener);
		}

		if (m_receiverJPushMsg != null) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(m_receiverJPushMsg);
		}
	}

	private void getAllNotifications() {
		m_dlgProgress.show();
		HttpAPI.getAllNotificationList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				m_dlgProgress.dismiss();
				Util.clearNotificationTable();
				try {
					JSONObject jsonObject = new JSONObject(result);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityMain.this, sMsg);
						return;
					}
					IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityMain.this);

					JSONArray jsonArray = jsonObject.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						String dataString = object.getString("dataJson");
						JSONObject dataObject = new JSONObject(dataString);
						if (App.PUSH_INIT_FLAG == false) {
							int noti_id = object.getInt("id");
							if (noti_id > App.PUSH_LAST_ID) {
								App.PUSH_LAST_ID = noti_id;
							}
						}
						ItemNotification itemNotification = new ItemNotification(
								dataObject.optInt("db_id"),
								object.optString("title"),
								object.optString("msg"),
								dataObject.optString("task_id"),
								dataObject.optLong("time"),
								dataObject.optString("type"),
								dataObject.optString("task_status"),
								dataObject.optString("notice_type")
						);

						itemNotification.isRead = object.optBoolean("readStatus") ? 1: 0;
//						Util.addNotification(itemNotification);
						iotdbHelper.addNotificationEntry(itemNotification);
					}
					App.PUSH_INIT_FLAG = true;
					gotoHome();
				}
				catch (Exception ignore) {

				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
			}
		}, ActivityMain.class.getSimpleName());
	}

	private void RegisterReceiverJPushMsg() {
		m_receiverJPushMsg = new ReceiverJPushMessage();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(AppConst.ACTION_PUSH_RECEIVED);
		filter.addAction(AppConst.ACTION_PAY_RECEIVED);
		filter.addAction(AppConst.ACTION_NOTICE_RECEIVED);
		LocalBroadcastManager.getInstance(this).registerReceiver(m_receiverJPushMsg, filter);
	}

	public void showNotificaionPopup(final ItemNotification itemNotification) {
		View popupView = getLayoutInflater().inflate(R.layout.popup_notification, null);
		LinearLayout llContainer = popupView.findViewById(R.id.ID_LL_CONTAINTER);
		TextView tvTitle = popupView.findViewById(R.id.ID_TXTVIEW_TITLE);
		TextView tvStatus = popupView.findViewById(R.id.ID_TXTVIEW_STATS);
		TextView tvTime = popupView.findViewById(R.id.ID_TXTVIEW_TIME);
		TextView tvContent = popupView.findViewById(R.id.ID_TXTVIEW_CONTENT);

		tvTitle.setText(itemNotification.title);
		tvTime.setText(Util.getDateTimeFormatString(new Date(itemNotification.time)));
		tvContent.setText(itemNotification.content);
		tvStatus.setText(R.string.str_unread_status);

		popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setOnDismissListener(() -> popupWindow = null);

		llContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				if (itemNotification.taskStatus.isEmpty()) {
					HttpAPI.readNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
						@Override
						public void onSuccess(String result) {
//							itemNotification.isRead = 1;
//							Util.updateNotificationEntry(itemNotification, itemNotification);
							IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityMain.this);
							iotdbHelper.updateNotificationEntry(itemNotification, itemNotification);
							switch (itemNotification.taskStatus) {
								case ItemNotification.TYPE_ALLOCK_TASK:
									gotoMission(itemNotification);
									break;
							}
						}

						@Override
						public void onError(Object error) {

						}
					}, this.getClass().getSimpleName());
//				} else {
//					itemNotification.isRead = 1;
//					IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityMain.this);
//					iotdbHelper.updateNotificationEntry(itemNotification, itemNotification);
//					switch (itemNotification.taskStatus) {
//						case ItemNotification.TYPE_ALLOCK_TASK:
//							gotoMission(itemNotification);
//							break;
//					}
//				}
				if (popupWindow != null)
					popupWindow.dismiss();
			}
		});

		popupWindow.setAnimationStyle(R.style.popup_window_animation);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();

		// Show popup window offset 1,1 to phoneBtton at the screen center.
		popupWindow.showAtLocation(llRoot, Gravity.TOP, 0, 170);
	}

	public void showWeatherPopup(int codeValue, String weatherType, int temperature, int highTemp, int lowTemp, String district, String updateTime, String quality, int qualityValue) {
		View popupView = getLayoutInflater().inflate(R.layout.popup_weather, null);
		TextView tvTemperature = popupView.findViewById(R.id.ID_TXTVIEW_TEMPERATURE);
		TextView tvAddressAndTime = popupView.findViewById(R.id.ID_TXTVIEW_ADDRESS_TIME);
		ImageView ivWeatherType = popupView.findViewById(R.id.ID_IMGVIEW_WEATHER_TYPE);
		TextView tvTempRange = popupView.findViewById(R.id.ID_TXTVIEW_TEMPERATURE_RANGE);
		TextView tvWeatherQuality = popupView.findViewById(R.id.ID_TXTVIEW_QUALITY);
		TextView tvWeatherQualityValue = popupView.findViewById(R.id.ID_TXTVIEW_QUALITY_VALUE);
		ImageView ivWeatherQuality = popupView.findViewById(R.id.ID_IMGVIEW_QUALITY);
		TextView tvWeatherType = popupView.findViewById(R.id.ID_TXTVIEW_TYPE);

		tvTemperature.setText(temperature + getString(R.string.str_degree));
		tvAddressAndTime.setText(String.format(getString(R.string.str_weather_address_time), district, updateTime));
		tvTempRange.setText(String.format(getString(R.string.str_weather_today_temperature), lowTemp, highTemp));
		tvWeatherQuality.setText(quality);
		tvWeatherQualityValue.setText(String.valueOf(qualityValue));
		//ivWeatherDegree.setImageResource();
		tvWeatherType.setText(weatherType);

		int weatherResId = Util.getResId("weather_" + codeValue, R.drawable.class);
		if (weatherResId != -1) {
			ivWeatherType.setImageResource(weatherResId);
		}

		final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		popupWindow.setAnimationStyle(R.style.popup_window_animation);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();

		// Show popup window offset 1,1 to phoneBtton at the screen center.
		popupWindow.showAtLocation(llRoot, Gravity.TOP, 0, 170);
	}

	public void setChildFragment(Fragment child, String tag) {
		currentTag = tag;
		curFragment = child;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.flContainer, child, tag).commitAllowingStateLoss();
	}

	public void pushChildFragment(Fragment child, String tag) {
		currentTag = tag;
		curFragment = child;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.flContainer, child).addToBackStack(tag).commitAllowingStateLoss();
	}

	public void popChildFragment() {
		getSupportFragmentManager().popBackStack();
		//getSupportFragmentManager().popBackStack(currentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	private void setUserStatusDisabled() {
		m_dlgProgress.show();

		HttpAPI.setUserStatusDisabled(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityMain.this, sMsg);
						return;
					}

					setTaskStatus(ActivityMain.TASK_STATUS_DISABLE);
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							ActivityMain.this,
							getResources().getString(R.string.str_api_failed)
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();

				Util.ShowDialogError(
						ActivityMain.this,
						getResources().getString(R.string.str_api_failed)
				);
			}
		}, ActivityMain.class.getSimpleName());
	}

	private void setUserStatusReady() {
		m_dlgProgress.show();

		HttpAPI.setUserStatusReady(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityMain.this, sMsg);
						return;
					}

					setTaskStatus(ActivityMain.TASK_STATUS_READY);
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							ActivityMain.this,
//							getResources().getString(R.string.str_api_failed)
							"setUserStatusReady+catch"
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();

				Util.ShowDialogError(
						ActivityMain.this,
//						getResources().getString(R.string.str_api_failed)
						"setUserStatusReady+onerror"
				);
			}
		}, ActivityMain.class.getSimpleName());
	}

	public double getLat() {
		return m_dLat;
	}

	public double getLon() {
		return m_dLon;
	}

	class BaiduLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation bdLocation) {
			if (bdLocation == null) {
				return;
			}
			double dLat = bdLocation.getLatitude();
			double dLon = bdLocation.getLongitude();


			if (dLat == Double.MIN_VALUE || dLon == Double.MIN_VALUE) {
//				bdLocation.setLatitude(40.006629);
//				bdLocation.setLongitude(124.360903);
				return;
			}
			if (dLat == 0 || dLon == 0) {
				return;
			}

			m_dLat = dLat;
			m_dLon = dLon;
			Global.gLatValue = dLat;
			Global.gLonValue = dLon;
			m_province = bdLocation.getProvince();
			m_city = bdLocation.getCity();
			m_district = bdLocation.getDistrict();
			tryUploadLocation();

//			lat = String.valueOf(bdLocation.getLatitude());
//			lon = String.valueOf(bdLocation.getLongitude());


			if (curFragment != null) {
				if (curFragment instanceof FragmentHome) {
					((FragmentHome)curFragment).setPointMap(dLat, dLon);
				} else if (curFragment instanceof FragmentMission) {
					((FragmentMission)curFragment).setPointMap(dLat, dLon);
				}
			}

//			LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//			geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
		}
	}

//	private OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {
//		@Override
//		public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//		}
//
//		@Override
//		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//			if (reverseGeoCodeResult == null
//					|| reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
//				return;
//			}
//			ReverseGeoCodeResult.AddressComponent address = reverseGeoCodeResult.getAddressDetail();
//			// Set Address to m_txtAutoAddress
//			m_province = address.province != null ? address.province : "";
//			m_city = address.city != null ? address.city : "";
//			m_district = address.district != null ? address.district : "";
//
//			m_dLat = reverseGeoCodeResult.getLocation().latitude;
//			m_dLon = reverseGeoCodeResult.getLocation().longitude;
//		}
//	};


	private void tryUploadLocation() {
		long cur_time = System.currentTimeMillis();
		if (m_up_loc_time == null || Math.abs(cur_time-m_up_loc_time) > 600000){
			m_up_loc_time = cur_time;
		}else{
			return;
		}

		HttpAPI.updateLocation(
				Prefs.Instance().getUserToken(),
				Prefs.Instance().getUserPhone(),
				String.valueOf(m_dLat),
				String.valueOf(m_dLon),
				m_province,
				m_city,
				m_district,
				new VolleyCallback() {
					@Override
					public void onSuccess(String response) {
//						m_dlgProgress.dismiss();
//
//						try {
//							JSONObject jsonObject = new JSONObject(response);
//							int iRetCode = jsonObject.getInt("retcode");
//							if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
//								String sMsg = jsonObject.getString("msg");
//								Util.ShowDialogError(ActivityMain.this, sMsg);
//								return;
//							}
//						}
//						catch (JSONException e) {
////							Util.ShowDialogError(
////									ActivityMain.this,
////									getResources().getString(R.string.str_api_failed)
////							);
//							return;
//						}
					}

					@Override
					public void onError(Object error) {
//						m_dlgProgress.dismiss();

//						Util.ShowDialogError(
//								ActivityMain.this,
//								getResources().getString(R.string.str_api_failed)
//						);
					}
				},
				"ActivityMain"
		);
	}

	private void showDialog(String pay_status) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View confirmView = layoutInflater.inflate(R.layout.alert_pay_status, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(this).create();
		TextView tvHeader = confirmView.findViewById(R.id.ID_TXT_HEADER);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		if (pay_status.equals("success")) {
			tvHeader.setText(R.string.str_payment_scueess);
		} else {
			tvHeader.setText(R.string.str_payment_fail);
		}

		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getSupportFragmentManager().popBackStack();
				FragmentPointLevel fragmentPointLevel = new FragmentPointLevel();
				ActivityMain.this.pushChildFragment(fragmentPointLevel, FragmentPointLevel.class.getSimpleName());
				fragmentPointLevel.setActivity(ActivityMain.this);
				confirmDlg.dismiss();
			}
		});

		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.show();
	}
}
