//@formatter:off
package com.iot.volunteer;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
	private static final String PREFS = "manage_sensor";

	private static final String PREF_USER_TOKEN			= "user_token";
	private static final String PREF_SAVE_USER			= "save_user";
	private static final String PREF_SAVE_PWD			= "save_pwd";
	private static final String PREF_USER_NAME			= "user_name";
	private static final String PREF_USER_PHONE			= "user_phone";
	private static final String PREF_USER_PSWD			= "user_pswd";
	private static final String PREF_MONITERING_WATCH	= "monitering_watch_id";
	private static final String PREF_SEX				= "user_sex";
	private static final String PREF_BIRTHDAY			= "user_birthday";
	private static final String PREF_QQ_ID				= "user_qq_id";
	private static final String PREF_EMAIL				= "user_email";
	private static final String PREF_PROVINCE			= "user_province";
	private static final String PREF_CITY				= "user_city";
	private static final String PREF_DISTRICT			= "user_district";
	private static final String PREF_USER_PHOTO			= "user_photo";
	private static final String PREF_CONTENT			= "user_content";
	private static final String PREF_POLICY				= "user_policy";
	private static final String PREF_POINT				= "user_point";
	private static final String PREF_MERIT				= "user_merit";
	private static final String PREF_CASH				= "user_cash";
	private static final String PREF_BANK_NAME			= "user_bank_name";
	private static final String PREF_BANK_ID			= "user_bank_id";
	private static final String PREF_BANK_PASSWORD		= "user_bank_password";
	private static final String PREF_ALIPAY_NAME		= "user_alipay_name";
	private static final String PREF_ALIPAY_ID			= "user_alipay_id";
	private static final String PREF_WEIXIN_NAME		= "user_weixin_name";
	private static final String PREF_WEIXIN_ID			= "user_weixin_id";
	private static final String PREF_BALANCE			= "balance";
	private static final String PREF_POINT_LEVEL		= "point_level";
	private static final String PREF_TASK_STATUS		= "user_task_status";
	private static final String PREF_TASK_ID			= "user_task_id";

	private static final String PREF_ID_CARD_BACK		= "user_id_card_back";
	private static final String PREF_ID_CARD_FRONT		= "user_id_card_front";
	private static final String PREF_ADDRESS			= "user_address";
	private static final String PREF_CERTIFICATE		= "user_certificate";
	private static final String PREF_ID_CARD_NUM		= "user_id_card_num";
	private static final String PREF_CERT_PIC			= "user_cert_pic";
	private static final String PREF_COMPANY			= "user_company";
	private static final String PREF_RESIDENCE			= "user_residence";
	private static final String PREF_JOB				= "user_job";
	private static final String PREF_JOB_CERT			= "user_job_cert";

	private static final String PREF_AGREEMENT			= "user_agreement";
	private static final String PREF_WATCH_FREE_DAYS	= "watch_free_days";
	private static final String PREF_YANGAN_FREE_DAYS	= "yangan_free_days";
	private static final String PREF_RANQI_FREE_DAYS	= "ranqi_free_days";
	private static final String PREF_WATCH_PAY			= "watch_pay";
	private static final String PREF_YANGAN_PAY			= "yangan_pay";
	private static final String PREF_RANQI_PAY			= "ranpi_pay";
	private static final String PREF_USER_BIRTH_DESC	= "user_birth_desc";
	private static final String PREF_SOS_STATUS			= "sos_status";
	private static final String PREF_FIRE_STATUS		= "fire_status";
	private static final String PREF_WATCH_NETSTATUS	= "watch_netstatus";
	private static final String PREF_FIRE_NETSTATUS		= "fire_netstatus";
	private static final String PREF_HEARTRATE_STATUS	= "heartrate_status";
	private static final String PREF_WATCHBATTERY_STATUS= "watchbattery_status";
	private static final String PREF_FIREBATTERY_STATUS	= "firebattery_status";
	private static final String PREF_ELECFENCE_SATTUS	= "elecfence_status";
	private static final String PREF_MORNING_STATUS		= "morning_status";
	private static final String PREF_EVENING_STATUS		= "evening_status";
	private static final String PREF_NEW_HEART_RATES	= "new_heart_rate_count";
	private static final String PREF_INFO_NOTIFICATION	= "info_notification";
	private static final String PREF_SHOW_AGAIN_MANUAL	= "show_again_manual";

	private static final String PREF_AGREED			= "agreement";

	public static final int	DEFAULT_MAX_RATE		= 100;
	public static final int	DEFAULT_MIN_RATE		= 60;

	private static Prefs		m_instance;
	private SharedPreferences			m_prefsRead;
	private SharedPreferences.Editor	m_prefsWrite;

	public static Prefs Instance() {
		return m_instance;
	}

	public Prefs(Context a_context) {
		m_instance = this;

		m_prefsRead = a_context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		m_prefsWrite = m_prefsRead.edit();
	}

	public void commit() {
		m_prefsWrite.commit();
	}

	public Boolean isSaveUser() {
		return m_prefsRead.getBoolean(PREF_SAVE_USER, false);
	}

	public Boolean isSavePassword() {
		return m_prefsRead.getBoolean(PREF_SAVE_PWD, false);
	}

	public String getUserName() {
		return m_prefsRead.getString(PREF_USER_NAME, "");
	}

	public String getUserPhone() {
		return m_prefsRead.getString(PREF_USER_PHONE, "");
	}

	public String getUserPswd() {
		return m_prefsRead.getString(PREF_USER_PSWD, "");
	}

	public String getUserToken() {
		return m_prefsRead.getString(PREF_USER_TOKEN, "");
	}

	public int getMoniteringWatchId() {
		return m_prefsRead.getInt(PREF_MONITERING_WATCH, -1);
	}

	public boolean getSex() {
		return m_prefsRead.getBoolean(PREF_SEX, true);
	}

	public String getBirthday() {
		return m_prefsRead.getString(PREF_BIRTHDAY, "");
	}

	public String getQQId() {
		return m_prefsRead.getString(PREF_QQ_ID, "");
	}

	public String getEmail() {
		return m_prefsRead.getString(PREF_EMAIL, "");
	}

	public String getProvince() {
		return m_prefsRead.getString(PREF_PROVINCE, "");
	}

	public String getCity() {
		return m_prefsRead.getString(PREF_CITY, "");
	}

	public String getDistrict() {
		return m_prefsRead.getString(PREF_DISTRICT, "");
	}

	public String getContent() {
		return m_prefsRead.getString(PREF_CONTENT, "");
	}

	public String getPolicy() {
		return m_prefsRead.getString(PREF_POLICY, "");
	}

	public int getPoint() {
		return m_prefsRead.getInt(PREF_POINT, 0);
	}

	public int getMerit() {
		return m_prefsRead.getInt(PREF_MERIT, 0);
	}

	public float getCash() {
		return m_prefsRead.getFloat(PREF_CASH, 0);
	}

	public String getBankName() {
		return m_prefsRead.getString(PREF_BANK_NAME, "");
	}

	public String getBankId() {
		return m_prefsRead.getString(PREF_BANK_ID, "");
	}

	public String getBankPassword() {
		return m_prefsRead.getString(PREF_BANK_PASSWORD, "");
	}

	public String getAlipayName() {
		return m_prefsRead.getString(PREF_ALIPAY_NAME, "");
	}

	public String getAlipayId() {
		return m_prefsRead.getString(PREF_ALIPAY_ID, "");
	}

	public String getWeixinName() {
		return m_prefsRead.getString(PREF_WEIXIN_NAME, "");
	}

	public String getWeixinId() {
		return m_prefsRead.getString(PREF_WEIXIN_ID, "");
	}

	public String getBalance() {
		return m_prefsRead.getString(PREF_BALANCE, "");
	}

	public String getPointLevel() {
		return m_prefsRead.getString(PREF_POINT_LEVEL, "");
	}

	public int getTaskStatus() {
		return m_prefsRead.getInt(PREF_TASK_STATUS, 0);
	}

	public String getTaskId() {
		return m_prefsRead.getString(PREF_TASK_ID, null);
	}

	public String getIDCardBack() {
		return m_prefsRead.getString(PREF_ID_CARD_BACK, "");
	}

	public String getIDCardFront() {
		return m_prefsRead.getString(PREF_ID_CARD_FRONT, "");
	}

	public String getAddress() {
		return m_prefsRead.getString(PREF_ADDRESS, "");
	}

	public String getCertificate() {
		return m_prefsRead.getString(PREF_CERTIFICATE, "");
	}

	public String getIDCardNum() {
		return m_prefsRead.getString(PREF_ID_CARD_NUM, "");
	}

	public String getCertPic() {
		return m_prefsRead.getString(PREF_CERT_PIC, "");
	}

	public String getCompany() {
		return m_prefsRead.getString(PREF_COMPANY, "");
	}

	public String getResidence() {
		return m_prefsRead.getString(PREF_RESIDENCE, "");
	}

	public String getJob() {
		return m_prefsRead.getString(PREF_JOB, "");
	}

	public String getJobCert() {
		return m_prefsRead.getString(PREF_JOB_CERT, "");
	}

	public String getAgreement() {
		return m_prefsRead.getString(PREF_AGREEMENT, "");
	}

	public boolean getAgreed() {
		return m_prefsRead.getBoolean(PREF_AGREED, false);
	}

	public int getWatchFreeDays() {
		return m_prefsRead.getInt(PREF_WATCH_FREE_DAYS, 0);
	}

	public int getYanganFreeDays() {
		return m_prefsRead.getInt(PREF_YANGAN_FREE_DAYS, 0);
	}

	public int getRanqiFreeDays() {
		return m_prefsRead.getInt(PREF_RANQI_FREE_DAYS, 0);
	}

	public String getWatchPay() {
		return m_prefsRead.getString(PREF_WATCH_PAY, "");
	}

	public String getYanganPay() {
		return m_prefsRead.getString(PREF_YANGAN_PAY, "");
	}

	public String getRanqiPay() {
		return m_prefsRead.getString(PREF_RANQI_PAY, "");
	}

	public String getUserBirthDesc() {
		return m_prefsRead.getString(PREF_USER_BIRTH_DESC, "");
	}

	public String getUserPhoto() {
		return m_prefsRead.getString(PREF_USER_PHOTO, "");
	}

	public boolean getSosStatus() {
		return m_prefsRead.getBoolean(PREF_SOS_STATUS, false);
	}

	public boolean getFireStatus() {
		return m_prefsRead.getBoolean(PREF_FIRE_STATUS, false);
	}

	public boolean getWatchNetStatus() {
		return m_prefsRead.getBoolean(PREF_WATCH_NETSTATUS, false);
	}

	public boolean getFireNetStatus() {
		return m_prefsRead.getBoolean(PREF_FIRE_NETSTATUS, false);
	}

	public boolean getHeartRateStatus() {
		return m_prefsRead.getBoolean(PREF_HEARTRATE_STATUS, false);
	}

	public boolean getWatchBatteryStatus() {
		return m_prefsRead.getBoolean(PREF_WATCHBATTERY_STATUS, false);
	}

	public boolean getFireBatteryStatus() {
		return m_prefsRead.getBoolean(PREF_FIREBATTERY_STATUS, false);
	}

	public boolean getElecFenceStatus() {
		return m_prefsRead.getBoolean(PREF_ELECFENCE_SATTUS, false);
	}

	public boolean getInfoNotification() {
		return m_prefsRead.getBoolean(PREF_INFO_NOTIFICATION, true);
	}

	public boolean getMorningStatus() {
		return m_prefsRead.getBoolean(PREF_MORNING_STATUS, false);
	}

	public boolean getEveningStatus() {
		return m_prefsRead.getBoolean(PREF_EVENING_STATUS, false);
	}

	public int getNewHeartRateCount() {
		return m_prefsRead.getInt(PREF_NEW_HEART_RATES, 0);
	}

	public boolean getShowAgainManual() {
		return m_prefsRead.getBoolean(PREF_SHOW_AGAIN_MANUAL, true);
	}

	public void setSaveUser(Boolean a_bUser) {
		m_prefsWrite.putBoolean(PREF_SAVE_USER, a_bUser);
	}

	public void setSavePassword(Boolean a_bPwd) {
		m_prefsWrite.putBoolean(PREF_SAVE_PWD, a_bPwd);
	}

	public void setUserName(String a_sUserName) {
		m_prefsWrite.putString(PREF_USER_NAME, a_sUserName);
	}

	public void setUserPhone(String a_sUserPhone) {
		m_prefsWrite.putString(PREF_USER_PHONE, a_sUserPhone);
	}

	public void setUserPswd(String a_sUserPswd) {
		m_prefsWrite.putString(PREF_USER_PSWD, a_sUserPswd);
	}

	public void setUserToken(String a_sUserToken) {
		m_prefsWrite.putString(PREF_USER_TOKEN, a_sUserToken);
	}

	public void setMoniteringWatchId(int a_iMoniteringWatchId) {
		m_prefsWrite.putInt(PREF_MONITERING_WATCH, a_iMoniteringWatchId);
	}

	public void setSex(boolean a_bSex) {
		m_prefsWrite.putBoolean(PREF_SEX, a_bSex);
	}

	public void setBirthday(String a_sBirthday) {
		m_prefsWrite.putString(PREF_BIRTHDAY, a_sBirthday);
	}

	public void setQQId(String a_sQQId) {
		m_prefsWrite.putString(PREF_QQ_ID, a_sQQId);
	}

	public void setEmail(String a_sEmail) {
		m_prefsWrite.putString(PREF_EMAIL, a_sEmail);
	}

	public void setProvince(String a_sProvince) {
		m_prefsWrite.putString(PREF_PROVINCE, a_sProvince);
	}

	public void setCity(String a_sCity) {
		m_prefsWrite.putString(PREF_CITY, a_sCity);
	}

	public void setDistrict(String a_sDistrict) {
		m_prefsWrite.putString(PREF_DISTRICT, a_sDistrict);
	}

	public void setUserPhoto(String a_sUserPhoto) {
		m_prefsWrite.putString(PREF_USER_PHOTO, a_sUserPhoto);
	}

	public void setContent(String a_sContent) {
		m_prefsWrite.putString(PREF_CONTENT, a_sContent);
	}

	public void setPolicy(String a_sPolicy) {
		m_prefsWrite.putString(PREF_POLICY, a_sPolicy);
	}

	public void setPoint(int a_nPoint) {
		m_prefsWrite.putInt(PREF_POINT, a_nPoint);
	}

	public void setMerit(int a_nMerit) {
		m_prefsWrite.putInt(PREF_MERIT, a_nMerit);
	}

	public void setCash(float a_fCash) {
		m_prefsWrite.putFloat(PREF_CASH, a_fCash);
	}

	public void setBankName(String a_sBankName) {
		m_prefsWrite.putString(PREF_BANK_NAME, a_sBankName);
	}

	public void setBankId(String a_sBankId) {
		m_prefsWrite.putString(PREF_BANK_ID, a_sBankId);
	}

	public void setBankPassword(String a_sBankPassword) {
		m_prefsWrite.putString(PREF_BANK_PASSWORD, a_sBankPassword);
	}

	public void setAlipayName(String a_sAlipayName) {
		m_prefsWrite.putString(PREF_ALIPAY_NAME, a_sAlipayName);
	}

	public void setAlipayId(String a_sAlipayId) {
		m_prefsWrite.putString(PREF_ALIPAY_ID, a_sAlipayId);
	}

	public void setWeixinName(String a_sWeixinName) {
		m_prefsWrite.putString(PREF_WEIXIN_NAME, a_sWeixinName);
	}

	public void setWeixinId(String a_sWeixinId) {
		m_prefsWrite.putString(PREF_WEIXIN_ID, a_sWeixinId);
	}

	public void setBalance(String a_sWeixinId) {
		m_prefsWrite.putString(PREF_BALANCE, a_sWeixinId);
	}

	public void setPointLevel(String a_sWeixinId) {
		m_prefsWrite.putString(PREF_POINT_LEVEL, a_sWeixinId);
	}

	public void setTaskStatus(int a_iTaskStatus) {
		m_prefsWrite.putInt(PREF_TASK_STATUS, a_iTaskStatus);
	}

	public void setTaskId(String a_sTaskId) {
		m_prefsWrite.putString(PREF_TASK_ID, a_sTaskId);
	}

	public void setIDCardBack(String a_sIDCardBack) {
		m_prefsWrite.putString(PREF_ID_CARD_BACK, a_sIDCardBack);
	}

	public void setIDCardFront(String a_sIDCardFront) {
		m_prefsWrite.putString(PREF_ID_CARD_FRONT, a_sIDCardFront);
	}

	public void setAddress(String a_sAddress) {
		m_prefsWrite.putString(PREF_ADDRESS, a_sAddress);
	}

	public void setCertificate(String a_sCertificate) {
		m_prefsWrite.putString(PREF_CERTIFICATE, a_sCertificate);
	}

	public void setIDCardNum(String a_sIDCardNum) {
		m_prefsWrite.putString(PREF_ID_CARD_NUM, a_sIDCardNum);
	}

	public void setCertPic(String a_sCertPic) {
		m_prefsWrite.putString(PREF_CERT_PIC, a_sCertPic);
	}

	public void setCompany(String a_sCompany) {
		m_prefsWrite.putString(PREF_COMPANY, a_sCompany);
	}

	public void setResidence(String a_sResidence) {
		m_prefsWrite.putString(PREF_RESIDENCE, a_sResidence);
	}

	public void setJob(String a_sJob) {
		m_prefsWrite.putString(PREF_JOB, a_sJob);
	}

	public void setJobCert(String a_sJobCert) {
		m_prefsWrite.putString(PREF_JOB_CERT, a_sJobCert);
	}

	public void setAgreement(String a_sAgreement) {
		m_prefsWrite.putString(PREF_AGREEMENT, a_sAgreement);
	}

	public void setAgreed() {
		m_prefsWrite.putBoolean(PREF_AGREED, true);
	}

	public void setWatchFreeDays(int a_iWatchFreeDays) {
		m_prefsWrite.putInt(PREF_WATCH_FREE_DAYS, a_iWatchFreeDays);
	}

	public void setYanganFreeDays(int a_iYanganFreeDays) {
		m_prefsWrite.putInt(PREF_YANGAN_FREE_DAYS, a_iYanganFreeDays);
	}

	public void setRanqiFreeDays(int a_iRanqiFreeDays) {
		m_prefsWrite.putInt(PREF_RANQI_FREE_DAYS, a_iRanqiFreeDays);
	}

	public void setWatchPay(String a_sWatchPay) {
		m_prefsWrite.putString(PREF_WATCH_PAY, a_sWatchPay);
	}

	public void setYanganPay(String a_sYanganPay) {
		m_prefsWrite.putString(PREF_YANGAN_PAY, a_sYanganPay);
	}

	public void setRanqiPay(String a_sRanqiPay) {
		m_prefsWrite.putString(PREF_RANQI_PAY, a_sRanqiPay);
	}

	public void setUserBirthDesc(String a_sUserBirthDesc) {
		m_prefsWrite.putString(PREF_USER_BIRTH_DESC, a_sUserBirthDesc);
	}

	public void setSosStatus(boolean a_bSosStatus) {
		m_prefsWrite.putBoolean(PREF_SOS_STATUS, a_bSosStatus);
	}

	public void setFireStatus(boolean a_bFireStatus) {
		m_prefsWrite.putBoolean(PREF_FIRE_STATUS, a_bFireStatus);
	}

	public void setWatchNetStatus(boolean a_bWatchNetStatus) {
		m_prefsWrite.putBoolean(PREF_WATCH_NETSTATUS, a_bWatchNetStatus);
	}

	public void setFireNetstatus(boolean a_bFireNetstatus) {
		m_prefsWrite.putBoolean(PREF_FIRE_NETSTATUS, a_bFireNetstatus);
	}

	public void setHeartrateStatus(boolean a_bHeartrateStatus) {
		m_prefsWrite.putBoolean(PREF_HEARTRATE_STATUS, a_bHeartrateStatus);
	}

	public void setWatchbatteryStatus(boolean a_bWatchbatteryStatus) {
		m_prefsWrite.putBoolean(PREF_WATCHBATTERY_STATUS, a_bWatchbatteryStatus);
	}

	public void setFirebatteryStatus(boolean a_bFirebatteryStatus) {
		m_prefsWrite.putBoolean(PREF_FIREBATTERY_STATUS, a_bFirebatteryStatus);
	}

	public void setElecfenceSattus(boolean a_bElecfenceSattus) {
		m_prefsWrite.putBoolean(PREF_ELECFENCE_SATTUS, a_bElecfenceSattus);
	}

	public void setMorningStatus(boolean a_bMorningStatus) {
		m_prefsWrite.putBoolean(PREF_MORNING_STATUS, a_bMorningStatus);
	}

	public void setEveningStatus(boolean a_bEveningStatus) {
		m_prefsWrite.putBoolean(PREF_EVENING_STATUS, a_bEveningStatus);
	}

	public void setNewHeartRateCount(int a_iNewHeartRateCount) {
		m_prefsWrite.putInt(PREF_NEW_HEART_RATES, a_iNewHeartRateCount);
	}

	public void setInfoNotification(boolean a_bShowNoti) {
		m_prefsWrite.putBoolean(PREF_INFO_NOTIFICATION, a_bShowNoti);
	}

	public void setShowAgainManual(boolean a_bShowAgainManual) {
		m_prefsWrite.putBoolean(PREF_SHOW_AGAIN_MANUAL, a_bShowAgainManual);
	}
}
