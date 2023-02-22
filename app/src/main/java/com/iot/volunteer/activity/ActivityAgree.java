//@formatter:off
package com.iot.volunteer.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityAgree extends ActivityBase {
	private final String TAG = "ActivityAgree";

	private ImageView	mBackImg;
	private TextView	mTitleView;
	private WebView		mWebView;

	private boolean		mAgreementOrPolicy = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agree);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		mAgreementOrPolicy = getIntent().getBooleanExtra("agreement_policy", true);

		initControls();
		setEventListener();
	}

	@Override
	protected void initControls() {
		super.initControls();

		mBackImg = findViewById(R.id.ID_IMGVIEW_BACK);
		mTitleView = findViewById(R.id.ID_TXTVIEW_TITLE);
		mWebView = findViewById(R.id.ID_WEBVIEW_CONTENT);

		if (mAgreementOrPolicy) {
			mTitleView.setText(R.string.str_agree_user_agreement);

			String strAgreement = Prefs.Instance().getAgreement();
			mWebView.loadData(strAgreement, "text/html", "UTF-8");
		} else {
			mTitleView.setText(R.string.str_agree_privacy_policy);

			String strPolicy = Prefs.Instance().getPolicy();
			mWebView.loadData(strPolicy, "text/html", "UTF-8");
		}
	}

	@Override
	protected void setEventListener() {
		mBackImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
