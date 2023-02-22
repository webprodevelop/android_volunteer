//@formatter:off
package com.iot.volunteer.fragment;

import androidx.fragment.app.Fragment;
import android.view.View;

import com.iot.volunteer.App;
import com.iot.volunteer.activity.ActivityMain;
import com.iot.volunteer.view.DialogProgress;

public class FragmentBase extends Fragment {
	protected String TAG = "FragmentBase";

	protected ActivityMain activityMain;
	protected DialogProgress m_dlgProgress;

	protected void initControls(View layout) {
		m_dlgProgress = new DialogProgress(getActivity());
		m_dlgProgress.setCancelable(false);

		TAG = getClass().getSimpleName();
	}

	protected void setEventListener() {

	}

	public void setActivity(ActivityMain activity) {
		activityMain = activity;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser) {
			App.Instance().cancelPendingRequests(TAG);
		}
	}
}