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
import com.iot.volunteer.adapter.AdapterRecommended;
import com.iot.volunteer.database.IOTDBHelper;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.model.ItemNews;
import com.iot.volunteer.util.Util;
import com.iot.volunteer.view.DialogProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentRecommendedList extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private ListView						lvRecommended;
	private AdapterRecommended				recommendedAdapter;

	static FragmentRecommendedList fragment = null;

	public static FragmentRecommendedList getInstance() {
		if (fragment == null) {
			fragment = new FragmentRecommendedList();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_discover_list, container, false);

		initControls(rootView);
		setEventListener();

		getNewsList();

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		lvRecommended = layout.findViewById(R.id.lvRecommended);

		recommendedAdapter = new AdapterRecommended(getActivity(), activityMain.recommendedArray);
		lvRecommended.setAdapter(recommendedAdapter);

		m_dlgProgress = new DialogProgress(getContext());
		m_dlgProgress.setCancelable(false);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});

		lvRecommended.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				ItemNews itemNews = activityMain.recommendedArray.get(position);
				FragmentRecommended fragmentRecommended = new FragmentRecommended();
				fragmentRecommended.setActivity(activityMain);
				fragmentRecommended.setItemNews(itemNews);
				activityMain.pushChildFragment(fragmentRecommended, FragmentRecommended.class.getSimpleName());
			}
		});
	}

	private void getNewsList() {
		m_dlgProgress.show();

		HttpAPI.getNewsList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
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
					JSONArray recommendedJSONArray = dataObject.getJSONArray("hot_recommend");
					activityMain.recommendedArray.clear();
					activityMain.recommendedPreviewArray.clear();
					for (int i = 0; i < recommendedJSONArray.length(); i++) {
						JSONObject recommendedObject = recommendedJSONArray.getJSONObject(i);

						int id = recommendedObject.getInt("id");
						String createTime = recommendedObject.getString("createDateStr");
						String updateTime = recommendedObject.getString("updatedTimeStr");
						String releaseTime = recommendedObject.getString("releaseTimeStr");
						String status = recommendedObject.getString("status");
						String title = recommendedObject.getString("title");
						String content = recommendedObject.getString("content");
						String picture = recommendedObject.getString("picture");
						String newsType = recommendedObject.getString("newsType");
						String publishTo = recommendedObject.getString("publishTo");
						String newsBranch = recommendedObject.getString("newsBranch");

						ItemNews itemRecommended = new ItemNews(id, createTime, updateTime, releaseTime, status, title, content, picture, newsType, publishTo, newsBranch);
						activityMain.recommendedArray.add(itemRecommended);
						if (i < 3) {
							activityMain.recommendedPreviewArray.add(itemRecommended);
						}
					}

					recommendedAdapter.notifyDataSetChanged();
					//((ActivityMain)getActivity()).showDiscoverNotification(bNotification);
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