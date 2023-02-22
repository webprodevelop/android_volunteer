//@formatter:off
package com.iot.volunteer.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ZoomControls;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.iot.volunteer.Global;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.activity.ActivityMain;
import com.iot.volunteer.activity.ActivityNotification;
import com.iot.volunteer.adapter.AdapterKnowledge;
import com.iot.volunteer.adapter.AdapterRecommended;
import com.iot.volunteer.database.IOTDBHelper;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.model.ItemNews;
import com.iot.volunteer.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentHome extends FragmentBase implements View.OnClickListener {
	static FragmentHome fragment = null;

	public static FragmentHome getInstance() {
		if (fragment == null) {
			fragment = new FragmentHome();
		}
		return fragment;
	}

	private ImageView			ivPhoto;
	private TextView			tvName;
	private TextView			tvPointLevel;
	private LinearLayout		llStars;
	private ImageView			ivStar1;
	private ImageView			ivStar2;
	private ImageView			ivStar3;
	private ImageView			ivStar4;
	private ImageView			ivStar5;
	private ImageView			ivNotification;
	private ImageView			ivNewNotification;
	private LinearLayout		llMonitorPeopleCount;
	private TextView			tvMonitorPeopleCount;
	private TextView			tvInvestMission;
	private TextView			tvRescueRecord;
	private TextView			tvRecommendMore;
	private ListView			lvRecommended;
	private AdapterRecommended	recommendedAdapter;
	private TextView			tvAidKnowledgeMore;
	private RecyclerView		rvKnowledge;
	private AdapterKnowledge	knowledgeAdapter;

	private MapView									mvMap;
	private BaiduMap								baiduMap;
	private boolean									isFirstLocation = true;
	private MapStatus.Builder						mapStatusBuilder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);

		isFirstLocation = true;

		initControls(rootView);
		setEventListener();
		loadData();

		getNewsList();
//		getVolunteerStatistics();
		setPointMap(Global.gLatValue, Global.gLonValue);

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivPhoto = layout.findViewById(R.id.ivPhoto);
		tvName = layout.findViewById(R.id.tvName);
		tvPointLevel = layout.findViewById(R.id.tvPointLevel);
		llStars = layout.findViewById(R.id.llStars);
		ivStar1 = layout.findViewById(R.id.ivStar1);
		ivStar2 = layout.findViewById(R.id.ivStar2);
		ivStar3 = layout.findViewById(R.id.ivStar3);
		ivStar4 = layout.findViewById(R.id.ivStar4);
		ivStar5 = layout.findViewById(R.id.ivStar5);
		ivNotification = layout.findViewById(R.id.ivNotification);
		ivNewNotification = layout.findViewById(R.id.ivNewNotification);
		llMonitorPeopleCount = layout.findViewById(R.id.llMonitorPeopleCount);
		tvMonitorPeopleCount = layout.findViewById(R.id.tvMonitorPeopleCount);
		tvInvestMission = layout.findViewById(R.id.tvInvestMission);
		tvRescueRecord = layout.findViewById(R.id.tvRescueRecord);
		tvRecommendMore = layout.findViewById(R.id.tvRecommendMore);
		lvRecommended = layout.findViewById(R.id.lvRecommended);
		tvAidKnowledgeMore = layout.findViewById(R.id.tvAidKnowledgeMore);
		rvKnowledge = layout.findViewById(R.id.rvKnowledge);
		mvMap = layout.findViewById(R.id.mvMap);

		recommendedAdapter = new AdapterRecommended(getActivity(), activityMain.recommendedPreviewArray);
		lvRecommended.setAdapter(recommendedAdapter);

		RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 4) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		//RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false);
		rvKnowledge.setLayoutManager(layoutManager);

		knowledgeAdapter = new AdapterKnowledge(activityMain.knowledgePreviewArray);
		rvKnowledge.setAdapter(knowledgeAdapter);

		mvMap.showZoomControls(false);
		mvMap.showScaleControl(false);
		View child = mvMap.getChildAt(1);
		if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}

		baiduMap = mvMap.getMap();
		baiduMap.setMyLocationEnabled(true);
	}

	@Override
	protected void setEventListener() {
		llStars.setOnClickListener(this);
		ivNotification.setOnClickListener(this);
		llMonitorPeopleCount.setOnClickListener(this);
		tvInvestMission.setOnClickListener(this);
		tvRescueRecord.setOnClickListener(this);
		tvRecommendMore.setOnClickListener(this);
		tvAidKnowledgeMore.setOnClickListener(this);

		lvRecommended.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				ItemNews itemNews = activityMain.recommendedPreviewArray.get(position);
				FragmentRecommended fragmentRecommended = new FragmentRecommended();
				fragmentRecommended.setActivity(activityMain);
				fragmentRecommended.setItemNews(itemNews);
				activityMain.pushChildFragment(fragmentRecommended, FragmentRecommended.class.getSimpleName());
			}
		});

		knowledgeAdapter.setClickListener(new AdapterKnowledge.ItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				ItemNews itemNews = activityMain.knowledgePreviewArray.get(position);
				FragmentKnowledge fragmentKnowledge = new FragmentKnowledge();
				fragmentKnowledge.setActivity(activityMain);
				fragmentKnowledge.setItemNews(itemNews);
				activityMain.pushChildFragment(fragmentKnowledge, FragmentKnowledge.class.getSimpleName());
			}
		});

		mapStatusBuilder = new MapStatus.Builder();
	}

	@Override
	public void onResume() {
		super.onResume();

		checkNotiAlarm();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		baiduMap.setMyLocationEnabled(false);

		//mvMap.onDestroy();
		//mvMap = null;
	}

	public void checkNotiAlarm() {
		IOTDBHelper iotdbHelper = new IOTDBHelper(getContext());
		boolean isNotiAlarm = iotdbHelper.isNotificationAlarm();

		if (isNotiAlarm) {
			ivNewNotification.setVisibility(View.VISIBLE);
		} else {
			ivNewNotification.setVisibility(View.GONE);
		}
	}

	private void loadData() {
		tvName.setText(Prefs.Instance().getUserName());
		if (!Prefs.Instance().getUserPhoto().isEmpty()) {
			Picasso.get().load(Prefs.Instance().getUserPhoto()).placeholder(R.drawable.img_contact).into(ivPhoto);
		} else {
			ivPhoto.setImageResource(R.drawable.img_contact);
		}

		ivStar1.setVisibility(View.GONE);
		ivStar2.setVisibility(View.GONE);
		ivStar3.setVisibility(View.GONE);
		ivStar4.setVisibility(View.GONE);
		ivStar5.setVisibility(View.GONE);
		int point = Prefs.Instance().getPoint();
		if (point > 5) point = 5;
		if (point < 0) point = 0;
		switch (point) {
			case 1:
				tvPointLevel.setText("V1");
				ivStar1.setVisibility(View.VISIBLE);
				break;
			case 2:
				tvPointLevel.setText("V2");
				ivStar1.setVisibility(View.VISIBLE);
				ivStar2.setVisibility(View.VISIBLE);
				break;
			case 3:
				tvPointLevel.setText("V3");
				ivStar1.setVisibility(View.VISIBLE);
				ivStar2.setVisibility(View.VISIBLE);
				ivStar3.setVisibility(View.VISIBLE);
				break;
			case 4:
				tvPointLevel.setText("V4");
				ivStar1.setVisibility(View.VISIBLE);
				ivStar2.setVisibility(View.VISIBLE);
				ivStar3.setVisibility(View.VISIBLE);
				ivStar4.setVisibility(View.VISIBLE);
				break;
			case 5:
				tvPointLevel.setText("V5");
				ivStar1.setVisibility(View.VISIBLE);
				ivStar2.setVisibility(View.VISIBLE);
				ivStar3.setVisibility(View.VISIBLE);
				ivStar4.setVisibility(View.VISIBLE);
				ivStar5.setVisibility(View.VISIBLE);
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.llStars:
				onPointLevel();
				break;
			case R.id.ivNotification:
				onNotifications();
				break;
			case R.id.llMonitorPeopleCount:
				onMonitorPeopleCount();
				break;
			case R.id.tvInvestMission:
				onInvestMission();
				break;
			case R.id.tvRescueRecord:
				onRescueRecord();
				break;
			case R.id.tvRecommendMore:
				onRecommendMore();
				break;
			case R.id.tvAidKnowledgeMore:
				onAidKnowledgeMore();
				break;
		}
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

					JSONArray knowledgeJSONArray = dataObject.getJSONArray("common_sense");
					activityMain.knowledgeArray.clear();
					activityMain.knowledgePreviewArray.clear();
					for (int i = 0; i < knowledgeJSONArray.length(); i++) {
						JSONObject knowledgeObject = knowledgeJSONArray.getJSONObject(i);

						int id = knowledgeObject.getInt("id");
						String createTime  = knowledgeObject.getString("createDateStr");
						String updateTime  = knowledgeObject.getString("updatedTimeStr");
						String releaseTime = knowledgeObject.getString("releaseTimeStr");
						String status      = knowledgeObject.getString("status");
						String title       = knowledgeObject.getString("title");
						String content     = knowledgeObject.getString("content");
						String picture     = knowledgeObject.getString("picture");
						String newsType    = knowledgeObject.getString("newsType");
						String publishTo   = knowledgeObject.getString("publishTo");
						String newsBranch  = knowledgeObject.getString("newsBranch");

						ItemNews itemKnowledge = new ItemNews(id, createTime, updateTime, releaseTime, status, title, content, picture, newsType, publishTo, newsBranch);
						activityMain.knowledgeArray.add(itemKnowledge);
						if (i < 3) {
							activityMain.knowledgePreviewArray.add(itemKnowledge);
						}
					}

					recommendedAdapter.notifyDataSetChanged();
					knowledgeAdapter.notifyDataSetChanged();
					getVolunteerStatistics();
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

	private void getVolunteerStatistics() {
//		m_dlgProgress.show();

		HttpAPI.getVolunteerStatistics(
			Prefs.Instance().getUserToken(),
			Prefs.Instance().getUserPhone(),
			new VolleyCallback() {
				@Override
				public void onSuccess(String response) {
//					m_dlgProgress.dismiss();

					try {
						JSONObject jsonObject = new JSONObject(response);
						int iRetCode = jsonObject.getInt("retcode");
						if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
							String sMsg = jsonObject.getString("msg");
							Util.ShowDialogError(getActivity(), sMsg);
							return;
						}


						JSONArray arrStatistics = jsonObject.getJSONArray("data");
						for (int i = 0; i < arrStatistics.length(); i++) {
							JSONObject statisticsObj = arrStatistics.getJSONObject(i);

							String sCount = statisticsObj.getString("count");
							String sLat   = statisticsObj.getString("lat");
							String sLon   = statisticsObj.getString("lon");

							/// Marker for Statistics Text
							LatLng latlon = new LatLng(
								Double.parseDouble(sLat),
								Double.parseDouble(sLon)
							);

							View viewMarker = View.inflate(getActivity(), R.layout.marker_statistics, null);
							TextView txtMarker = viewMarker.findViewById(R.id.ID_TXT_STATISTICS);
							txtMarker.setText(sCount);
							Bitmap bmpMarker = Util.getViewBitmap(viewMarker);
							BitmapDescriptor bmpDescriptor = BitmapDescriptorFactory.fromBitmap(bmpMarker);

							OverlayOptions option = new MarkerOptions()
								.position(latlon)
								.icon(bmpDescriptor);

							baiduMap.addOverlay(option);
						}
					}
					catch (JSONException e) {
					}
				}

				@Override
				public void onError(Object error) {
				}
			},
			TAG
		);
	}

	public void onPointLevel() {
		FragmentPointLevel fragmentPointLevel = new FragmentPointLevel();
		activityMain.pushChildFragment(fragmentPointLevel, FragmentPointLevel.class.getSimpleName());
		fragmentPointLevel.setActivity(activityMain);
	}

	private void onNotifications() {
		Intent intent = new Intent(getActivity(), ActivityNotification.class);
		getActivity().startActivityForResult(intent, ActivityMain.REQUEST_NOTIFICATION);
	}

	private void onMonitorPeopleCount() {

	}

	private void onInvestMission() {
		activityMain.gotoMission(null);
	}

	private void onRescueRecord() {
		FragmentRescueQuery fragmentRescueQuery = new FragmentRescueQuery();
		activityMain.pushChildFragment(fragmentRescueQuery, FragmentRescueQuery.class.getSimpleName());
		fragmentRescueQuery.setActivity(activityMain);
	}

	private void onRecommendMore() {
		FragmentRecommendedList fragmentRecommendedList = new FragmentRecommendedList();
		fragmentRecommendedList.setActivity(activityMain);
		activityMain.pushChildFragment(fragmentRecommendedList, FragmentRecommendedList.class.getSimpleName());
		fragmentRecommendedList.setActivity(activityMain);
	}

	private void onAidKnowledgeMore() {
		FragmentKnowledgeList fragmentKnowledgeList = new FragmentKnowledgeList();
		fragmentKnowledgeList.setActivity(activityMain);
		activityMain.pushChildFragment(fragmentKnowledgeList, FragmentKnowledgeList.class.getSimpleName());
		fragmentKnowledgeList.setActivity(activityMain);
	}

	public void setPointMap(double dLat, double dLon) {
//		MyLocationData locData = new MyLocationData.Builder().accuracy(0)
//			.direction(-1)
//			.latitude(dLat)
//			.longitude(dLon)
//			.build();
//
//		baiduMap.setMyLocationData(locData);
		if (dLat == 0 || dLon == 0 ) {
			return;
		}

		LatLng latLng = new LatLng(dLat, dLon);

		if (isFirstLocation) {
			isFirstLocation = false;
			mapStatusBuilder.target(latLng).zoom(10.0f);
			baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatusBuilder.build()));
//			OverlayOptions overlayOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_you)).position(latLng);
//			baiduMap.addOverlay(overlayOptions);
		}
	}
}