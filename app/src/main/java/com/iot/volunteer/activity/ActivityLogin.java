//@formatter:off
package com.iot.volunteer.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.App;
import com.iot.volunteer.BuildConfig;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.Global;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.util.Util;
import com.iot.volunteer.http.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class ActivityLogin extends ActivityBase {
	private final String TAG = "ActivityLogin";

	private ImageView	ivLogo;
	private EditText	edtPhone;
	private TextView	tvRequirePhone;
	private EditText	edtPwd;
	private TextView	tvRequiredPwd;
	private Button		btnLogin;
	private TextView	tvRegister;
	private TextView	tvForgot;
	private CheckBox	cbSaveUser;
	private CheckBox	cbSavePwd;

	private String		m_sToken;
	private String		m_sUser;
	private String		m_sPhone = "";
	private String		m_sPswd = "";

	private boolean		m_bIsClickedLogin;
	private boolean		m_bLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//getWindow().setStatusBarColor(Color.WHITE);
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();

		// Init Value
		m_bIsClickedLogin = false;

		m_bLogout = getIntent().getBooleanExtra("log_out", false);

		if (Prefs.Instance().isSaveUser()) {
			m_sPhone = Prefs.Instance().getUserPhone();
			edtPhone.setText(m_sPhone);
		}
		if (Prefs.Instance().isSavePassword()) {
			m_sPswd = Prefs.Instance().getUserPswd();
			edtPwd.setText(m_sPswd);
		}

		if(!m_bLogout) {
			getAppInfo();
		}
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivLogo		= findViewById(R.id.ivLogo);
		edtPhone		= findViewById(R.id.edtPhone);
		edtPwd		= findViewById(R.id.edtPwd);
		tvRequirePhone	= findViewById(R.id.tvRequirePhone);
		tvRequiredPwd	= findViewById(R.id.tvRequiredPwd);
		btnLogin		= findViewById(R.id.btnLogin);
		tvRegister	= findViewById(R.id.tvRegister);
		tvForgot		= findViewById(R.id.tvForgot);
		cbSaveUser		= findViewById(R.id.cbSaveUser);
		cbSavePwd		= findViewById(R.id.cbSavePwd);

		// Hide tvRequirePhone, tvRequiredPwd
		tvRequirePhone.setVisibility(View.INVISIBLE);
		tvRequiredPwd.setVisibility(View.INVISIBLE);

		cbSaveUser.setChecked(Prefs.Instance().isSaveUser());
		cbSavePwd.setChecked(Prefs.Instance().isSavePassword());
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
			btnLogin.setEnabled(!(edtPhone.getText().toString().isEmpty() && edtPwd.getText().toString().isEmpty()));
		}
	};

	@Override
	protected void setEventListener() {
		edtPhone.addTextChangedListener(textWatcher);
		edtPwd.addTextChangedListener(textWatcher);

		// btnLogin
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvRequirePhone.setVisibility(View.INVISIBLE);
				tvRequiredPwd.setVisibility(View.INVISIBLE);

				// Check Value
				m_sPhone = edtPhone.getText().toString();
				if (m_sPhone.isEmpty()) {
					tvRequirePhone.setVisibility(View.VISIBLE);
					edtPhone.requestFocus();
					return;
				}
				if (m_sPhone.length() != 11) {
					Util.ShowDialogError(R.string.str_phone_number_incorrect, new Util.ResultProcess() {
						@Override
						public void process() {
							edtPhone.requestFocus();
						}
					});
					return;
				}

				m_sPswd = edtPwd.getText().toString();
				if (m_sPswd.isEmpty()) {
					tvRequiredPwd.setVisibility(View.VISIBLE);
					edtPwd.requestFocus();
					return;
				}
				if (m_sPswd.length() < 6 || m_sPswd.length() > 20) {
					Util.ShowDialogError(R.string.str_password_invalid, new Util.ResultProcess() {
						@Override
						public void process() {
							edtPwd.requestFocus();
						}
					});
					return;
				}

				if (m_bIsClickedLogin)
					return;
				m_bIsClickedLogin = true;
				TryLogin();
			}
		});

		// tvRegister
		tvRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StartRegister();
			}
		});

		// tvForgot
		tvForgot.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v) {
				StartForgot();
			}
		});

		// Set same Width and Height of the ivLogo
		ivLogo.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						ViewGroup.LayoutParams	lytparams = ivLogo.getLayoutParams();
						int iWidth = ivLogo.getWidth();
						lytparams.height = iWidth;
						ivLogo.setLayoutParams(lytparams);
					}
				}
		);

		cbSaveUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				Prefs.Instance().setSaveUser(b);
			}
		});

		cbSavePwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				Prefs.Instance().setSavePassword(b);
			}
		});
	}

	private void TryLogin() {
		Global.JPUSH_ID = JPushInterface.getRegistrationID(getApplicationContext());
		m_dlgProgress.show();

		HttpAPI.login(m_sPhone, m_sPswd, Global.JPUSH_ID, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_bIsClickedLogin = false;
				m_dlgProgress.dismiss();
				Prefs.Instance().setSaveUser(cbSaveUser.isChecked());
				Prefs.Instance().setSavePassword(cbSavePwd.isChecked());

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
										edtPhone.requestFocus();
										break;
									case HttpAPIConst.RespCode.ACCOUNT_NOT_EXIST:
										edtPhone.setText("");
										edtPhone.requestFocus();
										break;
									case HttpAPIConst.RespCode.PASSWORD_INVAILD:
									case HttpAPIConst.RespCode.PASSWORD_FAIL:
										edtPwd.requestFocus();
										break;
									default:
								}
							}
						});
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					String strBirthday = dataObject.optString("birthday");
					String strIDBack = dataObject.optString("id_card_back");
					String strAddress = dataObject.optString("address");
					String strCity = dataObject.optString("city");
					boolean bSex = dataObject.optInt("sex") == 1 ? true : false;
					String strIDFront = dataObject.optString("id_card_front");
					m_sPhone = dataObject.optString("mobile");
					String strCertificate = dataObject.optString("certificate");
					String strPhoto = dataObject.optString("picture");
					m_sToken = dataObject.optString("token");
					String strIDNum = dataObject.optString("id_card_num");
					String strCertPic = dataObject.optString("certificate_pic");
					String strProvince = dataObject.optString("province");
					String strDistrict = dataObject.optString("district");
					m_sUser = dataObject.optString("name");
					String strCompany = dataObject.optString("company");
					String strResidence = dataObject.optString("residence");
					String strJob = dataObject.optString("job");
					int iPoint = (int) dataObject.optDouble("point_level");
					int iMerit = (int) dataObject.optDouble("point");
					float fCash = (float) dataObject.optDouble("cash");
					String strBankName = dataObject.optString("bank_account_name");
					String strBankId = dataObject.optString("bank_account_id");
//					String strBankPassword = dataObject.getString("bank_account_pwd");
					String strAlipayName = dataObject.optString("alipay_account_name");
					String strAlipayId = dataObject.optString("alipay_account_id");
					String strWeixinName = dataObject.optString("weixin_account_name");
					String strWeixinId = dataObject.optString("weixin_account_id");
					String strBalance = dataObject.optString("balance");
					String strPointLevel = dataObject.optString("point_level");
					String strStatus = dataObject.optString("status");
					String strLat = dataObject.optString("lat");
					String strLon = dataObject.optString("lon");

					if (strStatus.equals("DISABLED")) {
						Prefs.Instance().setTaskStatus(0);
					} else if (strStatus.equals("READY")) {
						Prefs.Instance().setTaskStatus(1);
					} else if (strStatus.equals("WORKING")) {
						Prefs.Instance().setTaskStatus(2);
					}
					int taskId = dataObject.getInt("task_id");
					Prefs.Instance().setTaskId(String.valueOf(taskId));
					Prefs.Instance().setBankName(strBankName);
					Prefs.Instance().setBankId(strBankId);
//					Prefs.Instance().setBankPassword(strBankPassword);
					Prefs.Instance().setAlipayName(strAlipayName);
					Prefs.Instance().setAlipayId(strAlipayId);
					Prefs.Instance().setWeixinName(strWeixinName);
					Prefs.Instance().setWeixinId(strWeixinId);
					Prefs.Instance().setBalance(strBalance);
					Prefs.Instance().setPointLevel(strPointLevel);
					Prefs.Instance().setMerit(iMerit);
					Prefs.Instance().setCash(fCash);
					Prefs.Instance().setPoint(iPoint);
					Prefs.Instance().setBirthday(strBirthday);
					Prefs.Instance().setIDCardBack(strIDBack);
					Prefs.Instance().setAddress(strAddress);
					Prefs.Instance().setCity(strCity);
					Prefs.Instance().setSex(bSex);
					Prefs.Instance().setIDCardFront(strIDFront);
					Prefs.Instance().setUserPhone(m_sPhone);
					Prefs.Instance().setCertificate(strCertificate);
					Prefs.Instance().setUserPhoto(strPhoto);
					Prefs.Instance().setUserToken(m_sToken);
					Prefs.Instance().setIDCardNum(strIDNum);
					Prefs.Instance().setCertPic(strCertPic);
					Prefs.Instance().setProvince(strProvince);
					Prefs.Instance().setDistrict(strDistrict);
					Prefs.Instance().setUserName(m_sUser);
					Prefs.Instance().setUserPswd(m_sPswd);
					Prefs.Instance().setCompany(strCompany);
					Prefs.Instance().setResidence(strResidence);
					Prefs.Instance().setJob(strJob);
					try {
						Global.gLatValue = Double.parseDouble(strLat);
						Global.gLonValue = Double.parseDouble(strLon);
					} catch (Exception e) {

					}


					Prefs.Instance().commit();

					Util.showToastMessage(ActivityLogin.this, R.string.str_login_success);

					StartMain();
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							ActivityLogin.this,
							getResources().getString(R.string.str_login_failed)
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_bIsClickedLogin = false;
				m_dlgProgress.dismiss();
				Util.ShowDialogError(
						ActivityLogin.this,
						getResources().getString(R.string.str_login_failed)
				);
			}
		}, TAG);
	}

	private void StartMain() {
		Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
		// When Intent has JPush Notification Data
		if (getIntent() != null && getIntent().getExtras() != null)
			intent.putExtras(getIntent().getExtras());
		startActivity(intent);

		finish();
	}

	private void StartRegister() {
		Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
		startActivity(intent);
	}

	private void StartForgot() {
		Intent intent = new Intent(ActivityLogin.this, ActivityForgot.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}

	private void checkPolicyAndAgreement() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View confirmView = layoutInflater.inflate(R.layout.alert_delete_alarm, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(this).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
		TextView txtAgree	= confirmView.findViewById(R.id.ID_TXT_HEADER);

		String strAgreeText = getResources().getString(R.string.str_agree_policy_text);
		String strAgreeUserAgreement = getResources().getString(R.string.str_agree_user_agreement);
		String strAgreePrivacyPolicy = getResources().getString(R.string.str_agree_privacy_policy);
		int indexAgreeUserAgreement = strAgreeText.indexOf(strAgreeUserAgreement);
		if (indexAgreeUserAgreement == -1) {indexAgreeUserAgreement = 0;}
		int indexAgreePrivacyPolicy = strAgreeText.indexOf(strAgreePrivacyPolicy);
		if (indexAgreePrivacyPolicy == -1) {indexAgreePrivacyPolicy = 0;}
		SpannableString agreeText = new SpannableString(strAgreeText);
		ClickableSpan clickableUserAgreement = new ClickableSpan() {
			@Override
			public void onClick(View textView) {
				Intent intent = new Intent(ActivityLogin.this, ActivityAgree.class);
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
				Intent intent = new Intent(ActivityLogin.this, ActivityAgree.class);
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

		txtAgree.setText(agreeText);
		txtAgree.setMovementMethod(LinkMovementMethod.getInstance());
		txtAgree.setHighlightColor(Color.TRANSPARENT);

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmDlg.dismiss();

				finish();
			}
		});

		btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmDlg.dismiss();

				Prefs.Instance().setAgreed();
				Prefs.Instance().commit();
			}
		});

		btnCancel.setText(R.string.str_nonagree);
		btnConfirm.setText(R.string.str_agree);

		confirmDlg.setCancelable(false);
		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.show();
	}

	private void getAppInfo() {
		m_dlgProgress.show();

		HttpAPI.getAppInfo(new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityLogin.this, sMsg);
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					String strAgreement = dataObject.getString("agreement");
					String strPolicy = dataObject.getString("policy");

					Prefs.Instance().setAgreement(strAgreement);
					Prefs.Instance().setPolicy(strPolicy);
					Prefs.Instance().commit();

					JSONObject verObject = dataObject.getJSONObject("appVersion");
					Util.storeAppVersion = verObject.optString("app_ver_android");
					Util.storeAppURL = verObject.optString("store_url_android");

					m_dlgProgress.dismiss();

					if (!Prefs.Instance().getAgreed()) {
						checkPolicyAndAgreement();
						return;
					}

					if (BuildConfig.VERSION_NAME.compareTo(Util.storeAppVersion) < 0) {
						LayoutInflater layoutInflater = LayoutInflater.from(ActivityLogin.this);
						View confirmView = layoutInflater.inflate(R.layout.alert_new_version, null);

						final AlertDialog confirmDlg = new AlertDialog.Builder(ActivityLogin.this).create();
						TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

						btnConfirm.setOnClickListener(new OnClickListener() {
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

					if (!m_sPhone.isEmpty() && !m_sPswd.isEmpty()) {
						TryLogin();
					}
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							ActivityLogin.this,
							getResources().getString(R.string.str_page_loading_failed)
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(
						ActivityLogin.this,
						getResources().getString(R.string.str_api_failed)
				);
			}
		}, TAG);
	}
}
