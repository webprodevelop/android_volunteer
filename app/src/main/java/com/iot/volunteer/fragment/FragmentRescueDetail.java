//@formatter:off
package com.iot.volunteer.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.model.ItemRescue;
import com.iot.volunteer.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentRescueDetail extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private TextView						tvWatchName;
	private TextView						tvAidWatchPos;
	private TextView						tvAidType;
	private TextView						tvAidReportTime;
	private TextView						tvMissionStart;
	private TextView						tvMissionEnd;
	private TextView						tvMissionProgressView;
	private TextView						tvMissionPoint;

	private ItemRescue						itemRescue;

	static FragmentRescueDetail fragment = null;

	public static FragmentRescueDetail getInstance() {
		if (fragment == null) {
			fragment = new FragmentRescueDetail();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_rescue_detail, container, false);

		initControls(rootView);
		setEventListener();

		getRescueDetail();

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		tvWatchName = layout.findViewById(R.id.tvWatchName);
		tvAidWatchPos = layout.findViewById(R.id.tvAidWatchPos);
		tvAidType = layout.findViewById(R.id.tvAidType);
		tvAidReportTime = layout.findViewById(R.id.tvAidReportTime);
		tvMissionStart = layout.findViewById(R.id.tvMissionStart);
		tvMissionEnd = layout.findViewById(R.id.tvMissionEnd);
		tvMissionProgressView = layout.findViewById(R.id.tvMissionProgressView);
		tvMissionPoint = layout.findViewById(R.id.tvMissionPoint);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});
		tvMissionProgressView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentMissionProgress fragmentMissionProgress = new FragmentMissionProgress();
				activityMain.pushChildFragment(fragmentMissionProgress, FragmentMissionProgress.class.getSimpleName());
				fragmentMissionProgress.setActivity(activityMain);
				fragmentMissionProgress.setRescueItem(itemRescue);
			}
		});
	}

	public void setRescueInfo(ItemRescue itemRescue) {
		this.itemRescue = itemRescue;
	}

	private void getRescueDetail() {
		if (itemRescue == null) {
			return;
		}

		m_dlgProgress.show();

		HttpAPI.getRescueDetail(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemRescue.rescueId, new VolleyCallback() {
			@SuppressLint("StringFormatInvalid")
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

					JSONObject dataJSONObject = jsonObject.getJSONObject("data");

					itemRescue.rescueId = dataJSONObject.getInt("task_id");
					itemRescue.integral = dataJSONObject.getInt("point");
					itemRescue.label = dataJSONObject.getString("contactName");
					itemRescue.alertType = dataJSONObject.getString("task_content");
					itemRescue.alarmContent = dataJSONObject.getString("alarm_content");
					itemRescue.reportTime = dataJSONObject.getString("alarm_create_time");
					itemRescue.deviceType = dataJSONObject.getString("device_type");
					itemRescue.deviceSerial = dataJSONObject.getString("device_serial");
					itemRescue.contactPhone = dataJSONObject.getString("contactPhone");
					itemRescue.startTime = dataJSONObject.getString("create_time");

					itemRescue.endTime = "";
					if (dataJSONObject.has("finish_time")) {
						itemRescue.endTime = dataJSONObject.getString("finish_time");
					}

					tvWatchName.setText(itemRescue.label);
//					tvAidWatchPos.setText(String.format(getResources().getString(R.string.str_aid_watch_pos), itemRescue.location));
					tvAidType.setText(itemRescue.alertType);
					tvAidReportTime.setText(itemRescue.reportTime);
					tvMissionStart.setText(itemRescue.startTime);
					tvMissionEnd.setText(itemRescue.endTime);
					tvMissionPoint.setText(String.valueOf(itemRescue.integral));
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							getActivity(),
							getResources().getString(R.string.str_api_failed)
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
				);
			}
		}, TAG);
	}
}