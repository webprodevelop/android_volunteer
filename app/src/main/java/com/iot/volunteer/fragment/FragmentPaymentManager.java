//@formatter:off
package com.iot.volunteer.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.activity.ActivityForgot;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.util.Util;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentPaymentManager extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private TextView 						txtWechatAccount;
	private TextView						txtAlipayAccount;
	private TextView						btnWechatBinding;
	private TextView						btnAilpayBinding;


	private TextView tvConfirm;
	private TextView tvCancel;
	private EditText edtVerifyCode;
	private Button	 btnAcquire;
	private TextView tvRequireAcquire;

	private TextView tvSave;
	private TextView tvSaveCancel;
	private EditText edtAlipay;
	private EditText edtAlipayAccount;
	private TextView tvRequiredAccount;
	private TextView tvRequiredID;

	static FragmentPaymentManager fragment = null;
	private boolean wechat_unbind = true;
	private boolean alipay_unbind = true;
	private CountDownTimer		m_timer;
	private int					m_iCount;
//	private String				m_sVerifyCode;

	public static final String WX_APP_ID = "wx7a58ef5cb07ff9f8";
	public static final String SECRET = "liaoningdandongshoumengou2020111";
	public String sCode = "";
	public String sLang = "";
	public String sAccessToken = "";
	public String sOpenId = "";
	public String sNickName = "";
	public static boolean finishedWXPay;
	public static int WXPayResult;
	public static String WXPayResultStr;

	private IWXAPI wxapi;

	public static FragmentPaymentManager getInstance() {
		if (fragment == null) {
			fragment = new FragmentPaymentManager();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_payment_manager, container, false);

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
		txtWechatAccount = layout.findViewById(R.id.txtWechatAccount);
		txtAlipayAccount = layout.findViewById(R.id.txtAlipayAccount);
		tvSave = layout.findViewById(R.id.tvSave);
		btnWechatBinding = layout.findViewById(R.id.btnWechatBinding);
		btnAilpayBinding = layout.findViewById(R.id.btnAilpayBinding);

		alipay_unbind = true;
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});
		btnWechatBinding.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View viwe) {
				if (wechat_unbind == true) {

					regToWx();
//					btnWechatBinding.setText(R.string.str_unbind);
//					wechat_unbind = false;
				} else {
					btnWechatBinding.setText(R.string.str_bind);
					wechat_unbind = true;
				}
			}
		});
		btnAilpayBinding.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View viwe) {
				if (alipay_unbind == true) {
					showAlipayAccount();
				} else {
					showStatusAlert();

				}
			}
		});
	}

	private void showStatusAlert() {
		final AlertDialog alertDlg = new AlertDialog.Builder(getContext()).create();
		alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View alertView = layoutInflater.inflate(R.layout.dialog_pay_unbind, null);

		tvConfirm = alertView.findViewById(R.id.tvConfirm);
		tvCancel = alertView.findViewById(R.id.tvCancel);
		edtVerifyCode = alertView.findViewById(R.id.edtVerifyCode);
		btnAcquire = alertView.findViewById(R.id.btnAcquire);
		tvRequireAcquire = alertView.findViewById(R.id.tvRequireAcquire);
		tvRequireAcquire.setVisibility(View.GONE);
		edtVerifyCode.setOnClickListener(v -> {
			tvRequireAcquire.setVisibility(View.GONE);
		});

		alertDlg.setView(alertView);

		btnAcquire.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TryGetCode();
			}
		});

		tvConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (edtVerifyCode.getText().toString().isEmpty()) {
					tvRequireAcquire.setVisibility(View.VISIBLE);
					edtVerifyCode.requestFocus();
					return;
				}
				tryRemovepayAccount(alertDlg);
				if (m_timer != null) {
					m_timer.cancel();
					m_timer = null;
				}
//				alertDlg.dismiss();
			}
		});

		tvCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (m_timer != null) {
					m_timer.cancel();
				}
				alertDlg.dismiss();
			}
		});

		alertDlg.show();
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
						Util.ShowDialogError(getActivity(), sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								setAcpuireBtn();
							}
						});
						return;
					}
				}
				catch (JSONException e) {
					setAcpuireBtn();
					Util.ShowDialogError(getActivity(), getString(R.string.str_auth_code_not_available));
					return;
				}
			}

			@Override
			public void onError(Object error) {
				setAcpuireBtn();
				Util.ShowDialogError(getActivity(), getString(R.string.str_auth_code_not_available));
			}
		}, TAG);
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

	private void showAlipayAccount() {
		final AlertDialog alertDlg = new AlertDialog.Builder(getContext()).create();
		alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View alertView = layoutInflater.inflate(R.layout.dialog_alipay_account, null);

		tvSave 				= alertView.findViewById(R.id.tvSave);
		tvSaveCancel 		= alertView.findViewById(R.id.tvSaveCancel);
		edtAlipay 			= alertView.findViewById(R.id.edtAlipay);
		edtAlipayAccount 	= alertView.findViewById(R.id.edtAlipayAccount);
		tvRequiredID		= alertView.findViewById(R.id.tvRequiredID);
		tvRequiredAccount	= alertView.findViewById(R.id.tvRequiredAccount);
		tvRequiredID.setVisibility(View.INVISIBLE);
		tvRequiredAccount.setVisibility(View.INVISIBLE);
		alertDlg.setView(alertView);
		tvSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (edtAlipay.getText().toString().isEmpty() ) {
					tvRequiredAccount.setVisibility(View.VISIBLE);
					edtAlipay.requestFocus();
					return;
				}
				if (edtAlipayAccount.getText().toString().isEmpty()) {
					tvRequiredID.setVisibility(View.VISIBLE);
					edtAlipayAccount.requestFocus();
					return;
				}
				tryGetAlipayAccount();
				alertDlg.dismiss();
			}
		});

		tvSaveCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				alertDlg.dismiss();
			}
		});
		alertDlg.show();
	}

	private void tryGetAlipayAccount() {
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
					btnAilpayBinding.setText(R.string.str_unbind);
					txtAlipayAccount.setText(strAlipayName);
					alipay_unbind = false;
					Util.showToastMessage(getContext(), R.string.str_operation_success);
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

	private void tryRemovepayAccount(AlertDialog alertDlg) {
		m_dlgProgress.show();
		HttpAPI.removePayAccount(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), "1", edtVerifyCode.getText().toString(), new VolleyCallback() {
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
									case HttpAPIConst.RespCode.VALIDATE_CODE_BLANK:
									case HttpAPIConst.RespCode.VALIDATE_CODE_FAIL:
									case HttpAPIConst.RespCode.VALIDATE_CODE_EXPIRED:
										edtVerifyCode.setText("");
										edtVerifyCode.requestFocus();
										break;
									default:
										alertDlg.dismiss();
								}
							}
						});
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					String strAlipayName = dataObject.getString("alipay_account_name");
					String strAlipayId = dataObject.getString("alipay_account_id");

					Prefs.Instance().setAlipayName(strAlipayName);
					Prefs.Instance().setAlipayId(strAlipayId);
					Prefs.Instance().commit();

					m_dlgProgress.dismiss();
					btnAilpayBinding.setText(R.string.str_bind);
					txtAlipayAccount.setText(strAlipayName);
					alipay_unbind = true;
					Util.showToastMessage(getContext(), R.string.str_operation_success);
					alertDlg.dismiss();
				}
				catch (JSONException e) {
					alertDlg.dismiss();
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
				alertDlg.dismiss();
				Util.ShowDialogError(
						getContext(),
						getResources().getString(R.string.str_page_loading_failed)
				);
			}
		}, TAG);
	}

	private void loadData() {
		String alipayName = Prefs.Instance().getAlipayName();
		String alipayId = Prefs.Instance().getAlipayId();
		if (alipayName != null && !alipayName.isEmpty()) {
			txtAlipayAccount.setText(Prefs.Instance().getAlipayName());
			if (alipayId != null) {// && alipayId.length() > 8) {
				txtAlipayAccount.setText(Prefs.Instance().getAlipayName());
//				txtAlipayAccount.setText(Prefs.Instance().getAlipayId());
				btnAilpayBinding.setText(R.string.str_unbind);
				alipay_unbind = false;
			}
		}


	}

	private void regToWx() {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		wxapi = WXAPIFactory.createWXAPI(getContext(), WX_APP_ID, true);

		// 将应用的appId注册到微信
		wxapi.registerApp(WX_APP_ID);

		//建议动态监听微信启动广播进行注册到微信
		getContext().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// 将该app注册到微信
				wxapi.registerApp(WX_APP_ID);
			}
		}, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
	}
}