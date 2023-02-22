//@formatter:off
package com.iot.volunteer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.R;

public class FragmentPointRule extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private WebView							wvContent;

	private String							pointRule;

	static FragmentPointRule fragment = null;

	public static FragmentPointRule getInstance() {
		if (fragment == null) {
			fragment = new FragmentPointRule();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_point_rule, container, false);

		initControls(rootView);
		setEventListener();

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		wvContent = layout.findViewById(R.id.wvContent);

		if (pointRule != null) {
//			wvContent.loadData(pointRule, "text/html", "UTF-8");
			wvContent.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + pointRule, "text/html", "UTF-8", null);
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

	public void setPointRule(String pointRule) {
		this.pointRule = pointRule;
	}
}