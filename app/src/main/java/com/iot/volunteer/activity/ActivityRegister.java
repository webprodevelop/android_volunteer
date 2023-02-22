//@formatter:off
package com.iot.volunteer.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.App;
import com.iot.volunteer.BuildConfig;
import com.iot.volunteer.Global;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class ActivityRegister extends ActivityBase {
	private final String	TAG = "ActivityRegister";

	private ImageView		ivBack;
	private EditText		edtAcquire;
	private EditText		edtPhone;
	private EditText		edtPwd;
	private EditText		edtConfirmPwd;
	private TextView		tvRequiredAuthCode;
	private TextView		tvRequiredPhone;
	private TextView		tvRequiredPwd;
	private TextView		rvRequiredConfirmPwd;
	private TextView		tvAgree;
	private Button			btnRegister;
	private Button			m_btnCancel;
	private Button			btnAcquire;
	private ImageView		ivCheckAgree;

	private String		m_sAuthCode;
	private String		m_sPhone;
	private String		m_sPswd;

	private CountDownTimer		m_timer;
	private int					m_iCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

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

		ivBack = findViewById(R.id.ivBack);
		edtAcquire	= findViewById(R.id.edtAcquire);
		edtPhone		= findViewById(R.id.edtPhone);
		edtPwd		= findViewById(R.id.edtPwd);
		edtConfirmPwd		= findViewById(R.id.edtConfirmPwd);
		tvRequiredAuthCode	= findViewById(R.id.tvRequiredAuthCode);
		tvRequiredPhone	= findViewById(R.id.tvRequiredPhone);
		tvRequiredPwd	= findViewById(R.id.tvRequiredPwd);
		rvRequiredConfirmPwd	= findViewById(R.id.rvRequiredConfirmPwd);
		tvAgree	= findViewById(R.id.tvAgree);
		btnRegister	= findViewById(R.id.btnRegister);
		m_btnCancel	= findViewById(R.id.btnCancel);
		btnAcquire	= findViewById(R.id.btnAcquire);
		ivCheckAgree	= findViewById(R.id.ivCheckAgree);

		String strAgreeText = getResources().getString(R.string.str_agree_text);
		String strAgreeUserAgreement = getResources().getString(R.string.str_agree_user_agreement);
		String strAgreePrivacyPolicy = getResources().getString(R.string.str_agree_privacy_policy);
		int indexAgreeUserAgreement = strAgreeText.indexOf(strAgreeUserAgreement);
		int indexAgreePrivacyPolicy = strAgreeText.indexOf(strAgreePrivacyPolicy);
		SpannableString agreeText = new SpannableString(strAgreeText);
		ClickableSpan clickableUserAgreement = new ClickableSpan() {
			@Override
			public void onClick(View textView) {
				Intent intent = new Intent(ActivityRegister.this, ActivityAgree.class);
				intent.putExtra("agreement_policy", true);
				startActivity(intent);
			}
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setUnderlineText(false);
			}
		};
		ClickableSpan clickablePrivacyPolicy = new ClickableSpan() {
			@Override
			public void onClick(View textView) {
				Intent intent = new Intent(ActivityRegister.this, ActivityAgree.class);
				intent.putExtra("agreement_policy", false);
				startActivity(intent);
			}
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setUnderlineText(false);
			}
		};
		agreeText.setSpan(clickableUserAgreement, indexAgreeUserAgreement, indexAgreeUserAgreement + strAgreeUserAgreement.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		agreeText.setSpan(clickablePrivacyPolicy, indexAgreePrivacyPolicy, indexAgreePrivacyPolicy + strAgreePrivacyPolicy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		tvAgree.setText(agreeText);
		tvAgree.setMovementMethod(LinkMovementMethod.getInstance());
		tvAgree.setHighlightColor(Color.TRANSPARENT);

		tvRequiredAuthCode.setVisibility(View.INVISIBLE);
		tvRequiredPhone.setVisibility(View.INVISIBLE);
		tvRequiredPwd.setVisibility(View.INVISIBLE);
		rvRequiredConfirmPwd.setVisibility(View.INVISIBLE);
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
			btnRegister.setEnabled(!(edtPhone.getText().toString().isEmpty() &&
					edtPwd.getText().toString().isEmpty() &&
					edtAcquire.getText().toString().isEmpty() &&
					edtConfirmPwd.getText().toString().isEmpty()) &&
					ivCheckAgree.isSelected());
		}
	};

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		edtPhone.addTextChangedListener(textWatcher);
		edtAcquire.addTextChangedListener(textWatcher);
		edtPwd.addTextChangedListener(textWatcher);
		edtConfirmPwd.addTextChangedListener(textWatcher);

		ivCheckAgree.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ivCheckAgree.setSelected(!ivCheckAgree.isSelected());
				btnRegister.setEnabled(!(edtPhone.getText().toString().isEmpty() &&
						edtPwd.getText().toString().isEmpty() &&
						edtAcquire.getText().toString().isEmpty() &&
						edtConfirmPwd.getText().toString().isEmpty()) &&
						ivCheckAgree.isSelected());
			}
		});

		btnAcquire.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvRequiredAuthCode.setVisibility(View.INVISIBLE);
				tvRequiredPhone.setVisibility(View.INVISIBLE);
				tvRequiredPwd.setVisibility(View.INVISIBLE);
				rvRequiredConfirmPwd.setVisibility(View.INVISIBLE);

				// Check Value
				m_sPhone = edtPhone.getText().toString();
				if (m_sPhone.isEmpty()) {
					String sMsg = getString(R.string.str_input_verify_code);
					Util.ShowDialogError(ActivityRegister.this, sMsg);
					tvRequiredPhone.setVisibility(View.VISIBLE);
					edtPhone.requestFocus();
					return;
				}
				if (m_sPhone.length() != 11) {
					String sMsg = getString(R.string.str_phone_number_incorrect);
					Util.ShowDialogError(ActivityRegister.this, sMsg);
					edtPhone.requestFocus();
					return;
				}

				TryGetCode();
			}
		});

		btnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvRequiredAuthCode.setVisibility(View.INVISIBLE);
				tvRequiredPhone.setVisibility(View.INVISIBLE);
				tvRequiredPwd.setVisibility(View.INVISIBLE);
				rvRequiredConfirmPwd.setVisibility(View.INVISIBLE);

				// Check Value
				m_sPhone = edtPhone.getText().toString();
				if (m_sPhone.isEmpty()) {
					tvRequiredPhone.setVisibility(View.VISIBLE);
					edtPhone.requestFocus();
					return;
				}
				m_sAuthCode = edtAcquire.getText().toString();
				if (m_sAuthCode.isEmpty()) {
					tvRequiredAuthCode.setVisibility(View.VISIBLE);
					edtAcquire.requestFocus();
					return;
				}
				if (m_sPhone.length() != 11) {
					String sMsg = getString(R.string.str_phone_number_incorrect);
					Util.ShowDialogError(ActivityRegister.this, sMsg);
					edtPhone.requestFocus();
					return;
				}
				m_sPswd = edtPwd.getText().toString();
				if (m_sPswd.isEmpty()) {
					tvRequiredPwd.setVisibility(View.VISIBLE);
					edtPwd.requestFocus();
					return;
				}
				if (m_sPswd.length() < 6 || m_sPswd.length() > 20) {
					String sMsg = getString(R.string.str_password_invalid);
					Util.ShowDialogError(ActivityRegister.this, sMsg);
					return;
				}
				if (edtConfirmPwd.getText().toString().isEmpty()) {
					rvRequiredConfirmPwd.setVisibility(View.VISIBLE);
					edtConfirmPwd.requestFocus();
					return;
				}
				if (!edtConfirmPwd.getText().toString().equals(m_sPswd)) {
					Util.ShowDialogError(R.string.str_incorrect_confirm_password);
					edtConfirmPwd.requestFocus();
					return;
				}


				if (ivCheckAgree.isSelected()) {
					TryRegister();
				}
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
		btnAcquire.setEnabled(false);
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
				btnAcquire.setText(sMsg);
			}
			@Override
			public void onFinish() {
				setAcpuireBtn();
//				String sMsg = getString(R.string.str_auth_code_not_available);
//				Util.ShowDialogError(ActivityRegister.this, sMsg);
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
						Util.ShowDialogError(ActivityRegister.this, sMsg, new Util.ResultProcess() {
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
					Util.ShowDialogError(ActivityRegister.this, getString(R.string.str_auth_code_not_available));
					return;
				}
			}

			@Override
			public void onError(Object error) {
				setAcpuireBtn();
				Util.ShowDialogError(ActivityRegister.this, getString(R.string.str_auth_code_not_available));
			}
		}, TAG);
	}

	private void setAcpuireBtn() {
		btnAcquire.setText(R.string.str_acquire_code);
		btnAcquire.setEnabled(true);
		if (m_timer != null) {
			m_timer.cancel();
			m_timer = null;
		}
	}

	private void TryRegister() {
		m_dlgProgress.show();
		if (m_timer != null) {
			m_timer.cancel();
			m_timer = null;
		}

		Global.JPUSH_ID = JPushInterface.getRegistrationID(getApplicationContext());

		HttpAPI.register(m_sPhone, m_sAuthCode, m_sPswd, Global.JPUSH_ID, new VolleyCallback() {
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
										edtPhone.requestFocus();
										break;
									case HttpAPIConst.RespCode.PHONE_REGISTERED:
										edtPhone.setText("");
										edtAcquire.setText("");
										edtPhone.requestFocus();
										break;
									case HttpAPIConst.RespCode.PASSWORD_INVAILD:
										edtPwd.requestFocus();
										break;
									case HttpAPIConst.RespCode.VALIDATE_CODE_BLANK:
									case HttpAPIConst.RespCode.VALIDATE_CODE_FAIL:
									case HttpAPIConst.RespCode.VALIDATE_CODE_EXPIRED:
										edtAcquire.setText("");
										edtAcquire.requestFocus();
										break;
									default:
								}
							}
						});
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					String strToken = dataObject.getString("token");

					Prefs.Instance().setUserToken(strToken);
					Prefs.Instance().setUserPhone(m_sPhone);
					Prefs.Instance().setUserPswd(m_sPswd);
					Prefs.Instance().commit();
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							ActivityRegister.this,
							getResources().getString(R.string.str_registration_failed)
					);
					return;
				}

				Util.showToastMessage(ActivityRegister.this, R.string.str_registration_success);
				StartLogin();
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(
						ActivityRegister.this,
						getResources().getString(R.string.str_registration_failed)
				);
			}
		}, TAG);
	}

	private void StartLogin() {
		Intent intent = new Intent(ActivityRegister.this, ActivityLogin.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}
}
