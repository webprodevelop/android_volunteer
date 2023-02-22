//@formatter:off
package com.iot.volunteer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentWeixinAccount extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private EditText						edtWeixin;
	private EditText						edtWeixinAccount;
	private TextView						tvRefresh;
	private TextView						tvSave;

	static FragmentWeixinAccount fragment = null;

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

	private IWXAPI							wxapi;

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

	public static FragmentWeixinAccount getInstance() {
		if (fragment == null) {
			fragment = new FragmentWeixinAccount();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_weixin_account, container, false);

		regToWx();
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
		edtWeixin = layout.findViewById(R.id.edtWeixin);
		edtWeixinAccount = layout.findViewById(R.id.edtWeixinAccount);
		tvRefresh = layout.findViewById(R.id.tvRefresh);
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
		tvRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final SendAuth.Req req = new SendAuth.Req();
				req.scope = "snsapi_userinfo";
				req.state = "wechat_access_token";

				if (wxapi.sendReq(req) == false) {
					Util.ShowDialogError(
							getContext(),
							getResources().getString(R.string.str_page_loading_failed)
					);
					return;
				}
			}
		});
		tvSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				m_dlgProgress.show();
				HttpAPI.registerPayAccount(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), "2", sOpenId, sNickName, new VolleyCallback() {
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
							String strWeixinName = dataObject.getString("weixin_account_name");
							String strWeixinId = dataObject.getString("weixin_account_id");

							Prefs.Instance().setWeixinName(strWeixinName);
							Prefs.Instance().setWeixinId(strWeixinId);
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
		String weixinName = Prefs.Instance().getWeixinName();
		if (weixinName != null && !weixinName.isEmpty()) {
			edtWeixin.setText(Prefs.Instance().getWeixinName());
		}
		String weixinId = Prefs.Instance().getWeixinId();
		if (weixinId != null && weixinId.length() > 8) {
			edtWeixinAccount.setText(Prefs.Instance().getWeixinId());
		}
	}

	private void tryGetWechatAccessToken() {
		HttpAPI.getWechatAccessToken(WX_APP_ID, SECRET, sCode, "authorization_code", new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONObject dataObject = jsonObject.getJSONObject("data");
					sAccessToken = dataObject.getString("access_token");
					sOpenId = dataObject.getString("openid");
					tryGetWechatUserInfo();
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
				Util.ShowDialogError(
						getContext(),
						getResources().getString(R.string.str_page_loading_failed)
				);
			}
		}, TAG);
	}

	private void tryGetWechatUserInfo() {
		HttpAPI.getWechatUserInfo(sAccessToken, sOpenId, sLang, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONObject dataObject = jsonObject.getJSONObject("data");
					sNickName = dataObject.getString("sNickName");

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							edtWeixin.setText(sNickName);
							edtWeixinAccount.setText(sOpenId);
						}
					});
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
				Util.ShowDialogError(
						getContext(),
						getResources().getString(R.string.str_page_loading_failed)
				);
			}
		}, TAG);
	}
}