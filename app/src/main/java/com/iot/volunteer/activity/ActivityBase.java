package com.iot.volunteer.activity;

import androidx.appcompat.app.AppCompatActivity;
import com.iot.volunteer.App;
import com.iot.volunteer.view.DialogProgress;

public class ActivityBase extends AppCompatActivity {
    protected DialogProgress m_dlgProgress;

    protected void initControls() {
        m_dlgProgress = new DialogProgress(this);
        m_dlgProgress.setCancelable(false);
    }

    protected void setEventListener() {

    }

    protected String getTag() {
        return "ActivityBase";
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.Instance().cancelPendingRequests(getTag());
    }
}
