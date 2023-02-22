//@formatter:off
package com.iot.volunteer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.adapter.AdapterRescue;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.model.ItemRescue;
import com.iot.volunteer.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentRescueQuery extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private ListView						lvRescueQuery;
	private AdapterRescue					rescuedAdapter;

	private ArrayList<ItemRescue>			rescueList = new ArrayList<>();

	static FragmentRescueQuery fragment = null;

	public static FragmentRescueQuery getInstance() {
		if (fragment == null) {
			fragment = new FragmentRescueQuery();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_rescue_query, container, false);

		initControls(rootView);
		setEventListener();

		getRescueList();

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		lvRescueQuery = layout.findViewById(R.id.lvRescueQuery);

		rescuedAdapter = new AdapterRescue(getActivity(), rescueList);
		lvRescueQuery.setAdapter(rescuedAdapter);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});

		lvRescueQuery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				FragmentRescueDetail fragmentRescueDetail = new FragmentRescueDetail();
				activityMain.pushChildFragment(fragmentRescueDetail, FragmentRescueDetail.class.getSimpleName());
				fragmentRescueDetail.setActivity(activityMain);
				ItemRescue itemRescue = rescueList.get(position);
				fragmentRescueDetail.setRescueInfo(itemRescue);
			}
		});
	}

	private void getRescueList() {
		m_dlgProgress.show();

		HttpAPI.getRescueList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
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

					JSONArray dataJSONArray = jsonObject.getJSONArray("data");
					for (int i = 0; i < dataJSONArray.length(); i++) {
						JSONObject rescueObject = dataJSONArray.getJSONObject(i);
						int rescueId = rescueObject.getInt("task_id");
						String strVictim = rescueObject.getString("contactName");
						String strRescueTime = "";
						if (rescueObject.has("finish_time")) {
							strRescueTime = rescueObject.getString("finish_time");
						}
						int integral = rescueObject.getInt("point");

						ItemRescue itemRescue = new ItemRescue();
						itemRescue.rescueId = rescueId;
						itemRescue.victim = strVictim;
						itemRescue.rescueTime = strRescueTime;
						itemRescue.status = 3;
						itemRescue.integral = integral;

						rescueList.add(itemRescue);
					}

					rescuedAdapter.notifyDataSetChanged();
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