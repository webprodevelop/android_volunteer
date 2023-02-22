//@formatter:off
package com.iot.volunteer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.adapter.AdapterCashHistory;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.model.ItemCashHistory;
import com.iot.volunteer.model.ItemRescue;
import com.iot.volunteer.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentCashHistory extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private ListView						lvCashHistory;
	private AdapterCashHistory				cashHistoryAdapter;

	private ArrayList<ItemCashHistory>		cashHistoryList = new ArrayList<>();

	static FragmentCashHistory fragment = null;

	public static FragmentCashHistory getInstance() {
		if (fragment == null) {
			fragment = new FragmentCashHistory();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_cash_history, container, false);

		initControls(rootView);
		setEventListener();

		getFinancialList();

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		lvCashHistory = layout.findViewById(R.id.lvCashHistory);

		cashHistoryAdapter = new AdapterCashHistory(getActivity(), cashHistoryList);
		lvCashHistory.setAdapter(cashHistoryAdapter);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});
	}

	private void getFinancialList() {
		m_dlgProgress.show();

		HttpAPI.getFinancialList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
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
					cashHistoryList.clear();
					JSONArray dataJSONArray = jsonObject.getJSONArray("data");
					for (int i = 0; i < dataJSONArray.length(); i++) {
						JSONObject rescueObject = dataJSONArray.getJSONObject(i);
						float fAmount = (float) rescueObject.getDouble("amount");
						String strTime = rescueObject.getString("time");
						String status = rescueObject.getString("status");
						int point = rescueObject.getInt("point");

						ItemCashHistory itemCashHistory = new ItemCashHistory();
						itemCashHistory.amount = fAmount;
						itemCashHistory.time = strTime;
						itemCashHistory.integral = point;
						itemCashHistory.status = status;

						cashHistoryList.add(itemCashHistory);
					}

					cashHistoryAdapter.notifyDataSetChanged();
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
}