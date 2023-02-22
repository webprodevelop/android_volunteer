//@formatter:off
package com.iot.volunteer.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.iot.volunteer.R;

import java.util.List;

public class ActivitySelectAddress extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivitySelectAddress";

	private ImageView								ivBack;
	private TextView								tvTitle;
	private AutoCompleteTextView					actSearch;
	private ImageView								ivSearch;
	private MapView									mvMap;
	private TextView								tvConfirm;
	private TextView								tvAddress;

	private BaiduMap								baiduMap;
	private SuggestionSearch						suggestionSearch;
	private List<SuggestionResult.SuggestionInfo>	suggestionInfoList;
	private ArrayAdapter<String>					adapterAddress;
	private MapStatus.Builder						mapStatusBuilder;
	private OverlayOptions							overlayOptions;
	private GeoCoder								geoCoder;
	private BaiduLocationListener					baiduLocationListener;
	private LocationClient							locationClient;
	private LocationClientOption					locationClientOption;

	private String									latitude = "";
	private String									longitude = "";

	private String									province = "";
	private String									city = "";
	private String									district = "";
	private String									streetNumber = "";
	private String									strAddress = "";

	private boolean									isFirstLocation = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_address);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		tvTitle = findViewById(R.id.ID_TXTVIEW_TITLE);
		actSearch = findViewById(R.id.ID_TXTVIEW_AUTO_ADDRESS);
		ivSearch = findViewById(R.id.ID_IMGVIEW_SEARCH);
		mvMap = findViewById(R.id.ID_VIEW_MAP);
		tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);
		tvAddress = findViewById(R.id.ID_TXTVIEW_ADDRESS);

		adapterAddress = new ArrayAdapter<String>(this, R.layout.listitem_address);
		actSearch.setAdapter(adapterAddress);

		mvMap.showZoomControls(false);
		mvMap.showScaleControl(false);
		View child = mvMap.getChildAt(1);
		if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}

		baiduMap = mvMap.getMap();
		baiduMap.setMyLocationEnabled(true);

		suggestionSearch = SuggestionSearch.newInstance();
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		ivSearch.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);

		suggestionSearch.setOnGetSuggestionResultListener(suggestionResultListener);
		actSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				suggestionSearch.requestSuggestion(
						new SuggestionSearchOption().keyword(editable.toString()).city("丹东")
				);
			}
		});

		actSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SuggestionResult.SuggestionInfo info;
				if (suggestionInfoList == null)
					return;
				if (suggestionInfoList.size() == 0)
					return;
				if (position >= suggestionInfoList.size())
					position = suggestionInfoList.size() - 1;
				info = suggestionInfoList.get(position);
				latitude = String.valueOf(info.pt.latitude);
				longitude = String.valueOf(info.pt.longitude);
				// Point Position
				setPointMap(String.valueOf(info.pt.latitude), String.valueOf(info.pt.longitude));

				actSearch.dismissDropDown();
			}
		});

		mapStatusBuilder = new MapStatus.Builder();

		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);

		locationClientOption = new LocationClientOption();
		locationClientOption.setScanSpan(5000);
		locationClientOption.setCoorType("BD09LL");		// gcj02
		locationClientOption.setIsNeedAddress(true);
		locationClientOption.setOpenGps(true);
		locationClient = new LocationClient(this);
		locationClient.setLocOption(locationClientOption);
		baiduLocationListener = new BaiduLocationListener();
		locationClient.registerLocationListener(baiduLocationListener);
		locationClient.start();

		baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				setPointMap(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));

				// Dismiss DropDown
				actSearch.dismissDropDown();
			}
			@Override public void onMapPoiClick(MapPoi mapPoi) {
				LatLng latLng = mapPoi.getPosition();
				onMapClick(latLng);
			}
		});
	}

	private void setPointMap(String lat, String lng) {
		MyLocationData locData = new MyLocationData.Builder().accuracy(0)
				.direction(-1)
				.latitude(Double.parseDouble(lat))
				.longitude(Double.parseDouble(lng))
				.build();

		baiduMap.setMyLocationData(locData);

		LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

		if (isFirstLocation) {
			isFirstLocation = false;
			mapStatusBuilder.target(latLng).zoom(18.0f);
			baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatusBuilder.build()));
		}
		overlayOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red)).position(latLng);
		baiduMap.clear();
		baiduMap.addOverlay(overlayOptions);

		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
	}

	private OnGetGeoCoderResultListener	getGeoCoderResultListener = new OnGetGeoCoderResultListener() {
		@Override
		public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
			if (reverseGeoCodeResult == null
					|| reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			ReverseGeoCodeResult.AddressComponent address = reverseGeoCodeResult.getAddressDetail();
			// Set Address to m_txtAutoAddress
			province = address.province;
			strAddress = address.province;
			if (!address.city.isEmpty()){
				strAddress += " " + address.city;
				city = address.city;
			}
			String sAddress = address.district;
			if (!address.district.isEmpty()){
				strAddress += " " + address.district;
				sAddress = address.district;
				district = address.district;
			}
			if (!address.street.isEmpty()){
				sAddress += " " + address.street;
				strAddress += " " + address.street;
			}
			if (!address.streetNumber.isEmpty()){
				sAddress += " " + address.streetNumber;
				strAddress += " " + address.streetNumber;
			}

			tvAddress.setText(strAddress);

			if (!sAddress.isEmpty()) {
				actSearch.setText(sAddress);
				// When called from m_clientLoc, Once getting address is finished, stop Client
				if (locationClient != null) {
					locationClient.stop();
					locationClient = null;
				}
			}
		}
	};

	private void showSearchDropDown() {
		adapterAddress.getFilter().filter("");
		actSearch.showDropDown();
	}

	/*private Runnable runnableDropDown = new Runnable() {
		@Override
		public void run() {
			showSearchDropDown();
		}
	};*/

	OnGetSuggestionResultListener suggestionResultListener = new OnGetSuggestionResultListener() {
		@Override
		public void onGetSuggestionResult(SuggestionResult result) {
			if (result == null)
				return;
			suggestionInfoList = result.getAllSuggestions();
			if (suggestionInfoList == null)
				return;
			adapterAddress.clear();
			for (SuggestionResult.SuggestionInfo info : suggestionInfoList) {
				String sAddress = "";
				if (info.district != null && info.district.compareTo("null") != 0)
					sAddress += info.district + " ";
				sAddress += info.key;
				adapterAddress.add(sAddress);
			}
			adapterAddress.notifyDataSetChanged();
			showSearchDropDown();
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();

		baiduMap.setMyLocationEnabled(false);
		mvMap.onDestroy();
		mvMap = null;

		if (locationClient != null) {
			locationClient.stop();
			locationClient.unRegisterLocationListener(baiduLocationListener);
		}
		if (suggestionSearch != null) {
			suggestionSearch.destroy();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_IMGVIEW_SEARCH:
				onSearch();
				break;
			case R.id.ID_BTN_CONFIRM:
				onConfirm();
				break;
		}
	}

	private void onSearch() {

	}

	private void onConfirm() {
		Intent intent = new Intent();
		intent.putExtra("province_data", province);
		intent.putExtra("city_data", city);
		intent.putExtra("district_data", district);
		intent.putExtra("address_data", strAddress);
		intent.putExtra("lat_data", latitude);
		intent.putExtra("lon_data", longitude);
		setResult(RESULT_OK, intent);
		finish();
	}

	class BaiduLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation bdLocation) {
			if (bdLocation == null || mvMap == null) {
				return;
			}
			double dLat = bdLocation.getLatitude();
			double dLon = bdLocation.getLongitude();
			if (dLat == Double.MIN_VALUE && dLon == Double.MIN_VALUE) {
				bdLocation.setLatitude(40.006629);
				bdLocation.setLongitude(124.360903);
			}

			setPointMap(String.valueOf(bdLocation.getLatitude()), String.valueOf(bdLocation.getLongitude()));
		}
	}
}
