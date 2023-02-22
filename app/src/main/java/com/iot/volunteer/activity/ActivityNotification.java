//@formatter:off
package com.iot.volunteer.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.adapter.AdapterNotification;
import com.iot.volunteer.database.IOTDBHelper;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.model.ItemNotification;
import com.iot.volunteer.util.AppConst;
import com.iot.volunteer.util.Util;

import java.util.ArrayList;

public class ActivityNotification extends ActivityBase {
	private final String TAG = "ActivityNotification";

	private ImageView						mBackImg;
	private ImageView						mReadAll;
	private ImageView						mDeleteAll;
	private ListView						mNotiList;
	private AdapterNotification				mNotiAdapter;
	private ArrayList<ItemNotification>		mNotiArray = new ArrayList<>();

	private ReceiverJPushMessage			m_receiverJPushMsg;

	public static boolean IS_FOREGROUND = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();
		loadNotificationList();

		registerReceiverJPushMsg();
	}

	private void loadNotificationList() {
//		IOTDBHelper iotdbHelper = new IOTDBHelper(this);
//		mNotiArray = iotdbHelper.getAllNotificationEntries();
		mNotiArray = Util.getAllNotifications(ActivityNotification.this);

		mNotiAdapter = new AdapterNotification(this, mNotiArray);
		mNotiList.setAdapter(mNotiAdapter);

		mReadAll.setVisibility(View.VISIBLE);
		mDeleteAll.setVisibility(View.VISIBLE);

		for(int i = 0; i < mNotiArray.size(); i++) {
			if (mNotiArray.get(i).type.equals("task") &&
					(mNotiArray.get(i).taskStatus == "create")) {
				mBackImg.setVisibility(View.GONE);
				mReadAll.setVisibility(View.GONE);
				mDeleteAll.setVisibility(View.GONE);
				break;
			}
		}
	}

	@Override
	protected void initControls() {
		super.initControls();

		mBackImg = findViewById(R.id.ID_IMGVIEW_BACK);
		mReadAll = findViewById(R.id.ID_IMGVIEW_READALL);
		mDeleteAll = findViewById(R.id.ID_IMGVIEW_DELETE);
		mNotiList = findViewById(R.id.ID_LSTVIEW_NOTIFICATION);
	}

	@Override
	protected void setEventListener() {
		mBackImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		mReadAll.setOnClickListener(view -> onReadAll());
		mDeleteAll.setOnClickListener(view -> onDeleteAll());

		mNotiList.setOnItemClickListener((adapterView, view, position, id) -> checkIsRead(mNotiArray.get(position)));
//		mNotiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//				Log.d(String.valueOf(mNotiArray), "----------------");
//				if (Prefs.Instance().getTaskId().equals(mNotiArray.get(position).taskId)){
//					showNotification(mNotiArray.get(position));
//				}
//
//			}
//		});
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		IS_FOREGROUND = false;

		if (m_receiverJPushMsg != null) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(m_receiverJPushMsg);
		}
	}

	private void onReadAll() {
		if (this.mNotiArray.size() > 0) {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View confirmView = layoutInflater.inflate(R.layout.alert_logout, null);

			final AlertDialog confirmDlg = new AlertDialog.Builder(this).create();

			TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
			TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
			TextView tvConfirm = confirmView.findViewById(R.id.ID_TEXT_CONFIRM);
			tvConfirm.setText(R.string.str_read_all_confirm);

			btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

			btnConfirm.setOnClickListener(v -> {
				confirmDlg.dismiss();
				readAllNotification();
			});

			confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			confirmDlg.setView(confirmView);
			confirmDlg.show();
		}
	}

	private void readAllNotification() {
		HttpAPI.readAllNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String result) {

			}

			@Override
			public void onError(Object error) {

			}
		}, ActivityNotification.class.getSimpleName());

		for (ItemNotification itemNotification : mNotiArray) {
			if (itemNotification.isRead == 0) {
//				if (itemNotification.type.equals("task")) {
					itemNotification.isRead = 1;
					IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityNotification.this);
					iotdbHelper.updateNotificationEntry(itemNotification, itemNotification);
//				}
//				else {
//					itemNotification.isRead = 1;
//					Util.updateNotificationEntry(itemNotification, itemNotification);
//				}
			}
		}

		loadNotificationList();
	}

	private void onDeleteAll() {
		if (this.mNotiArray.size() > 0) {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View confirmView = layoutInflater.inflate(R.layout.alert_logout, null);

			final AlertDialog confirmDlg = new AlertDialog.Builder(this).create();

			TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
			TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
			TextView tvConfirm = confirmView.findViewById(R.id.ID_TEXT_CONFIRM);
			tvConfirm.setText(R.string.str_delete_all_confirm);

			btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

			btnConfirm.setOnClickListener(v -> {
				confirmDlg.dismiss();
				deleteAllNotification();
			});

			confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			confirmDlg.setView(confirmView);
			confirmDlg.show();
		}
	}

	private void deleteAllNotification() {
		HttpAPI.removeAllNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String result) {

			}

			@Override
			public void onError(Object error) {

			}
		}, ActivityNotification.class.getSimpleName());

		for (ItemNotification itemNotification : mNotiArray) {
//			if (itemNotification.type.equals("task")) {
				IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityNotification.this);
				iotdbHelper.deleteNotificationEntry(itemNotification.id);
//			}
//			else {
//				// Need to implement by new API
//				Util.deleteNotificationEntry(itemNotification.id);
//			}
		}

		loadNotificationList();
	}

	private void registerReceiverJPushMsg() {
		m_receiverJPushMsg = new ReceiverJPushMessage();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(AppConst.ACTION_PUSH_RECEIVED);
		LocalBroadcastManager.getInstance(this).registerReceiver(m_receiverJPushMsg, filter);
	}

	private void checkIsRead(ItemNotification itemNotification) {
		if (itemNotification.isRead > 0) {
			showNotification(itemNotification);
		}
		else {
//			if (itemNotification.type.equals("task")) {
//				itemNotification.isRead = 1;
//				IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityNotification.this);
//				iotdbHelper.updateNotificationEntry(itemNotification, itemNotification);
//			}
//			else {
				HttpAPI.readNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
					@Override
					public void onSuccess(String result) {
//						itemNotification.isRead = 1;
//						Util.updateNotificationEntry(itemNotification, itemNotification);

						itemNotification.isRead = 1;
						showNotification(itemNotification);
					}

					@Override
					public void onError(Object error) {
						Util.ShowDialogError(R.string.str_api_failed);
					}
				}, ActivityNotification.class.getSimpleName());
//			}
		}
	}

	public void showNotification(ItemNotification itemNotification) {
		itemNotification.isRead = 1;

		IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityNotification.this);
		iotdbHelper.updateNotificationEntry(itemNotification, itemNotification);

		Intent intent;
		if (itemNotification.taskId != "" && Prefs.Instance().getTaskId().equals(itemNotification.taskId)) {
			switch (itemNotification.taskStatus) {
				case ItemNotification.TYPE_ALLOCK_TASK:
					intent = new Intent();
					intent.putExtra("task_data", itemNotification);
					setResult(RESULT_OK, intent);
					finish();
					break;
			}
		}

		loadNotificationList();
	}

	public void deleteNotification(ItemNotification itemNotification) {

//		if (itemNotification.type.equals("task")) {
//			m_dlgProgress.dismiss();
//			IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityNotification.this);
//			iotdbHelper.deleteNotificationEntry(itemNotification.id);
//			loadNotificationList();
//		}
//		else {
			HttpAPI.removeNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
				@Override
				public void onSuccess(String result) {
					m_dlgProgress.dismiss();
					IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityNotification.this);
					iotdbHelper.deleteNotificationEntry(itemNotification.id);
//					Util.deleteNotificationEntry(itemNotification.id);
					loadNotificationList();
				}

				@Override
				public void onError(Object error) {
					m_dlgProgress.dismiss();
				}
			}, ActivityNotification.class.getSimpleName());
//		}
	}

	public class ReceiverJPushMessage extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (!intent.getAction().equals(AppConst.ACTION_PUSH_RECEIVED) || !IS_FOREGROUND)
					return;

				ItemNotification itemNotification = (ItemNotification)intent.getSerializableExtra("notification_data");
				if (itemNotification != null) {
					if (itemNotification.taskStatus.equals(ItemNotification.TYPE_ALLOCK_TASK)) {
						loadNotificationList();
					} else if (itemNotification.taskStatus.equals(ItemNotification.TYPE_FINISH_TASK) || itemNotification.taskStatus.equals(ItemNotification.TYPE_CANCEL_TASK)) {
						intent = new Intent();
						intent.putExtra("task_data", itemNotification);
						ActivityNotification.this.setResult(RESULT_OK, intent);
						ActivityNotification.this.finish();
					}
				}
			}
			catch (Exception e) {
			}
		}

	}
}
