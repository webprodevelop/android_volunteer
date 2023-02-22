//@formatter:off
package com.iot.volunteer.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iot.volunteer.BuildConfig;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.activity.ActivityAccountManage;
import com.iot.volunteer.activity.ActivityAgree;
import com.iot.volunteer.activity.ActivityChangePassword;
import com.iot.volunteer.activity.ActivityLogin;
import com.iot.volunteer.activity.ActivityMain;
import com.iot.volunteer.activity.ActivityUserData;
import com.iot.volunteer.database.IOTDBHelper;
import com.iot.volunteer.helper.RoomActivity;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.model.ItemWatchInfo;
import com.iot.volunteer.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentUser extends FragmentBase implements View.OnClickListener {
	private CircleImageView ivPhoto;
	private TextView		tvPointLevel;
	private TextView		tvPhoneNumber;
	private RelativeLayout	llUserData;
	private RelativeLayout	llPointLevel;
	private RelativeLayout	llRescueQuery;
	private RelativeLayout	llBankAccount;
	private RelativeLayout	llAlipayAccount;
	private RelativeLayout	llWeixinAccount;
	private RelativeLayout	llPaymentManager;
	private RelativeLayout	llContactCustomer;
	private RelativeLayout	llUpdateMobile;
	private RelativeLayout	llUpdatePassword;
	private RelativeLayout	llAgree;
	private RelativeLayout	llPrivacy;
	private RelativeLayout	llAppVersion;
	private Button			btnLogout;

	private IOTDBHelper iotdbHelper;

	private TextView		txtVersion;

	static FragmentUser fragment = null;

	private ItemWatchInfo							moniteringWatchInfo = null;

	public static FragmentUser getInstance() {
		if (fragment == null) {
			fragment = new FragmentUser();
		}
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		iotdbHelper = new IOTDBHelper(context);
		super.onAttach(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_user, container, false);

		initControls(rootView);
		setEventListener();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		setUserInfo();
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivPhoto = layout.findViewById(R.id.ivPhoto);
		tvPhoneNumber = layout.findViewById(R.id.tvPhoneNumber);
		tvPointLevel = layout.findViewById(R.id.tvPointLevel);
		llUserData = layout.findViewById(R.id.llUserData);
		llPointLevel = layout.findViewById(R.id.llPointLevel);
		llRescueQuery = layout.findViewById(R.id.llRescueQuery);
		llBankAccount = layout.findViewById(R.id.llBankAccount);
		llAlipayAccount = layout.findViewById(R.id.llAlipayAccount);
		llWeixinAccount = layout.findViewById(R.id.llWeixinAccount);
		llPaymentManager = layout.findViewById(R.id.llPaymentManager);
		llContactCustomer = layout.findViewById(R.id.llContactCustomer);
		llUpdateMobile = layout.findViewById(R.id.llUpdateMobile);
		llUpdatePassword = layout.findViewById(R.id.llUpdatePassword);
		llAgree = layout.findViewById(R.id.llAgreement);
		llPrivacy = layout.findViewById(R.id.llPrivacy);
		llAppVersion = layout.findViewById(R.id.llAppVersion);
		btnLogout = layout.findViewById(R.id.btnLogout);
		txtVersion = layout.findViewById(R.id.txtVersion);
		txtVersion.setText(getResources().getString(R.string.str_app_version) + BuildConfig.VERSION_NAME);

		setUserInfo();
	}

	/*public void showSOSContactNotification(boolean show) {
		if (show) {
			llRescueQuery.setVisibility(View.VISIBLE);
		} else {
			llRescueQuery.setVisibility(View.GONE);
		}

		showInfoNotification();
	}*/

	/*public void showPushManageNotification(boolean show) {
		if (show) {
			mPushManageNotiView.setVisibility(View.VISIBLE);
		} else {
			mPushManageNotiView.setVisibility(View.GONE);
		}

		showInfoNotification();
	}*/

	/*private void showInfoNotification() {
		boolean showInfoNoti = (llRescueQuery.getVisibility() == View.VISIBLE || mPushManageNotiView.getVisibility() == View.VISIBLE);

		Prefs.Instance().setInfoNotification(showInfoNoti);
		Prefs.Instance().commit();
		((ActivityMain)getActivity()).showInfoNotification();
	}*/

	private void setUserInfo() {
		tvPhoneNumber.setText(Prefs.Instance().getUserPhone());
		if (!Prefs.Instance().getUserPhoto().isEmpty()) {
			Picasso.get().load(Prefs.Instance().getUserPhoto()).placeholder(R.drawable.img_contact).into(ivPhoto);
		} else {
			ivPhoto.setImageResource(R.drawable.img_contact);
		}

		int point = Prefs.Instance().getPoint();
		if (point > 5) point = 5;
		if (point < 0) point = 0;
		if (point == 0) {
			tvPointLevel.setText("");
		} else {
			tvPointLevel.setText("V" + point);
		}
	}

	@Override
	protected void setEventListener() {
		llUserData.setOnClickListener(this);
		llPointLevel.setOnClickListener(this);
		llRescueQuery.setOnClickListener(this);
		llBankAccount.setOnClickListener(this);
		llAlipayAccount.setOnClickListener(this);
		llWeixinAccount.setOnClickListener(this);
		llPaymentManager.setOnClickListener(this);
		llContactCustomer.setOnClickListener(this);
		llUpdateMobile.setOnClickListener(this);
		llUpdatePassword.setOnClickListener(this);
		llAgree.setOnClickListener(this);
		llPrivacy.setOnClickListener(this);
		llAppVersion.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.llUserData:
				onUserData();
				break;
			case R.id.llPointLevel:
				onPointLevel();
				break;
			case R.id.llRescueQuery:
				onRescueQuery();
				break;
			case R.id.llBankAccount:
				onBankAccount();
				break;
			case R.id.llAlipayAccount:
				onAlipayAccount();
				break;
			case R.id.llWeixinAccount:
				onWeixinAccount();
				break;
			case R.id.llPaymentManager:
				onPaymentManager();
				break;
			case R.id.llContactCustomer:
				onContactCustomer();
				break;
			case R.id.llUpdateMobile:
				onChangeMobile();
				break;
			case R.id.llUpdatePassword:
				onChangePassword();
				break;
			case R.id.llAgreement:
				onAgree();
				break;
			case R.id.llPrivacy:
				onPrivacy();
				break;
			case R.id.llAppVersion:
				onAppVersion();
				break;
			case R.id.btnLogout:
				onLogout();
				break;
		}
	}

	private void onUserData() {
		Intent intent = new Intent(getActivity(), ActivityUserData.class);
		getActivity().startActivity(intent);
	}

	private void onPointLevel() {
		FragmentPointLevel fragmentPointLevel = new FragmentPointLevel();
		activityMain.pushChildFragment(fragmentPointLevel, FragmentPointLevel.class.getSimpleName());
		fragmentPointLevel.setActivity(activityMain);
	}

	private void onRescueQuery() {
		FragmentRescueQuery fragmentRescueQuery = new FragmentRescueQuery();
		activityMain.pushChildFragment(fragmentRescueQuery, FragmentRescueQuery.class.getSimpleName());
		fragmentRescueQuery.setActivity(activityMain);
	}

	private void onBankAccount() {
		activityMain.pushChildFragment(FragmentBankAccount.getInstance(), FragmentBankAccount.class.getSimpleName());
		FragmentBankAccount.getInstance().setActivity(activityMain);
	}

	private void onAlipayAccount() {
		activityMain.pushChildFragment(FragmentAlipayAccount.getInstance(), FragmentAlipayAccount.class.getSimpleName());
		FragmentAlipayAccount.getInstance().setActivity(activityMain);
	}

	private void onWeixinAccount() {
		activityMain.pushChildFragment(FragmentWeixinAccount.getInstance(), FragmentWeixinAccount.class.getSimpleName());
		FragmentWeixinAccount.getInstance().setActivity(activityMain);
	}

	private void onPaymentManager() {
		activityMain.pushChildFragment(FragmentPaymentManager.getInstance(), FragmentPaymentManager.class.getSimpleName());
		FragmentPaymentManager.getInstance().setActivity(activityMain);
	}

	private void onContactCustomer() {
		HttpAPI.requestChat(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), "0", new VolleyCallback() {
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

	private void onChangeMobile() {
		Intent intent = new Intent(getActivity(), ActivityAccountManage.class);
		getActivity().startActivity(intent);
	}

	private void onChangePassword() {
		Intent intent = new Intent(getActivity(), ActivityChangePassword.class);
		getActivity().startActivity(intent);
	}

	private void onAgree() {
		Intent intent = new Intent(getContext(), ActivityAgree.class);
		intent.putExtra("agreement_policy", true);
		startActivity(intent);
	}

	private void onPrivacy() {
		Intent intent = new Intent(getContext(), ActivityAgree.class);
		intent.putExtra("agreement_policy", false);
		startActivity(intent);
	}

	private void onAppVersion() {
		//
	}

	private void onLogout() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View confirmView = layoutInflater.inflate(R.layout.alert_logout, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmDlg.dismiss();
			}
		});

		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmDlg.dismiss();

				iotdbHelper.clearAll();
//				Prefs.Instance().setUserPswd("");
//				Prefs.Instance().commit();

				Intent intent = new Intent(getActivity(), ActivityLogin.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.putExtra("log_out", true);
				getActivity().startActivity(intent);
			}
		});

		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.show();
	}
}