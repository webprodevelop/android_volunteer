//@formatter:off
package com.iot.volunteer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.R;
import com.iot.volunteer.model.ItemRescue;

public class FragmentMissionProgress extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvNotificationTime;
	private TextView						tvContactAidAgency;
	private TextView						tvPhoneNumber;
	private TextView						tvTask;
	private TextView						tvAlarmContent;
	private TextView						tvDevice;
	private TextView						tvSerial;
	private TextView						tvPoint;
	private TextView						tvMissionStartTime;
	private TextView						tvMissionEndTime;

	private ItemRescue						itemRescue;

	static FragmentMissionProgress fragment = null;

	public static FragmentMissionProgress getInstance() {
		if (fragment == null) {
			fragment = new FragmentMissionProgress();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_mission_progress, container, false);

		initControls(rootView);
		setEventListener();
		setData();

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvNotificationTime = layout.findViewById(R.id.tvNotificationTime);
		tvContactAidAgency = layout.findViewById(R.id.tvContactAidAgency);
		tvPhoneNumber = layout.findViewById(R.id.tvPhoneNumber);
		tvTask = layout.findViewById(R.id.tvTask);
		tvAlarmContent = layout.findViewById(R.id.tvAlarmContent);
		tvDevice = layout.findViewById(R.id.tvDevice);
		tvSerial = layout.findViewById(R.id.tvSerial);
		tvPoint = layout.findViewById(R.id.tvPoint);
		tvMissionStartTime = layout.findViewById(R.id.tvMissionStartTime);
		tvMissionEndTime = layout.findViewById(R.id.tvMissionEndTime);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});
	}

	private void setData() {
		if (itemRescue == null) {
			return;
		}

		tvNotificationTime.setText(itemRescue.reportTime);
		tvContactAidAgency.setText(itemRescue.label);
		tvPhoneNumber.setText(itemRescue.contactPhone);
		tvTask.setText(itemRescue.alertType);
		tvAlarmContent.setText(itemRescue.alarmContent);
		tvDevice.setText(itemRescue.deviceType);
		tvSerial.setText(itemRescue.deviceSerial);
		tvPoint.setText(String.valueOf(itemRescue.integral));
		tvMissionStartTime.setText(itemRescue.startTime);
		tvMissionEndTime.setText(itemRescue.endTime);
	}

	public void setRescueItem(ItemRescue itemRescue) {
		this.itemRescue = itemRescue;
	}
}