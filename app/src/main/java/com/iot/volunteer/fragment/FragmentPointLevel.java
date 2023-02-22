//@formatter:off
package com.iot.volunteer.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.activity.ActivityLogin;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentPointLevel extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private TextView						tvCurrentPointValue;
	private TextView						tvConsumerDetail;
	private TextView						tvCashIn;
	private SeekBar							sbPointLevel;
	private ImageView						ivInfo;
	private TextView						tvLevel1;
	private TextView						tvPointRange1;
	private TextView						tvConvertRatio1;
	private TextView						tvPremiumRatio1;
	private TextView						tvOther1;
	private TextView						tvLevel2;
	private TextView						tvPointRange2;
	private TextView						tvConvertRatio2;
	private TextView						tvPremiumRatio2;
	private TextView						tvOther2;
	private TextView						tvLevel3;
	private TextView						tvPointRange3;
	private TextView						tvConvertRatio3;
	private TextView						tvPremiumRatio3;
	private TextView						tvOther3;
	private TextView						tvLevel4;
	private TextView						tvPointRange4;
	private TextView						tvConvertRatio4;
	private TextView						tvPremiumRatio4;
	private TextView						tvOther4;

	private String							pointRule;
	private boolean 						alipay;
	private int 							iBalance;
	private float 							fCash;
	private int 							iPoint;


	static FragmentPointLevel fragment = null;


	public static FragmentPointLevel getInstance() {
		if (fragment == null) {
			fragment = new FragmentPointLevel();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_point_level, container, false);

		initControls(rootView);
		setEventListener();

//		loadData();
		getPointRule();

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		tvCurrentPointValue = layout.findViewById(R.id.tvCurrentPointValue);
		tvConsumerDetail = layout.findViewById(R.id.tvConsumerDetail);
		tvCashIn = layout.findViewById(R.id.tvCashIn);
		ivInfo = layout.findViewById(R.id.ivInfo);
		sbPointLevel = layout.findViewById(R.id.sbPointLevel);
		sbPointLevel.setEnabled(false);
		tvLevel1 = layout.findViewById(R.id.tvLevel1);
		tvPointRange1 = layout.findViewById(R.id.tvPointRange1);
		tvConvertRatio1 = layout.findViewById(R.id.tvConvertRatio1);
		tvPremiumRatio1 = layout.findViewById(R.id.tvPremiumRatio1);
		tvOther1 = layout.findViewById(R.id.tvOther1);
		tvLevel2 = layout.findViewById(R.id.tvLevel2);
		tvPointRange2 = layout.findViewById(R.id.tvPointRange2);
		tvConvertRatio2 = layout.findViewById(R.id.tvConvertRatio2);
		tvPremiumRatio2 = layout.findViewById(R.id.tvPremiumRatio2);
		tvOther2 = layout.findViewById(R.id.tvOther2);
		tvLevel3 = layout.findViewById(R.id.tvLevel3);
		tvPointRange3 = layout.findViewById(R.id.tvPointRange3);
		tvConvertRatio3 = layout.findViewById(R.id.tvConvertRatio3);
		tvPremiumRatio3 = layout.findViewById(R.id.tvPremiumRatio3);
		tvOther3 = layout.findViewById(R.id.tvOther3);
		tvLevel4 = layout.findViewById(R.id.tvLevel4);
		tvPointRange4 = layout.findViewById(R.id.tvPointRange4);
		tvConvertRatio4 = layout.findViewById(R.id.tvConvertRatio4);
		tvPremiumRatio4 = layout.findViewById(R.id.tvPremiumRatio4);
		tvOther4 = layout.findViewById(R.id.tvOther4);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});

		tvConsumerDetail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.pushChildFragment(FragmentCashHistory.getInstance(), FragmentCashHistory.class.getSimpleName());
				FragmentCashHistory.getInstance().setActivity(activityMain);
			}
		});

		tvCashIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onCashIn();
			}
		});

		ivInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentPointRule fragmentPointRule = new FragmentPointRule();
				activityMain.pushChildFragment(fragmentPointRule, FragmentPointRule.class.getSimpleName());
				fragmentPointRule.setActivity(activityMain);
				fragmentPointRule.setPointRule(pointRule);
			}
		});
	}

	private void loadData() {
		int merit = Prefs.Instance().getMerit();
		float cash = Prefs.Instance().getCash();
		tvCurrentPointValue.setText(String.format(getResources().getString(R.string.str_point_and_cash), merit, cash));

		int point = Prefs.Instance().getPoint();
		if (point > 4) point = 4;
		if (point < 0) point = 0;
		sbPointLevel.setMax(4);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			sbPointLevel.setMin(1);
		}
		sbPointLevel.setProgress(point);

//		getPointRule();
	}

	private void getPointRule() {
		m_dlgProgress.show();

		HttpAPI.getPointRule(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
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

					JSONObject dataObject = jsonObject.getJSONObject("data");
					String strV1ExchangePoint = dataObject.getString("v1_exchange_point");
					String strV2ExchangePoint = dataObject.getString("v2_exchange_point");
					String strV3ExchangePoint = dataObject.getString("v3_exchange_point");
					String strV4ExchangePoint = dataObject.getString("v4_exchange_point");

					String strV1ExchangePrice = dataObject.getString("v1_exchange_price");
					String strV2ExchangePrice = dataObject.getString("v2_exchange_price");
					String strV3ExchangePrice = dataObject.getString("v3_exchange_price");
					String strV4ExchangePrice = dataObject.getString("v4_exchange_price");

					String strV1Description = dataObject.getString("v1_description");
					String strV2Description = dataObject.getString("v2_description");
					String strV3Description = dataObject.getString("v3_description");
					String strV4Description = dataObject.getString("v4_description");

					String strV1Limit = dataObject.getString("v1_limit");
					String strV2Limit = dataObject.getString("v2_limit");
					String strV3Limit = dataObject.getString("v3_limit");
					String strV4Limit = dataObject.getString("v4_limit");

					String strV1Equity = dataObject.getString("v1_equity");
					String strV2Equity = dataObject.getString("v2_equity");
					String strV3Equity = dataObject.getString("v3_equity");
					String strV4Equity = dataObject.getString("v4_equity");

					pointRule = dataObject.getString("point_rule");

					tvPointRange1.setText("0-" + strV1Limit);
					int iminValue = Integer.parseInt(strV1Limit) + 1;
					tvPointRange2.setText(String.valueOf(iminValue) + "-" + strV2Limit);
					iminValue = Integer.parseInt(strV2Limit) + 1;
					tvPointRange3.setText(String.valueOf(iminValue) + "-" + strV3Limit);
					iminValue = Integer.parseInt(strV3Limit) + 1;
					tvPointRange4.setText(String.valueOf(iminValue) + "-" + strV4Limit);

					tvConvertRatio1.setText(strV1ExchangePoint + ":" + strV1ExchangePrice);
					tvConvertRatio2.setText(strV2ExchangePoint + ":" + strV2ExchangePrice);
					tvConvertRatio3.setText(strV3ExchangePoint + ":" + strV3ExchangePrice);
					tvConvertRatio4.setText(strV4ExchangePoint + ":" + strV4ExchangePrice);

					tvPremiumRatio1.setText(strV1Equity);
					tvPremiumRatio2.setText(strV2Equity);
					tvPremiumRatio3.setText(strV3Equity);
					tvPremiumRatio4.setText(strV4Equity);

					tvOther1.setText(strV1Description);
					tvOther2.setText(strV2Description);
					tvOther3.setText(strV3Description);
					tvOther4.setText(strV4Description);

					iBalance = (int) dataObject.getDouble("balance");
					fCash = (float) dataObject.getDouble("cash");
					tvCurrentPointValue.setText(String.format(getResources().getString(R.string.str_point_and_cash), iBalance, fCash));

					iPoint = (int) dataObject.getDouble("point_level");
					if (iPoint > 4) iPoint = 4;
					if (iPoint < 0) iPoint = 0;
					sbPointLevel.setMax(4);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						sbPointLevel.setMin(1);
					}
					sbPointLevel.setProgress(iPoint);
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							getActivity(),
							getResources().getString(R.string.str_api_failed)
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();

				Util.ShowDialogError(
						getActivity(),
						getResources().getString(R.string.str_api_failed)
				);
			}
		}, TAG);
	}

	private void onCashIn() {
		final AlertDialog alertDlg = new AlertDialog.Builder(getActivity()).create();
		alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View alertView = layoutInflater.inflate(R.layout.dialog_cash_in, null);

		final LinearLayout llWeixin = alertView.findViewById(R.id.llWeixin);
		final LinearLayout llZhifubao = alertView.findViewById(R.id.llZhifubao);
		final LinearLayout llBankCard = alertView.findViewById(R.id.llBankCard);
		TextView tvCashDesc = alertView.findViewById(R.id.tvCashDesc);
		TextView tvConfirm = alertView.findViewById(R.id.tvConfirm);
		TextView tvCancel = alertView.findViewById(R.id.tvCancel);
		llWeixin.setSelected(true);

		alertDlg.setView(alertView);

		llWeixin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				llWeixin.setSelected(true);
				llZhifubao.setSelected(false);
				llBankCard.setSelected(false);
				alipay = false;
//				tryRequestTransferPay("2");
			}
		});

		llZhifubao.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				llWeixin.setSelected(false);
				llZhifubao.setSelected(true);
				llBankCard.setSelected(false);
				alipay = true;
//				tryRequestTransferPay("1");
			}
		});

//		llBankCard.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				llWeixin.setSelected(false);
//				llZhifubao.setSelected(false);
//				llBankCard.setSelected(true);
//
//				tryRequestTransferPay("3");
//			}
//		});

		int merit = Prefs.Instance().getMerit();
		float cash = Prefs.Instance().getCash();
		tvCashDesc.setText(String.format(getResources().getString(R.string.str_cash_desc), cash));
		tvConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (alipay == true) {
					tryRequestTransferPay("1");
				} else {
					tryRequestTransferPay("2");
				}
				alertDlg.dismiss();
			}
		});

		tvCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				alertDlg.dismiss();
			}
		});

		alertDlg.show();
	}

	private void tryRequestTransferPay(String payType) {
		m_dlgProgress.show();
		HttpAPI.requestTransferPay(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), payType, String.valueOf(iBalance), new VolleyCallback() {
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

					getPointRule();
					m_dlgProgress.dismiss();
					Util.ShowDialogError(
							getContext(),
							getResources().getString(R.string.str_operation_success)
					);
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
}