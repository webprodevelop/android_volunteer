package com.iot.volunteer.http;

import com.iot.volunteer.Global;
import com.iot.volunteer.model.ItemSensorInfo;
import com.iot.volunteer.model.ItemWatchInfo;

import java.util.HashMap;
import java.util.Map;

public class HttpAPI {
    public static void login(String phoneNumber, String password, String jpushToken, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "login");
        params.put("mobile",	phoneNumber);
        params.put("password",	password);
        params.put("registration_id",	jpushToken);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getVerifyCode(String phoneNumber, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "getVerifyCode");
        params.put("mobile",	phoneNumber);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void register(String phoneNumber, String code, String password, String jpushToken, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "register");
        params.put("mobile", phoneNumber);
        params.put("verify_code", code);
        params.put("password", password);
        params.put("registration_id",	jpushToken);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

	public static void getVolunteerStatistics(String token, String mobile, final VolleyCallback resultCallback, String tag) {
		String url = HttpAPIConst.URL_API;

		Map<String, String> params = new HashMap<String, String>();
		params.put("pAct", "getVolunteerStatistics");
		params.put("mobile", mobile);
		params.put("token", token);

		VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
	}

    public static void getWechatAccessToken(String appid, String secret, String code, String grant_type, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_WECHAT_ACCESS_TOKEN;

        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", appid);
        params.put("secret", secret);
        params.put("code", code);
        params.put("grant_type", grant_type);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getWechatUserInfo(String access_token, String openid, String lang, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_WECHAT_USER_INFO;

        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", access_token);
        params.put("openid", openid);
        params.put("lang", lang);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void registerPayAccount(String token, String mobile, String pay_type, String pay_account_id, String pay_account_name, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "registerPayAccount");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("pay_type", pay_type);
        params.put("pay_account_id", pay_account_id);
        params.put("pay_account_name",	pay_account_name);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void removePayAccount(String token, String mobile, String pay_type, String verify_code, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "removePayAccount");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("pay_type", pay_type);
        params.put("verify_code", verify_code);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void requestTransferPay(String token, String mobile, String pay_type, String point, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "requestTransferPay");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("pay_type", pay_type);
        params.put("point", point);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void forgotPassword(String phoneNumber, String code, String password, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "forgotPassword");
        params.put("mobile", phoneNumber);
        params.put("verify_code", code);
        params.put("password", password);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void resetPassword(String token, String mobile, String code, String oldPassword, String newPassword, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "resetPassword");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("verify_code", code);
        params.put("old_password", oldPassword);
        params.put("new_password", newPassword);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getNewsList(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "getNewsList");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getNewsInfo(String token, String mobile, int id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "getNewsInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getIdCardFrontInfo(String token, String mobile, String id_card_front_src, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getIdCardFrontInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id_card_front_src", id_card_front_src);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getRescueList(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "getRescueList");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getRescueDetail(String token, String mobile, int rescueId, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "getRescueDetail");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("task_id", String.valueOf(rescueId));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void updateUserInfo(String token, String mobile, String name, int sex, String birthday, String residence, String phone, String company, String job, String id_card_num, String address,
                                      String picture, String IDFront, String IDBack, String certPic, String certificate, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "updateUserInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("name", name);
        params.put("sex", String.valueOf(sex));
        params.put("birthday", birthday);
        params.put("residence", residence);
        params.put("address", address);
        params.put("id_card_num", id_card_num);
        params.put("phone", phone);
        params.put("company", company);
        params.put("job", job);
        params.put("picture_src", picture);
        params.put("id_card_front_src", IDFront);
        params.put("id_card_back_src", IDBack);
        params.put("certificate_src", certPic);
        params.put("certificate", certificate);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getPointRule(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "getPointRule");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getFinancialList(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "getFinancialList");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void modifyBankPassword(String token, String mobile, String code, String oldPassword, String newPassword, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "modifyBankPassword");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("verify_code", code);
        params.put("old_bank_password", oldPassword);
        params.put("new_bank_password", newPassword);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void registerBankCard(String token, String mobile, String bankName, String bankCard, String bankPassword, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "modifyBankPassword");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("bank_name", bankName);
        params.put("bank_card", bankCard);
        params.put("bank_password", bankPassword);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void forgotBankPassword(String token, String mobile, String bankPassword, String verifyCode, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "forgotBankPassword");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("bank_password", bankPassword);
        params.put("verify_code", verifyCode);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getTaskDetail(String token, String mobile, int taskId, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "getTaskDetail");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("task_id", String.valueOf(taskId));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void finishTask(String token, String mobile, int taskId, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "finishTask");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("task_id", String.valueOf(taskId));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void cancelTask(String token, String mobile, int taskId, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "cancelTask");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("task_id", String.valueOf(taskId));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void acceptTask(String token, String mobile, int taskId, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "acceptTask");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("task_id", String.valueOf(taskId));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void setUserStatusDisabled(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "setUserStatusDisabled");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void setUserStatusReady(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "setUserStatusReady");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void updateLocation(String token, String mobile, String lat, String lon, String province, String city, String district, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "updateLocation");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("lat", lat);
        params.put("lon", lon);
        params.put("province", province);
        params.put("city", city);
        params.put("district", district);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getAppInfo(final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "getAppInfo");

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getWeatherInfo(String city, final VolleyCallback resultCallback, String tag) {
        String url = "https://way.jd.com/he/freeweather?city=" + city + "&appkey=3dc194ba40b2ded0a8286bb42030a231";
        //"http://api.map.baidu.com/telematics/v3/weather?location=dandong&output=json&ak=3p49MVra6urFRGOT9s8UBWr2"

        VolleyRequest.getStringResponse(url, resultCallback, tag);
    }

    public static void requestChat(String token, String mobile, String taskId, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "requestChat");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("task_id", taskId);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getAllNotificationList(String token, String mobile, int last_id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getAllNotificationList");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("last_id", String.valueOf(last_id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getAllNotificationList(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getAllNotificationList");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void removeNotification(String token, String mobile, int id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "removeNotification");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void readNotification(String token, String mobile, int id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "readNotification");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void readAllNotification(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "readAllNotification");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void removeAllNotification(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "removeAllNotification");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getUpdateUserInfoVerifyCode(String phoneNumber, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<String, String>();
        params.put("pAct", "getUpdateUserInfoVerifyCode");
        params.put("mobile",	phoneNumber);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void updateMobile(String token, String mobile, String newMobile, String verifyCode, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "updateMobile");
        params.put("mobile", mobile);
        params.put("token", token);
        params.put("new_mobile", newMobile);
        params.put("verify_code", verifyCode);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

}
