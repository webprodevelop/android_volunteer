//@formatter:off
package com.iot.volunteer.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class ActivityAccountManage extends ActivityBase implements View.OnClickListener {
	private final String TAG = "ActivityAccountManage";

	private ImageView						ivBack;
	private TextView						tvTitle;

	private EditText						edtNewAccount;
	private TextView						tvRequiredNewAccount;

	private EditText						edtVerifyCode;
	private TextView						tvRequiredAcquireCode;

	private Button							btnAcquire;
	private TextView						tvConfirm;

	private CountDownTimer		m_timer;
	private int					m_iCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_change_mobile);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}
		initControls();
		setEventListener();

	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ivBack);
		tvTitle = findViewById(R.id.tvTitle);

		TextView tvCurAccount = findViewById(R.id.ID_TXTVIEW_CUR_ACCOUNT);
		edtNewAccount = findViewById(R.id.ID_EDTTEXT_NEW_ACCOUNT);
		tvRequiredNewAccount = findViewById(R.id.tvRequiredNewAccount);
		edtVerifyCode = findViewById(R.id.ID_EDTTEXT_VERIFY_CODE);
		tvRequiredAcquireCode = findViewById(R.id.tvRequiredAcquireCode);
		btnAcquire = findViewById(R.id.ID_BUTTON_ACQUIRE);
		tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);

		tvRequiredNewAccount.setVisibility(View.GONE);
		tvRequiredAcquireCode.setVisibility(View.GONE);

		tvCurAccount.setText(Prefs.Instance().getUserPhone());
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		btnAcquire.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
		edtNewAccount.setOnClickListener(this);
		edtVerifyCode.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ivBack:
				finish();
				break;
			case R.id.ID_BUTTON_ACQUIRE:
				onVerify();
				break;
			case R.id.ID_BTN_CONFIRM:
				onConfirm();
				break;
			case R.id.ID_EDTTEXT_NEW_ACCOUNT:
				tvRequiredNewAccount.setVisibility(View.GONE);
				break;
			case R.id.ID_EDTTEXT_VERIFY_CODE:
				tvRequiredAcquireCode.setVisibility(View.GONE);
				break;
		}
	}

	private void onVerify() {
		if (edtNewAccount.getText().toString().isEmpty()) {
			tvRequiredNewAccount.setVisibility(View.VISIBLE);
			edtNewAccount.requestFocus();
			return;
		}
		if (edtNewAccount.getText().toString().length() != 11) {
			tvRequiredAcquireCode.setVisibility(View.VISIBLE);
			Util.ShowDialogError(R.string.str_phone_number_incorrect);
			edtNewAccount.requestFocus();
			return;
		}

		getVerifyCode();
	}

	private void onConfirm() {
		if (edtNewAccount.getText().toString().isEmpty()) {
			tvRequiredNewAccount.setVisibility(View.VISIBLE);
			edtNewAccount.requestFocus();
			return;
		}
		if (edtNewAccount.getText().toString().length() != 11) {
			Util.ShowDialogError(R.string.str_phone_number_incorrect);
			edtNewAccount.requestFocus();
			return;
		}
		if (edtVerifyCode.getText().toString().isEmpty()) {
			tvRequiredAcquireCode.setVisibility(View.VISIBLE);
			edtVerifyCode.requestFocus();
			return;
		}

		updateMobile();
	}

	@Override
	public void onDestroy () {
		super.onDestroy();

		if (m_timer != null) {
			m_timer.cancel();
			m_timer = null;
		}
	}

	void startAcquireCountDown() {
		btnAcquire.setEnabled(false);
		if (m_timer != null) {
			m_timer.cancel();
			m_timer = null;
		}

		m_iCount = 61;
		m_timer = new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				m_iCount--;
//				int iMin = m_iCount / 60;
//				int iSec = m_iCount % 60;
//				String sMsg = "";
//				if (iMin != 0)
//					sMsg += iMin + getResources().getString(R.string.str_minute);
//				sMsg += iSec + getResources().getString(R.string.str_second);
				String sMsg = m_iCount + getResources().getString(R.string.str_second);
				btnAcquire.setText(sMsg);
			}
			@Override
			public void onFinish() {
				setAcpuireBtn();
//				String sMsg = getString(R.string.str_auth_code_not_available);
//				Util.ShowDialogError(getActivity(), sMsg);
			}
		};
		m_timer.start();
	}

	private void setAcpuireBtn() {
		btnAcquire.setText(R.string.str_acquire_code);
		btnAcquire.setEnabled(true);
		if (m_timer != null) {
			m_timer.cancel();
			m_timer = null;
		}
	}

	private void getVerifyCode() {
		startAcquireCountDown();

		HttpAPI.getUpdateUserInfoVerifyCode(edtNewAccount.getText().toString(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityAccountManage.this, sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								setAcpuireBtn();
							}
						});

						return;
					}
					//Global.CODE = jsonObject.getString("verifyCode");
				}
				catch (JSONException e) {
					setAcpuireBtn();
					Util.ShowDialogError(ActivityAccountManage.this, getString(R.string.str_auth_code_not_available));
					return;
				}
			}

			@Override
			public void onError(Object error) {
				setAcpuireBtn();
				Util.ShowDialogError(ActivityAccountManage.this, getString(R.string.str_auth_code_not_available));
			}
		}, TAG);
	}

	private void updateMobile() {
		m_dlgProgress.show();

		if (m_timer != null) {
			m_timer.cancel();
		}

		final String strNewMobile = edtNewAccount.getText().toString();
		String strVerifyCode = edtVerifyCode.getText().toString();

		HttpAPI.updateMobile(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), strNewMobile, strVerifyCode, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						setAcpuireBtn();
						Util.ShowDialogError(sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								switch (iRetCode) {
									case HttpAPIConst.RespCode.PHONE_BLANK:
									case HttpAPIConst.RespCode.PHONE_INVAILD:
										edtNewAccount.requestFocus();
										break;
									case HttpAPIConst.RespCode.PHONE_REGISTERED:
										edtNewAccount.setText("");
										edtVerifyCode.setText("");
										edtNewAccount.requestFocus();
										break;
									case HttpAPIConst.RespCode.VALIDATE_CODE_BLANK:
									case HttpAPIConst.RespCode.VALIDATE_CODE_FAIL:
									case HttpAPIConst.RespCode.VALIDATE_CODE_EXPIRED:
										edtVerifyCode.setText("");
										edtVerifyCode.requestFocus();
										break;
									default:
								}
							}
						});
						return;
					}

					Prefs.Instance().setUserPhone(strNewMobile);
					Prefs.Instance().commit();
					Util.showToastMessage(ActivityAccountManage.this, R.string.str_change_mobile_success);
					finish();
//					FragmentUser fragmentUser = new FragmentUser();
//					activityMain.setChildFragment(fragmentUser, FragmentUser.class.getSimpleName());
//					fragmentUser.setActivity(activityMain);
				}
				catch (JSONException e) {
					setAcpuireBtn();
					Util.ShowDialogError(R.string.str_page_loading_failed);
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				setAcpuireBtn();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}
}