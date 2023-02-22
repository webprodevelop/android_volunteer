//@formatter:off
package com.iot.volunteer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentBankAccountSetting extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private ImageView						ivPhoto;
	private EditText						edtBank;
	private EditText						edtBankAccount;
	private EditText						edtSetPassword;
	private EditText						edtConfirmPassword;
	private TextView						tvSave;

	private boolean							manual = false;
	private String 							bankName = "";
	private String							bankCard = "";

	static FragmentBankAccountSetting fragment = null;

	public static FragmentBankAccountSetting getInstance() {
		if (fragment == null) {
			fragment = new FragmentBankAccountSetting();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_bank_account_setting, container, false);

		initControls(rootView);
		setEventListener();
		loadData();

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		ivPhoto = layout.findViewById(R.id.ivPhoto);
		edtBank = layout.findViewById(R.id.edtBank);
		edtBankAccount = layout.findViewById(R.id.edtBankAccount);
		edtSetPassword = layout.findViewById(R.id.edtSetPassword);
		edtConfirmPassword = layout.findViewById(R.id.edtConfirmPassword);
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
				onSave();
			}
		});
	}

	private void loadData() {
		if (manual) {
			ivPhoto.setVisibility(View.GONE);
			edtBank.setEnabled(true);
			edtBankAccount.setEnabled(true);
		} else  {
			ivPhoto.setVisibility(View.VISIBLE);
			edtBank.setEnabled(false);
			edtBankAccount.setEnabled(false);
		}

		edtBank.setText(bankName);
		edtBankAccount.setText(bankCard);
	}

	public void setBankSettings(boolean manual, String bankName, String bankCard) {
		this.manual = manual;
		this.bankName = bankName;
		this.bankCard = bankCard;
	}

	private void onSave() {
		if (edtSetPassword.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_password);
			edtSetPassword.requestFocus();
			return;
		} else if (edtConfirmPassword.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_confirm_password);
			edtConfirmPassword.requestFocus();
			return;
		} else if (!edtSetPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
			Util.ShowDialogError(R.string.str_incorrect_confirm_password);
			edtConfirmPassword.requestFocus();
			return;
		}

		registerBankCard();
	}

	private void registerBankCard() {
		m_dlgProgress.show();

		final String strBankName = edtBank.getText().toString();
		final String strBankId = edtBankAccount.getText().toString();
		String strPassword = edtSetPassword.getText().toString();

		HttpAPI.registerBankCard(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), strBankName, strBankId, strPassword, new VolleyCallback() {
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

					Prefs.Instance().setBankName(strBankName);
					Prefs.Instance().setBankId(strBankId);
					Prefs.Instance().commit();

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