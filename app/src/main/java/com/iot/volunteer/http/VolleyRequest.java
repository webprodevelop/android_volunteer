package com.iot.volunteer.http;

import android.app.Activity;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iot.volunteer.App;
import com.iot.volunteer.Global;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.activity.ActivityLogin;
import com.iot.volunteer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyRequest {
    public static void  getStringResponse(String url, final VolleyCallback resultCallback, String tag){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    if(resultCallback != null){
                        resultCallback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(resultCallback != null){
                    resultCallback.onError(error);
                }
            }
        });

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 60000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        // Adding String request to request queue
        App.Instance().addToRequestQueue(stringRequest, tag);
    }

    public static void  getStringResponsePost(String url, final Map<String, String> data, final VolleyCallback resultCallback, String tag){
//        Util.showToastMessage(App.Instance().getCurrentActivity(), data.get("pAct")); // for test
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if(resultCallback != null){
                        try {
                            JSONObject resp = new JSONObject(response);
                            int iRetCode = resp.getInt("retcode");
                            if (!resp.getString("token").equals("")) {
                                Prefs.Instance().setUserToken(resp.getString("token"));
                                Prefs.Instance().commit();
                            }
                            if (iRetCode == HttpAPIConst.RespCode.ACCOUNT_OTHER_LOGINED) {

                                Activity activity = App.Instance().getCurrentActivity();
                                if (!(activity instanceof ActivityLogin)) {
                                    String sMsg = resp.optString("msg");
                                    Util.ShowDialogError(activity, sMsg, new Util.ResultProcess() {
                                        @Override
                                        public void process() {
                                            Intent intent = new Intent(activity, ActivityLogin.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra("log_out", true);
                                            activity.startActivity(intent);
                                            activity.finish();
                                        }
                                    });
                                    return;
                                }
                            }
                        } catch (JSONException ignored) {

                        }
                        resultCallback.onSuccess(response);
                    }
                }, error -> {
            if(resultCallback != null){
                resultCallback.onError(error);
            }
        }){
            @Override
            public String getBodyContentType() {
                return HttpAPIConst.CONTENT_TYPE;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return data;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 60000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        // Adding String request to request queue
        App.Instance().addToRequestQueue(stringRequest, tag);
    }
}