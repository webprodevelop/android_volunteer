//@formatter:off
package com.iot.volunteer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentBankAccountForgot extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private EditText						edtAccountNumber;
	private EditText						edtNewPassword;
	private EditText						edtConfirmPassword;
	private EditText						edtAcquireCode;
	private Button							btnAcquire;
	private TextView						btnConfirm;

	static FragmentBankAccountForgot fragment = null;

	public static FragmentBankAccountForgot getInstance() {
		if (fragment == null) {
			fragment = new FragmentBankAccountForgot();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_bank_account_forgot, container, false);

		initControls(rootView);
		setEventListener();

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		edtAccountNumber = layout.findViewById(R.id.edtAccountNumber);
		edtNewPassword = layout.findViewById(R.id.edtNewPassword);
		edtConfirmPassword = layout.findViewById(R.id.edtConfirmPassword);
		edtAcquireCode = layout.findViewById(R.id.edtAcquireCode);
		btnAcquire = layout.findViewById(R.id.btnAcquire);
		btnConfirm = layout.findViewById(R.id.btnConfirm);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});
		btnAcquire.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onConfirm();
			}
		});
	}

	private void onConfirm() {
		if (edtAccountNumber.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_account_number);
			edtAccountNumber.requestFocus();
			return;
		} else if (edtNewPassword.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_new_password);
			edtNewPassword.requestFocus();
			return;
		} else if (edtNewPassword.length() < 6 || edtNewPassword.length() > 20) {
			Util.ShowDialogError(R.string.str_password_invalid);
			edtNewPassword.requestFocus();
			return;
		} else if (edtConfirmPassword.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_confirm_password);
			edtConfirmPassword.requestFocus();
			return;
		} else if (!edtNewPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
			Util.ShowDialogError(R.string.str_incorrect_confirm_password);
			edtConfirmPassword.requestFocus();
			return;
		}

		forgotBankPassword();
	}

	private void forgotBankPassword() {
		m_dlgProgress.show();

		final String strNewPassword = edtNewPassword.getText().toString();
		String strVerifyCode = edtAcquireCode.getText().toString();

		HttpAPI.forgotBankPassword(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), strNewPassword, strVerifyCode, new VolleyCallback() {
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

					activityMain.popChildFragment();
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							getActivity(),
							getResources().getString(R.string.str_change_password_failed)
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(
						getActivity(),
						getResources().getString(R.string.str_change_password_failed)
				);
			}
		}, TAG);
	}
}