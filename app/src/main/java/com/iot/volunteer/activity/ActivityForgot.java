//@formatter:off
package com.iot.volunteer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.App;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.R;
import com.iot.volunteer.util.Util;
import com.iot.volunteer.http.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityForgot extends ActivityBase {
	private final String	TAG = "ActivityForgot";

	private ImageView		m_ivBack;
	private EditText		m_edtAcquire;
	private EditText		m_edtPhone;
	private EditText		m_edtPswd;
	private EditText		m_edtPswdConfirm;
	private TextView		m_txtRequireAuthCode;
	private TextView		m_txtRequirePhone;
	private TextView		m_txtRequirePswd;
	private TextView		m_txtRequirePswdConfirm;
	private Button			m_btnConfirm;
	private Button			m_btnCancel;
	private Button			m_btnAcquire;

	private String		m_sAuthCode;
	private String		m_sPhone;
	private String		m_sPswd;

	private CountDownTimer		m_timer;
	private int					m_iCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//getWindow().setStatusBarColor(Color.WHITE);
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();
	}

	@Override
	protected void initControls() {
		super.initControls();

		m_ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		m_edtAcquire	= findViewById(R.id.ID_EDTTEXT_ACQUIRE);
		m_edtPhone		= findViewById(R.id.ID_EDTTEXT_PHONE);
		m_edtPswd		= findViewById(R.id.ID_EDTTEXT_PSWD);
		m_edtPswdConfirm		= findViewById(R.id.ID_EDTTEXT_CONFIRM_PSWD);
		m_txtRequireAuthCode	= findViewById(R.id.ID_TXTVIEW_REQUIRE_AUTH_CODE);
		m_txtRequirePhone	= findViewById(R.id.ID_TXTVIEW_REQUIRE_PHONE);
		m_txtRequirePswd	= findViewById(R.id.ID_TXTVIEW_REQUIRE_PSWD);
		m_txtRequirePswdConfirm	= findViewById(R.id.ID_TXTVIEW_REQUIRE_CONFIRM_PSWD);
		m_btnAcquire	= findViewById(R.id.ID_BUTTON_ACQUIRE);
		m_btnConfirm	= findViewById(R.id.ID_BUTTON_CONFIRM);
		m_btnCancel		= findViewById(R.id.ID_BUTTON_CANCEL);

		m_txtRequireAuthCode.setVisibility(View.INVISIBLE);
		m_txtRequirePhone.setVisibility(View.INVISIBLE);
		m_txtRequirePswd.setVisibility(View.INVISIBLE);
		m_txtRequirePswdConfirm.setVisibility(View.INVISIBLE);
	}

	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void afterTextChanged(Editable editable) {
			m_btnConfirm.setEnabled(!(m_edtPhone.getText().toString().isEmpty() && m_edtPswd.getText().toString().isEmpty() && m_edtAcquire.getText().toString().isEmpty() && m_edtPswdConfirm.getText().toString().isEmpty()));
		}
	};

	@Override
	protected void setEventListener() {
		m_ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		m_edtPhone.addTextChangedListener(textWatcher);
		m_edtAcquire.addTextChangedListener(textWatcher);
		m_edtPswd.addTextChangedListener(textWatcher);
		m_edtPswdConfirm.addTextChangedListener(textWatcher);

		m_btnAcquire.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_txtRequirePhone.setVisibility(View.INVISIBLE);

				// Check Value
				m_sPhone = m_edtPhone.getText().toString();
				if (m_sPhone.isEmpty()) {
//					String sMsg = getString(R.string.str_input_verify_code);
//					Util.ShowDialogError(ActivityForgot.this, sMsg);
					m_txtRequirePhone.setVisibility(View.VISIBLE);
					m_edtPhone.requestFocus();
					return;
				}
				if (m_sPhone.length() != 11) {
					String sMsg = getString(R.string.str_phone_number_incorrect);
					Util.ShowDialogError(ActivityForgot.this, sMsg);
					m_edtPhone.requestFocus();
					return;
				}

				TryGetCode();
			}
		});

		m_btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_txtRequireAuthCode.setVisibility(View.INVISIBLE);
				m_txtRequirePhone.setVisibility(View.INVISIBLE);
				m_txtRequirePswd.setVisibility(View.INVISIBLE);
				m_txtRequirePswdConfirm.setVisibility(View.INVISIBLE);

				// Check Value
				m_sPhone = m_edtPhone.getText().toString();
				if (m_sPhone.isEmpty()) {
					m_txtRequirePhone.setVisibility(View.VISIBLE);
					m_edtPhone.requestFocus();
					return;
				}
				m_sAuthCode = m_edtAcquire.getText().toString();
				if (m_sAuthCode.isEmpty()) {
					m_txtRequireAuthCode.setVisibility(View.VISIBLE);
					m_edtAcquire.requestFocus();
					return;
				}
				if (m_sPhone.length() != 11) {
					String sMsg = getString(R.string.str_phone_number_incorrect);
					Util.ShowDialogError(ActivityForgot.this, sMsg);
					return;
				}
				m_sPswd = m_edtPswd.getText().toString();
				if (m_sPswd.isEmpty()) {
					m_txtRequirePswd.setVisibility(View.VISIBLE);
					m_edtPswd.requestFocus();
					return;
				}
				if (m_sPswd.length() < 6 || m_sPswd.length() > 20) {
					String sMsg = getString(R.string.str_password_invalid);
					Util.ShowDialogError(ActivityForgot.this, sMsg);
					return;
				}
				if (m_edtPswdConfirm.getText().toString().isEmpty()) {
					m_txtRequirePswdConfirm.setVisibility(View.VISIBLE);
					m_edtPswdConfirm.requestFocus();
					return;
				}
				if (!m_edtPswdConfirm.getText().toString().equals(m_sPswd)) {
					Util.ShowDialogError(R.string.str_incorrect_confirm_password);
					m_edtPswdConfirm.requestFocus();
					return;
				}



				/*if (m_sAuthCode.compareTo(Global.CODE) != 0) {
					m_txtRequireAuthCode.setVisibility(View.VISIBLE);
					Util.ShowDialogError(
							ActivityForgot.this,
							getResources().getString(R.string.str_auth_code_failed)
					);
					return;
				}*/

				TryReset();
			}
		});

		m_btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_timer != null) {
					m_timer.cancel();
					m_timer = null;
				}

				App.Instance().cancelPendingRequests(TAG);
				finish();
			}
		});
	}

	void startAcquireCountDown() {
		m_btnAcquire.setEnabled(false);

		if (m_timer != null) {
			m_timer.cancel();
			m_timer = null;
		}

		m_iCount = 60;
		m_timer = new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				m_iCount--;
				int iMin = m_iCount / 60;
				int iSec = m_iCount % 60;
				String sMsg = "";
				if (iMin != 0)
					sMsg += iMin + getResources().getString(R.string.str_minute);
				sMsg += iSec + getResources().getString(R.string.str_second);
				m_btnAcquire.setText(sMsg);
			}
			@Override
			public void onFinish() {
				setAcpuireBtn();
//				String sMsg = getString(R.string.str_auth_code_not_available);
//				Util.ShowDialogError(ActivityForgot.this, sMsg);
			}
		};
		m_timer.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (m_timer != null) {
			m_timer.cancel();
			m_timer = null;
		}

		App.Instance().cancelPendingRequests(TAG);
	}

	private void TryGetCode() {
		startAcquireCountDown();

		HttpAPI.getVerifyCode(m_sPhone, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityForgot.this, sMsg, new Util.ResultProcess() {
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
					Util.ShowDialogError(ActivityForgot.this, getString(R.string.str_reset_failed));
					return;
				}
			}

			@Override
			public void onError(Object error) {
				setAcpuireBtn();
				Util.ShowDialogError(ActivityForgot.this, getString(R.string.str_reset_failed));
			}
		}, TAG);
	}

	private void setAcpuireBtn() {
		m_btnAcquire.setText(R.string.str_acquire_code);
		m_btnAcquire.setEnabled(true);
		if (m_timer != null) {
			m_timer.cancel();
			m_timer = null;
		}
	}

	private void TryReset() {
		m_dlgProgress.show();
		setAcpuireBtn();

		HttpAPI.forgotPassword(m_sPhone, m_sAuthCode, m_sPswd, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								switch (iRetCode) {
									case HttpAPIConst.RespCode.PHONE_BLANK:
									case HttpAPIConst.RespCode.PHONE_INVAILD:
										m_edtPhone.requestFocus();
										break;
									case HttpAPIConst.RespCode.ACCOUNT_NOT_EXIST:
										m_edtPhone.setText("");
										m_edtAcquire.setText("");
										m_edtPhone.requestFocus();
										break;
									case HttpAPIConst.RespCode.PASSWORD_INVAILD:
										m_edtPswd.requestFocus();
										break;
									case HttpAPIConst.RespCode.VALIDATE_CODE_BLANK:
									case HttpAPIConst.RespCode.VALIDATE_CODE_FAIL:
									case HttpAPIConst.RespCode.VALIDATE_CODE_EXPIRED:
										m_edtAcquire.setText("");
										m_edtAcquire.requestFocus();
										break;
									default:
								}
							}
						});
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
//					String strBirthday = dataObject.getString("birthday");
//					String strIDBack = dataObject.getString("id_card_back");
//					String strAddress = dataObject.getString("address");
//					String strCity = dataObject.getString("city");
//					boolean bSex = dataObject.getInt("sex") == 1 ? true : false;
//					String strIDFront = dataObject.getString("id_card_front");
					String strPhone = dataObject.getString("mobile");
//					String strCertificate = dataObject.getString("certificate");
//					String strPhoto = dataObject.getString("picture");
					String strToken = dataObject.getString("token");
//					String strIDNum = dataObject.getString("id_card_num");
//					String strCertPic = dataObject.getString("certificate_pic");
//					String strProvince = dataObject.getString("province");
//					String strDistrict = dataObject.getString("district");
//					String strUser = dataObject.getString("name");
//					String strCompany = dataObject.getString("company");
//					String strResidence = dataObject.getString("residence");
//					String strJob = dataObject.getString("job");
//					int iPoint = (int) dataObject.getDouble("point_level");
//					int iMerit = (int) dataObject.getDouble("point");
//					float fCash = (float) dataObject.getDouble("cash");
//					String strBankName = dataObject.getString("bank_account_name");
//					String strBankId = dataObject.getString("bank_account_id");
////					String strBankPassword = dataObject.getString("bank_account_pwd");
//					String strAlipayName = dataObject.getString("alipay_account_name");
//					String strAlipayId = dataObject.getString("alipay_account_id");
//					String strWeixinName = dataObject.getString("weixin_account_name");
//					String strWeixinId = dataObject.getString("weixin_account_id");
//					String strStatus = dataObject.getString("status");

//					if (strStatus.equals("DISABLED")) {
//						Prefs.Instance().setTaskStatus(0);
//					} else if (strStatus.equals("READY")) {
//						Prefs.Instance().setTaskStatus(1);
//					} else if (strStatus.equals("WORKING")) {
//						Prefs.Instance().setTaskStatus(2);
//					}
//					Prefs.Instance().setBankName(strBankName);
//					Prefs.Instance().setBankId(strBankId);
////					Prefs.Instance().setBankPassword(strBankPassword);
//					Prefs.Instance().setAlipayName(strAlipayName);
//					Prefs.Instance().setAlipayId(strAlipayId);
//					Prefs.Instance().setWeixinName(strWeixinName);
//					Prefs.Instance().setWeixinId(strWeixinId);
//					Prefs.Instance().setMerit(iMerit);
//					Prefs.Instance().setCash(fCash);
//					Prefs.Instance().setPoint(iPoint);
//					Prefs.Instance().setBirthday(strBirthday);
//					Prefs.Instance().setIDCardBack(strIDBack);
//					Prefs.Instance().setAddress(strAddress);
//					Prefs.Instance().setCity(strCity);
//					Prefs.Instance().setSex(bSex);
//					Prefs.Instance().setIDCardFront(strIDFront);
//					Prefs.Instance().setCertificate(strCertificate);
//					Prefs.Instance().setUserPhoto(strPhoto);
//					Prefs.Instance().setIDCardNum(strIDNum);
//					Prefs.Instance().setCertPic(strCertPic);
//					Prefs.Instance().setProvince(strProvince);
//					Prefs.Instance().setDistrict(strDistrict);
//					Prefs.Instance().setUserName(strUser);
//					Prefs.Instance().setCompany(strCompany);
//					Prefs.Instance().setResidence(strResidence);
//					Prefs.Instance().setJob(strJob);
					Prefs.Instance().setUserPhone(strPhone);
					Prefs.Instance().setUserPswd(m_sPswd);
					Prefs.Instance().setUserToken(strToken);
					Prefs.Instance().commit();
				} catch (JSONException e) {
					Util.ShowDialogError(ActivityForgot.this, getResources().getString(R.string.str_reset_failed));
					return;
				}
//				Prefs.Instance().setUserPswd(m_sPswd);

				/*Util.ShowDialogSuccess(
						ActivityForgot.this,
						getResources().getString(R.string.str_reset_success)
				);*/
				Util.showToastMessage(ActivityForgot.this, R.string.str_reset_success);
				StartLogin();
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(ActivityForgot.this, getResources().getString(R.string.str_reset_failed));
			}
		}, TAG);
	}

	private void StartLogin() {
		Intent intent = new Intent(ActivityForgot.this, ActivityLogin.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}
}
