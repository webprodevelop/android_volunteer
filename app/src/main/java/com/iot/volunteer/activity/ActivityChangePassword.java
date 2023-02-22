//@formatter:off
package com.iot.volunteer.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.App;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.fragment.FragmentChangePassword;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityChangePassword extends ActivityBase {
	private final String TAG = "ActivityChangePassword";
	private ImageView ivBack;
	private TextView tvTitle;
	private TextView tvRequiredOriginPassword;
	private TextView tvRequiredNewPassword;
	private TextView tvRequiredConfirmPassword;
	private TextView tvRequiredAcquireCode;
	private EditText edtOriginPassword;
	private EditText edtNewPassword;
	private EditText edtConfirmPassword;
	private EditText edtAcquireCode;
	private Button btnAcquire;
	private TextView btnConfirm;

	private boolean bIsBank = false;

	private CountDownTimer m_timer;
	private int m_iCount;

	static FragmentChangePassword fragment = null;

	public static FragmentChangePassword getInstance() {
		if (fragment == null) {
			fragment = new FragmentChangePassword();
		}
		return fragment;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_change_password);

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

		edtOriginPassword = findViewById(R.id.edtOriginPassword);
		tvRequiredOriginPassword = findViewById(R.id.tvRequiredOriginPassword);
		edtOriginPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvRequiredOriginPassword.setVisibility(View.GONE);
			}
		});

		edtNewPassword = findViewById(R.id.edtNewPassword);
		tvRequiredNewPassword = findViewById(R.id.tvRequiredNewPassword);
		edtNewPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvRequiredNewPassword.setVisibility(View.GONE);
			}
		});

		edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
		tvRequiredConfirmPassword = findViewById(R.id.tvRequiredConfirmPassword);
		edtConfirmPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvRequiredConfirmPassword.setVisibility(View.GONE);
			}
		});

		edtAcquireCode = findViewById(R.id.edtAcquireCode);
		tvRequiredAcquireCode = findViewById(R.id.tvRequiredAcquireCode);
		edtAcquireCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvRequiredAcquireCode.setVisibility(View.GONE);
			}
		});

		btnAcquire = findViewById(R.id.btnAcquire);
		btnConfirm = findViewById(R.id.btnConfirm);

		tvRequiredOriginPassword.setVisibility(View.GONE);
		tvRequiredNewPassword.setVisibility(View.GONE);
		tvRequiredConfirmPassword.setVisibility(View.GONE);
		tvRequiredAcquireCode.setVisibility(View.GONE);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				activityMain.popChildFragment();
				finish();
			}
		});
		btnAcquire.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				TryGetCode();
			}
		});
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onConfirm();
			}
		});
	}

	@Override
	public void onDestroy() {
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

	private void TryGetCode() {
		startAcquireCountDown();

		HttpAPI.getVerifyCode(Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityChangePassword.this, sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								setAcpuireBtn();
							}
						});

						return;
					}
					//Global.CODE = jsonObject.getString("verifyCode");
				} catch (JSONException e) {
					setAcpuireBtn();
					Util.ShowDialogError(ActivityChangePassword.this, getString(R.string.str_auth_code_not_available));
					return;
				}
			}

			@Override
			public void onError(Object error) {
				setAcpuireBtn();
				Util.ShowDialogError(ActivityChangePassword.this, getString(R.string.str_auth_code_not_available));
			}
		}, TAG);
	}

	public void setIsBank(boolean isBank) {
		this.bIsBank = isBank;
	}

	private void onConfirm() {
		if (edtOriginPassword.getText().toString().isEmpty()) {
			tvRequiredOriginPassword.setVisibility(View.VISIBLE);
			edtOriginPassword.requestFocus();
			return;
		} else if (edtNewPassword.getText().toString().isEmpty()) {
			tvRequiredNewPassword.setVisibility(View.VISIBLE);
			edtNewPassword.requestFocus();
			return;
		} else if (edtNewPassword.length() < 6 || edtNewPassword.length() > 20) {
			Util.ShowDialogError(R.string.str_password_invalid);
			edtNewPassword.requestFocus();
			return;
		} else if (edtConfirmPassword.getText().toString().isEmpty()) {
			tvRequiredConfirmPassword.setVisibility(View.VISIBLE);
			edtConfirmPassword.requestFocus();
			return;
		} else if (!edtNewPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
			Util.ShowDialogError(R.string.str_incorrect_confirm_password);
			edtConfirmPassword.requestFocus();
			return;
		} else if (edtAcquireCode.getText().toString().isEmpty()) {
			tvRequiredAcquireCode.setVisibility(View.VISIBLE);
			edtAcquireCode.requestFocus();
			return;
		}

		if (bIsBank) {
			modifyBankPassword();
		} else {
			resetPassword();
		}
	}

	private void modifyBankPassword() {
		m_dlgProgress.show();

		final String strNewPassword = edtNewPassword.getText().toString();
		String strOldPassword = edtOriginPassword.getText().toString();
		String strVerifyCode = edtAcquireCode.getText().toString();

		HttpAPI.modifyBankPassword(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), strVerifyCode, strOldPassword, strNewPassword, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityChangePassword.this, sMsg);
						return;
					}

					Util.showToastMessage(ActivityChangePassword.this, R.string.str_change_password_success);
					finish();
//					activityMain.popChildFragment();
				} catch (JSONException e) {
					Util.ShowDialogError(
							ActivityChangePassword.this,
							getResources().getString(R.string.str_change_password_failed)
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(
						ActivityChangePassword.this,
						getResources().getString(R.string.str_change_password_failed)
				);
			}
		}, TAG);
	}

	private void resetPassword() {
		m_dlgProgress.show();

		final String strNewPassword = edtNewPassword.getText().toString();
		String strOldPassword = edtOriginPassword.getText().toString();
		String strVerifyCode = edtAcquireCode.getText().toString();

		HttpAPI.resetPassword(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), strVerifyCode, strOldPassword, strNewPassword, new VolleyCallback() {
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
									case HttpAPIConst.RespCode.PASSWORD_OLD_FAIL:
										edtOriginPassword.requestFocus();
										break;
									case HttpAPIConst.RespCode.PASSWORD_BLANK:
									case HttpAPIConst.RespCode.PASSWORD_INVAILD:
										edtNewPassword.requestFocus();
										break;
									case HttpAPIConst.RespCode.VALIDATE_CODE_BLANK:
									case HttpAPIConst.RespCode.VALIDATE_CODE_FAIL:
									case HttpAPIConst.RespCode.VALIDATE_CODE_EXPIRED:
										edtAcquireCode.setText("");
										edtAcquireCode.requestFocus();
										break;
									default:
								}
							}
						});
						return;
					}

					Prefs.Instance().setUserPswd(strNewPassword);
					Prefs.Instance().commit();

					Util.showToastMessage(ActivityChangePassword.this, R.string.str_change_password_success);
					finish();
//					activityMain.popChildFragment();
				} catch (JSONException e) {
					Util.ShowDialogError(
							ActivityChangePassword.this,
							getResources().getString(R.string.str_change_password_failed)
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(
						ActivityChangePassword.this,
						getResources().getString(R.string.str_change_password_failed)
				);
			}
		}, TAG);
	}
}
