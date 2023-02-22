//@formatter:off
package com.iot.volunteer.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.annotation.RequiresApi;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.iot.volunteer.Global;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.activity.ActivityMain;
import com.iot.volunteer.helper.RoomActivity;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.model.ItemNotification;
import com.iot.volunteer.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentMission extends FragmentBase implements View.OnClickListener {
	static FragmentMission fragment = null;

	public final static int		REQUEST_NOTIFICATION	= 101;

	public static final int ALARM_STATUS_PENDING = 0; // 处理中
	public static final int ALARM_STATUS_PROCESSED = 1; // 已处理
	public static final int ALARM_STATUS_WAIT_ALLOC = 2; // 綁定中
	public static final int ALARM_STATUS_CANCEL = 3; // 取消

	private TextView								tvTitle;
	private MapView									mvMap;
	private LinearLayout							llMissionConfirm;
	private TextView								tvAidKind;
	private TextView								tvAidStatus;
	private TextView								tvAidPos;
	private TextView								tvAidCreateTime;
	private TextView								tvAidContent;
	private TextView								tvStartMission;
	private TextView								tvCancelMission;
	private TextView								tvMissionConfirmTime;
	private TextView								tvMissionContent;
	private TextView								tvTaskComplete;
	private TextView								tvAid1Distance;
	private TextView								tvAid2Distance;
	private TextView								tvAid3Distance;
	private TextView								tvAidYouDistance;
	private LinearLayout							llChatting;
	private LinearLayout							llGiveupMission;
	private LinearLayout							llTaskTop;
	private LinearLayout							llTaskBottom;
	private TextView								tvBottomAidKind;
	private TextView								tvBottomAidStatus;
	private TextView								tvBottomAidPos;
	private TextView								tvBottomCurrentDisease;
	private TextView								tvBottomAidCreateTime;
	private TextView								tvSOSContact1;
	private TextView								tvSOSContact2;
	private TextView								tvSOSContact3;
	private TextView								tvSOSContact1Phone;
	private TextView								tvSOSContact2Phone;
	private TextView								tvSOSContact3Phone;

	private BaiduMap								baiduMap;
	private MapStatus.Builder						mapStatusBuilder;
	private OverlayOptions							customerOverlayOptions;
	private OverlayOptions							youOverlayOptions;

	private String									customerLat = "";
	private String									customerLon = "";

	private boolean									isFirstLocation = true;

	private Handler									handler = new Handler();
	private CountDownTimer							timer;
	private int										iCount;
	private String									taskId;
	private RoutePlanSearch 						mSearch;
	private LatLng 									customerLatLng;
	private LatLng 									youLatLng;
	private Timer 									m_timer;

	public static FragmentMission getInstance() {
		if (fragment == null) {
			fragment = new FragmentMission();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_mission, container, false);

		isFirstLocation = true;


		initControls(rootView);
		setEventListener();

		if (Prefs.Instance().getTaskStatus() == ActivityMain.TASK_STATUS_WORKING) {
			if (taskId == null) {
				taskId = Prefs.Instance().getTaskId();
			}
		}else{
			setTaskStatus();
		}

		if (taskId != null && taskId != "0") {
			m_timer = new Timer();
			m_timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							getTaskDetail();
						}
					});
				}
			}, 0, 60000);
		}

		setPointMap(Global.gLatValue, Global.gLonValue);

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		llMissionConfirm = layout.findViewById(R.id.llMissionConfirm);
		tvTitle = layout.findViewById(R.id.tvTitle);
		mvMap = layout.findViewById(R.id.mvMap);
		tvAidKind = layout.findViewById(R.id.tvAidKind);
		tvAidStatus = layout.findViewById(R.id.tvAidStatus);
		tvAidPos = layout.findViewById(R.id.tvAidPos);
		tvAidCreateTime = layout.findViewById(R.id.tvAidCreateTime);
		tvAidContent = layout.findViewById(R.id.tvAidContent);
		tvStartMission = layout.findViewById(R.id.tvStartMission);
		tvCancelMission = layout.findViewById(R.id.tvCancelMission);
		tvMissionConfirmTime = layout.findViewById(R.id.tvMissionConfirmTime);
		tvMissionContent = layout.findViewById(R.id.tvMissionContent);
		tvTaskComplete = layout.findViewById(R.id.tvTaskComplete);
		tvAid1Distance = layout.findViewById(R.id.tvAid1Distance);
		tvAid2Distance = layout.findViewById(R.id.tvAid2Distance);
		tvAid3Distance = layout.findViewById(R.id.tvAid3Distance);
		tvAidYouDistance = layout.findViewById(R.id.tvAidYouDistance);
		llChatting = layout.findViewById(R.id.llChatting);
		llGiveupMission = layout.findViewById(R.id.llGiveupMission);
		llTaskTop = layout.findViewById(R.id.llTaskTop);
		llTaskBottom = layout.findViewById(R.id.llTaskBottom);
		tvBottomAidKind = layout.findViewById(R.id.tvBottomAidKind);
		tvBottomAidStatus = layout.findViewById(R.id.tvBottomAidStatus);
		tvBottomAidPos = layout.findViewById(R.id.tvBottomAidPos);
		tvBottomCurrentDisease = layout.findViewById(R.id.tvBottomCurrentDisease);
		tvBottomAidCreateTime = layout.findViewById(R.id.tvBottomAidCreateTime);
		tvSOSContact1 = layout.findViewById(R.id.tvSOSContact1);
		tvSOSContact2 = layout.findViewById(R.id.tvSOSContact2);
		tvSOSContact3 = layout.findViewById(R.id.tvSOSContact3);
		tvSOSContact1Phone = layout.findViewById(R.id.tvSOSContact1Phone);
		tvSOSContact2Phone = layout.findViewById(R.id.tvSOSContact2Phone);
		tvSOSContact3Phone = layout.findViewById(R.id.tvSOSContact3Phone);

		showStatusAlert();

		mSearch = RoutePlanSearch.newInstance();

		mvMap.showZoomControls(false);
		mvMap.showScaleControl(false);
		View child = mvMap.getChildAt(1);
		if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}

		baiduMap = mvMap.getMap();
		baiduMap.setMyLocationEnabled(true);
	}

	private void showStatusAlert() {
		if (!Prefs.Instance().getShowAgainManual()) {
			return;
		}

		final AlertDialog alertDlg = new AlertDialog.Builder(getContext()).create();
		alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View alertView = layoutInflater.inflate(R.layout.dialog_status_manual, null);

		final LinearLayout llNotShowAgain = alertView.findViewById(R.id.llNotShowAgain);
		TextView tvConfirm = alertView.findViewById(R.id.tvConfirm);

		alertDlg.setView(alertView);

		llNotShowAgain.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				boolean isNotShowAgain = llNotShowAgain.isSelected();
				llNotShowAgain.setSelected(!isNotShowAgain);
			}
		});

		tvConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Prefs.Instance().setShowAgainManual(!llNotShowAgain.isSelected());
				Prefs.Instance().commit();
				alertDlg.dismiss();
			}
		});

		alertDlg.show();
	}

	private void showCompleteTaskAlert() {
		final AlertDialog alertDlg = new AlertDialog.Builder(getContext()).create();
		alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View alertView = layoutInflater.inflate(R.layout.dialog_mission_complete, null);

		final TextView tvConfirm = alertView.findViewById(R.id.tvConfirm);
		TextView tvConversation = alertView.findViewById(R.id.tvConversation);

		alertDlg.setView(alertView);

		tvConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				alertDlg.dismiss();
				onCompleteMission();

			}
		});

		tvConversation.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				alertDlg.dismiss();
//				activityMain.setTaskStatus(ActivityMain.TASK_STATUS_READY);
//				setTaskStatus();
//				onChatting();
			}
		});

		alertDlg.show();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		iCount = 21;

		timer = new CountDownTimer(21000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				iCount--;
				tvConfirm.setText(getString(R.string.str_confirm) + "(" + iCount + "S)");
			}
			@Override
			public void onFinish() {
				alertDlg.dismiss();
			}
		};
		timer.start();
	}

	private void showGiveupTaskAlert() {
		final AlertDialog alertDlg = new AlertDialog.Builder(getContext()).create();
		alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View alertView = layoutInflater.inflate(R.layout.dialog_mission_cancel, null);

		final TextView tvConfirm = alertView.findViewById(R.id.tvConfirm);
		TextView tvCancel = alertView.findViewById(R.id.tvCancel);

		alertDlg.setView(alertView);

		tvConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancelTask();
				alertDlg.dismiss();
			}
		});

		tvCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				alertDlg.dismiss();
			}
		});

		alertDlg.show();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		iCount = 21;

		timer = new CountDownTimer(21000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				iCount--;
				tvConfirm.setText(getString(R.string.str_confirm) + "(" + iCount + "S)");
			}
			@Override
			public void onFinish() {
				alertDlg.dismiss();
			}
		};
		timer.start();
	}

	@Override
	protected void setEventListener() {
		mapStatusBuilder = new MapStatus.Builder();

		tvStartMission.setOnClickListener(this);
		tvCancelMission.setOnClickListener(this);
		tvTaskComplete.setOnClickListener(this);
		llGiveupMission.setOnClickListener(this);
		llChatting.setOnClickListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		baiduMap.setMyLocationEnabled(false);

		mvMap.onDestroy();
		mvMap = null;

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (m_timer != null) {
			m_timer.cancel();
			m_timer = null;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tvStartMission:
				onConfirmMission();
				break;
			case R.id.tvCancelMission:
				onCancelMission();
				break;
			case R.id.llGiveupMission:
				onGiveupMission();
				break;
			case R.id.tvTaskComplete:

				showCompleteTaskAlert();
				break;
			case R.id.llChatting:
				onChatting();
				break;
		}
	}

	private void onConfirmMission() {
		llMissionConfirm.setVisibility(View.GONE);
		tvTitle.setText(R.string.str_wait_mission);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		acceptTask();
	}

	private void onCancelMission() {
		llMissionConfirm.setVisibility(View.GONE);
		tvTitle.setText(R.string.str_wait_mission);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		cancelTask();
	}

	private void onGiveupMission() {
		showGiveupTaskAlert();
	}

	private void onCompleteMission() {
		finishTask();
	}

	private void onChatting() {
		m_dlgProgress.show();

		if(taskId == null) {
			taskId = "";
		}

		HttpAPI.requestChat(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), taskId, new VolleyCallback() {
			@RequiresApi(api = Build.VERSION_CODES.N)
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						m_dlgProgress.dismiss();

						LayoutInflater layoutInflater = LayoutInflater.from(getContext());
						View confirmView = layoutInflater.inflate(R.layout.alert_chart, null);

						final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

						TextView btnTitle = confirmView.findViewById(R.id.ID_TXTVIEW_TITLE);
						btnTitle.setText(jsonObject.getString("msg"));

						TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

						btnCancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								confirmDlg.dismiss();
							}
						});

						confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						confirmDlg.setView(confirmView);
						confirmDlg.show();

						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");

					RoomActivity.setChatId(dataObject.getString("roomId"));
					RoomActivity.setChatPass(dataObject.getString("password"));

					Intent intent = new Intent(getActivity(), RoomActivity.class);
					Objects.requireNonNull(getActivity()).startActivity(intent);
					m_dlgProgress.dismiss();
				}
				catch (JSONException e) {
					m_dlgProgress.dismiss();
					Util.ShowDialogError(getContext(), getString(R.string.str_api_failed));
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(getContext(), getString(R.string.str_api_failed));
			}
		}, "FragmentMission");
	}

	public void setTaskStatus() {
		switch (Prefs.Instance().getTaskStatus()) {
			case ActivityMain.TASK_STATUS_DISABLE:
			case ActivityMain.TASK_STATUS_READY:
				llGiveupMission.setVisibility(View.GONE);
				llTaskTop.setVisibility(View.GONE);
				llTaskBottom.setVisibility(View.GONE);
				tvTitle.setText(R.string.str_wait_mission);

				customerLat = "";
				customerLon = "";
				break;
			case ActivityMain.TASK_STATUS_WORKING:
				llGiveupMission.setVisibility(View.VISIBLE);
				llTaskTop.setVisibility(View.VISIBLE);
				llTaskBottom.setVisibility(View.VISIBLE);
				tvTitle.setText(R.string.str_task_processing);
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*if (requestCode == ActivityMain.REQUEST_FENCE_ADDEDIT) {
			if (resultCode == RESULT_OK) {
				if (data != null){
					boolean addOrEdit = data.getBooleanExtra("add_fence", true);
					ItemFence itemFence = (ItemFence) data.getSerializableExtra("fence_data");
					curFence.setFence(itemFence);
					if (addOrEdit) {
						fenceArray.add(curFence);
					}

					fenceAdapter.notifyDataSetChanged();
				}
			}
		}*/
	}

	public void setTaskId(String taskId) {
		if (taskId == null) {
			return;
		}
		this.taskId = taskId;
		Prefs.Instance().setTaskId(taskId);
		Prefs.Instance().commit();
	}

	private void showAnimMissionAlert() {
		llMissionConfirm.setVisibility(View.VISIBLE);
		Animation animSlideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top);
		llMissionConfirm.startAnimation(animSlideDown);
		tvTitle.setText(R.string.str_task_confirm);

		animSlideDown.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}

				iCount = 16;

				timer = new CountDownTimer(16000, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {
						iCount--;
						tvMissionConfirmTime.setText("(" + iCount + "S)");
					}
					@Override
					public void onFinish() {
						onCancelMission();
					}
				};
				timer.start();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	public void getTaskDetail() {
//		m_dlgProgress.show();

		HttpAPI.getTaskDetail(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), Integer.parseInt(taskId), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
//				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
//						String sMsg = jsonObject.getString("msg");
//						Util.ShowDialogError(getActivity(), sMsg);
						if (Prefs.Instance().getTaskStatus() == ActivityMain.TASK_STATUS_WORKING) {
							activityMain.setTaskStatus(ActivityMain.TASK_STATUS_READY);
							setTaskStatus();
						}
						return;
					}

					JSONObject dataJSONObj = jsonObject.getJSONObject("data");
					int taskStatus = dataJSONObj.getInt("task_status");
					if (taskStatus == ALARM_STATUS_WAIT_ALLOC){
						String strTask = dataJSONObj.getString("task_content");
						tvAidContent.setText(String.format(getResources().getString(R.string.str_aid_content), strTask));
						JSONObject infoJSONObj = dataJSONObj.getJSONObject("info");
						String strTitle = infoJSONObj.getString("title");
						tvAidKind.setText(strTitle);
						String strContent = infoJSONObj.has("content") ? infoJSONObj.getString("content") : "";
						tvAidStatus.setText(strContent);
						String strPlace = infoJSONObj.getString("place");
						tvAidPos.setText(strPlace);
						String strCreateTime = infoJSONObj.getString("create_time");
						tvAidCreateTime.setText(strCreateTime);

						customerLat = infoJSONObj.getString("lat");
						customerLon = infoJSONObj.getString("lon");
						setPointMap();
						showAnimMissionAlert();
						activityMain.setTaskStatus(ActivityMain.TASK_STATUS_WORKING);
						setTaskStatus();
					}else if (taskStatus == ALARM_STATUS_PENDING){
						String strTask = dataJSONObj.getString("task_content");
						tvMissionContent.setText(String.format(getResources().getString(R.string.str_aid_content), strTask));
						JSONObject infoJSONObj = dataJSONObj.getJSONObject("info");
						String strCreateTime = infoJSONObj.getString("create_time");
						tvBottomAidCreateTime.setText(strCreateTime);
						String strPlace = infoJSONObj.getString("place");
						tvBottomAidPos.setText(strPlace);
						String strTitle = infoJSONObj.getString("title");
						tvBottomAidKind.setText(strTitle);
						String strContent = infoJSONObj.has("content") ? infoJSONObj.getString("content") : "";
						tvBottomAidStatus.setText(strContent);
						String strIllness = infoJSONObj.getString("illness");
						tvBottomCurrentDisease.setText(strIllness);
						customerLat = infoJSONObj.getString("lat");
						customerLon = infoJSONObj.getString("lon");
						setPointMap();
						JSONArray statusJSONArray = dataJSONObj.getJSONArray("status");
						for(int i = 0; i < statusJSONArray.length(); i++) {
							JSONObject statusObj = statusJSONArray.getJSONObject(i);
							int volunteerNo = statusObj.getInt("volunteer_no");
							if (i == 0) {
								String distance = statusObj.getString("distance").substring(0, 5);
								Double d_distance = Double.valueOf(distance) * 1000;
								tvAidYouDistance.setText(String.format(getResources().getString(R.string.str_aid_you_distance), String.valueOf(d_distance)));
							} else if (i == 1) {
								String distance = statusObj.getString("distance");
								tvAid1Distance.setText(String.format(getResources().getString(R.string.str_aid_1_distance), distance));
							} else if (i == 2) {
								String distance = statusObj.getString("distance");
								tvAid2Distance.setText(String.format(getResources().getString(R.string.str_aid_2_distance), distance));
							} else if (i == 3) {
								String distance = statusObj.getString("distance");
								tvAid3Distance.setText(String.format(getResources().getString(R.string.str_aid_3_distance), distance));
							}
						}
						JSONArray contactJSONArray = dataJSONObj.getJSONArray("contact");
						for (int i = 0; i < contactJSONArray.length(); i++) {
							JSONObject contactObject = contactJSONArray.getJSONObject(i);
							String strPhone = contactObject.getString("phone");
							String strName = contactObject.getString("name");
							if (i == 0) {
								tvSOSContact1Phone.setText(strPhone);
								tvSOSContact1.setText(strName);
							} else if (i == 1) {
								tvSOSContact2Phone.setText(strPhone);
								tvSOSContact2.setText(strName);
							} else if (i == 2) {
								tvSOSContact3Phone.setText(strPhone);
								tvSOSContact3.setText(strName);
							}
						}
						activityMain.setTaskStatus(ActivityMain.TASK_STATUS_WORKING);
						setTaskStatus();
					}
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							getActivity(),
							getResources().getString(R.string.str_api_failed)
//							String.valueOf(e)+"getTaskDetail+catch"
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();

				Util.ShowDialogError(
						getActivity(),
						getResources().getString(R.string.str_api_failed)
//						String.valueOf(error)+"getTaskDetail+onerror"
				);
			}
		}, TAG);
	}

	private void finishTask() {
		m_dlgProgress.show();

		HttpAPI.finishTask(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), Integer.parseInt(taskId), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(getActivity(), sMsg);
						return;
					}

					activityMain.setTaskStatus(ActivityMain.TASK_STATUS_READY);
					setTaskStatus();
					setTaskId("0");
//					showCompleteTaskAlert();
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							getActivity(),
//							getResources().getString(R.string.str_api_failed)
							String.valueOf(e)+"finish catch"
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();

				Util.ShowDialogError(
						getActivity(),
//						getResources().getString(R.string.str_api_failed)
						String.valueOf(error)+"finish onerror"
				);
			}
		}, TAG);
	}

	private void cancelTask() {
		m_dlgProgress.show();

		HttpAPI.cancelTask(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), Integer.parseInt(taskId), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(getActivity(), sMsg);
						return;
					}

					activityMain.setTaskStatus(ActivityMain.TASK_STATUS_READY);
					setTaskStatus();
					setTaskId("0");
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							getActivity(),
//							getResources().getString(R.string.str_api_failed)
							String.valueOf(e)+"canceltask"
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();

				Util.ShowDialogError(
						getActivity(),
//						getResources().getString(R.string.str_api_failed)
						String.valueOf(error)+"canceltask"
				);
			}
		}, TAG);
	}

	private void acceptTask() {
		m_dlgProgress.show();

		HttpAPI.acceptTask(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(),  Integer.parseInt(taskId), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(getActivity(), sMsg);
						return;
					}

					getTaskDetail();
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							getActivity(),
//							getResources().getString(R.string.str_api_failed)
							String.valueOf(e)+"accepttask catch"
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();

				Util.ShowDialogError(
						getActivity(),
//						getResources().getString(R.string.str_api_failed)
						String.valueOf(error)+"accepttask onerror"
				);
			}
		}, TAG);
	}

    public void setPointMap() {
        setPointMap(activityMain.getLat(), activityMain.getLon());
    }

	public void setPointMap(double dLat, double dLon) {
		if (baiduMap == null) {
			return;
		}

		if (!customerLat.isEmpty() && !customerLon.isEmpty()) {
//			MyLocationData locData = new MyLocationData.Builder().accuracy(0)
//					.direction(-1)
//					.latitude(Double.parseDouble(customerLat))
//					.longitude(Double.parseDouble(customerLon))
//					.build();

//			baiduMap.setMyLocationData(locData);

			customerLatLng = new LatLng(Double.parseDouble(customerLat), Double.parseDouble(customerLon));

			if (isFirstLocation) {
				isFirstLocation = false;
				mapStatusBuilder.target(customerLatLng).zoom(18.0f);
				baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatusBuilder.build()));
			}
			baiduMap.clear();
			customerOverlayOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_customer)).position(customerLatLng).draggable(true);
			baiduMap.addOverlay(customerOverlayOptions);
			if (dLat != 0 && dLon != 0) {
				youLatLng = new LatLng(dLat, dLon);
				youOverlayOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_you)).position(youLatLng);
				baiduMap.addOverlay(youOverlayOptions);

			}
			if (customerLatLng != null && youLatLng != null) {
				drawTrack();
			}

		} else if (dLat != 0 && dLon != 0) {
//			MyLocationData locData = new MyLocationData.Builder().accuracy(0)
//					.direction(-1)
//					.latitude(dLat)
//					.longitude(dLon)
//					.build();

//			baiduMap.setMyLocationData(locData);

			LatLng yourLatLng = new LatLng(dLat, dLon);

			if (isFirstLocation) {
				isFirstLocation = false;
				mapStatusBuilder.target(yourLatLng).zoom(18.0f);
				baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatusBuilder.build()));
			}
			baiduMap.clear();
			youOverlayOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_you)).position(yourLatLng);
			baiduMap.addOverlay(youOverlayOptions);
		}
	}

	private void drawTrack() {
		if (youLatLng == null || customerLatLng == null)
			return;

		List<BitmapDescriptor> textureList = new ArrayList<>();
		textureList.add(BitmapDescriptorFactory.fromResource(R.drawable.track_dot));

		List<Integer> indexList = new ArrayList<>();
		indexList.add(0);

		List<LatLng> latlngs = new ArrayList<>();
		latlngs.add(customerLatLng);
		latlngs.add(youLatLng);

//		OverlayOptions ooPolyline = new PolylineOptions().width(20)
//				.color(getColor(R.color.color_tab_selected))
//				.points(latlngs).dottedLine(true)
//				.customTextureList(textureList)
//				.textureIndex(indexList);
//
//		if (sosPolyline != null)
//			sosPolyline.remove();
//
//		//Draw a line layer on the map, mPolyline: line layer
//		sosPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);
//		sosPolyline.setDottedLine(true);

		RoutePlanSearch routePlanSearch = RoutePlanSearch.newInstance();
		routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
			@Override
			public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

			}

			@Override
			public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

			}

			@Override
			public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

			}

			@Override
			public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
				if(drivingRouteResult.error== SearchResult.ERRORNO.NO_ERROR){
					for(int i = 0;i < drivingRouteResult.getRouteLines().size();i++){
						try{
							drawRouteLine(drivingRouteResult,i);
							break;
						}catch (NullPointerException e){
						}
					}
				}
			}

			@Override
			public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

			}

			@Override
			public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

			}
		});

		routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(PlanNode.withLocation(customerLatLng)).to(PlanNode.withLocation(youLatLng)).policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST));

//		animateMapStatus(latlngs);
	}

	public void drawRouteLine(DrivingRouteResult drivingRouteResult,int routeNum){
		//The first formal parameter is the result of the search, which is the collection of routes, and the second represents the first route.
		int[] color = {Color.BLACK,Color.BLUE,Color.CYAN,Color.DKGRAY
				,Color.GRAY,Color.GREEN,Color.LTGRAY,Color.YELLOW, Color.RED,Color.MAGENTA};//An array of colors used to randomly select a color to represent the route
		List<LatLng> linePoints = new ArrayList<>();//Collection of points on the route
		//A route of Baidu map is divided into sections, and getAllStep is to get all the sections of a route.
		//  Then use the point of the getWayPoints section on a road section. The point is usually a turn or intersection.
		for(int i = 0; i < drivingRouteResult.getRouteLines().get(routeNum).getAllStep().size();i++){
			for (int j = 0 ;j < drivingRouteResult.getRouteLines().get(routeNum).getAllStep().get(i).getWayPoints().size();j++){
				LatLng node = new LatLng(drivingRouteResult.getRouteLines().get(routeNum).getAllStep().get(i).getWayPoints().get(j).latitude
						,drivingRouteResult.getRouteLines().get(routeNum).getAllStep().get(i).getWayPoints().get(j).longitude);
				linePoints.add(node);//Add points to the collection
			}
			OverlayOptions ooPolyLine = new PolylineOptions().width(10).color(getActivity().getColor(R.color.color_tab_selected)).points(linePoints);//Set the properties of the polyline, color, etc.
			Polyline polyline = (Polyline) baiduMap.addOverlay(ooPolyLine);//Add to map
			polyline.setDottedLine(true);
		}
	}

	public void animateMapStatus(List<LatLng> points) {
		if (null == points || points.isEmpty()) {
			return;
		}
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (LatLng point : points) {
			builder.include(point);
		}
		MapStatusUpdate msUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
		baiduMap.animateMapStatus(msUpdate);
	}

}