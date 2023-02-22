//@formatter:off
package com.iot.volunteer.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.BuildConfig;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.activity.ActivityLogin;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentAlipayAccount extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private EditText 						edtAlipay;
	private EditText						edtAlipayAccount;
	private TextView						tvSave;

	static FragmentAlipayAccount fragment = null;

	public static FragmentAlipayAccount getInstance() {
		if (fragment == null) {
			fragment = new FragmentAlipayAccount();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_alipay_account, container, false);

		initControls(rootView);
		setEventListener();
		loadData();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		edtAlipay = layout.findViewById(R.id.edtAlipay);
		edtAlipayAccount = layout.findViewById(R.id.edtAlipayAccount);
		tvSave = layout.findViewById(R.id.tvSave);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});
		tvSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				m_dlgProgress.show();
				HttpAPI.registerPayAccount(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), "1", edtAlipayAccount.getText().toString(), edtAlipay.getText().toString(), new VolleyCallback() {
					@Override
					public void onSuccess(String response) {
						m_dlgProgress.dismiss();
						try {
							JSONObject jsonObject = new JSONObject(response);
							int iRetCode = jsonObject.getInt("retcode");
							if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
								String sMsg = jsonObject.getString("msg");
								Util.ShowDialogError(getContext(), sMsg);
								return;
							}

							JSONObject dataObject = jsonObject.getJSONObject("data");
							String strAlipayName = dataObject.getString("alipay_account_name");
							String strAlipayId = dataObject.getString("alipay_account_id");

							Prefs.Instance().setAlipayName(strAlipayName);
							Prefs.Instance().setAlipayId(strAlipayId);
							Prefs.Instance().commit();

							m_dlgProgress.dismiss();
						}
						catch (JSONException e) {
							Util.ShowDialogError(
									getContext(),
									getResources().getString(R.string.str_page_loading_failed)
							);
							return;
						}
					}

					@Override
					public void onError(Object error) {
						m_dlgProgress.dismiss();
						Util.ShowDialogError(
								getContext(),
								getResources().getString(R.string.str_page_loading_failed)
						);
					}
				}, TAG);
			}
		});
	}

	private void loadData() {
		String alipayName = Prefs.Instance().getAlipayName();
		if (alipayName != null && !alipayName.isEmpty()) {
			edtAlipay.setText(Prefs.Instance().getAlipayName());
		}
		String alipayId = Prefs.Instance().getAlipayId();
		if (alipayId != null && alipayId.length() > 8) {
			edtAlipayAccount.setText(Prefs.Instance().getAlipayId());
		}
	}
}