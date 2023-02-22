//@formatter:off
package com.iot.volunteer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.adapter.AdapterKnowledge;
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

public class FragmentKnowledgeList extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private RecyclerView rvKnowledge;
	private AdapterKnowledge				knowledgeAdapter;

	static FragmentKnowledgeList fragment = null;

	public static FragmentKnowledgeList getInstance() {
		if (fragment == null) {
			fragment = new FragmentKnowledgeList();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_knowledge_list, container, false);

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
		rvKnowledge = layout.findViewById(R.id.rvKnowledge);
		RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
		//RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false);
		rvKnowledge.setLayoutManager(layoutManager);

		knowledgeAdapter = new AdapterKnowledge(activityMain.knowledgeArray);
		rvKnowledge.setAdapter(knowledgeAdapter);
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
					JSONArray knowledgeJSONArray = dataObject.getJSONArray("common_sense");
					activityMain.knowledgeArray.clear();
					activityMain.knowledgePreviewArray.clear();
					for (int i = 0; i < knowledgeJSONArray.length(); i++) {
						JSONObject knowledgeObject = knowledgeJSONArray.getJSONObject(i);

						int id = knowledgeObject.getInt("id");
						String createTime = knowledgeObject.getString("createDateStr");
						String updateTime = knowledgeObject.getString("updatedTimeStr");
						String releaseTime = knowledgeObject.getString("releaseTimeStr");
						String status = knowledgeObject.getString("status");
						String title = knowledgeObject.getString("title");
						String content = knowledgeObject.getString("content");
						String picture = knowledgeObject.getString("picture");
						String newsType = knowledgeObject.getString("newsType");
						String publishTo = knowledgeObject.getString("publishTo");
						String newsBranch = knowledgeObject.getString("newsBranch");

						ItemNews itemKnowledge = new ItemNews(id, createTime, updateTime, releaseTime, status, title, content, picture, newsType, publishTo, newsBranch);
						activityMain.knowledgeArray.add(itemKnowledge);
						if (i < 3) {
							activityMain.knowledgePreviewArray.add(itemKnowledge);
						}
					}

					knowledgeAdapter.notifyDataSetChanged();
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

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});

		knowledgeAdapter.setClickListener(new AdapterKnowledge.ItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				ItemNews itemNews = activityMain.knowledgeArray.get(position);
				FragmentKnowledge fragmentKnowledge = new FragmentKnowledge();
				fragmentKnowledge.setActivity(activityMain);
				fragmentKnowledge.setItemNews(itemNews);
				activityMain.pushChildFragment(fragmentKnowledge, FragmentKnowledge.class.getSimpleName());
			}
		});
	}
}