//@formatter:off
package com.iot.volunteer.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.model.ItemNews;
import com.iot.volunteer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentKnowledge extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private TextView						tvContentTitle;
	private WebView							wvContent;

	private ItemNews						itemNews;

	static FragmentKnowledge fragment = null;
	private String content;

	public static FragmentKnowledge getInstance() {
		if (fragment == null) {
			fragment = new FragmentKnowledge();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_knowledge, container, false);

		initControls(rootView);
		setEventListener();

		HttpAPI.getNewsInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNews.id, new VolleyCallback() {
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
					content = dataObject.getString("content");
					if (content != null) {
						tvContentTitle.setText(itemNews.title);
						wvContent.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + content, "text/html", "UTF-8", null);
					}
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

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		tvContentTitle = layout.findViewById(R.id.tvContentTitle);
		wvContent = layout.findViewById(R.id.wvContent);
		wvContent.setWebChromeClient(new WebChromeClient(){
			@Override
			public Bitmap getDefaultVideoPoster() {
				if (this == null) {
					return null;
				}
				final Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
				Canvas canvas = new Canvas(bitmap);
				canvas.drawARGB(255, 255, 255, 255);
				return bitmap;
			}
		});
		if (itemNews != null) {
			if (itemNews.title != null) {
				tvTitle.setText(layout.getContext().getResources().getText(R.string.str_aid_knowledge));
			}
//			if (itemNews.content != null) {
//				tvContentTitle.setText(itemNews.title);
////				wvContent.loadData(itemNews.content, "text/html", "UTF-8");
//				wvContent.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + itemNews.content, "text/html", "UTF-8", null);
//			}
		}
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

	public void setItemNews(ItemNews itemNews) {
		this.itemNews = itemNews;
	}
}