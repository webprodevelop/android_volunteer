//@formatter:off
package com.iot.volunteer.util;

public class AppConst {
	// Activity Request Code
	public static final int		REQUEST_PERMISSION_CAMERA	= 100;
	public static final int		REQUEST_PERMISSION_LOCATION	= 101;
	public static final int		REQUEST_PERMISSION_PHONE_STATE = 102;
	public static final int		REQUEST_PERMISSION_STORAGE = 103;

	// Activity Extra Param
	public static final String	EXTRA_APPKEY		= "appkey";
	public static final String	EXTRA_ACCESSTOKEN	= "accesstoken";
	public static final String	EXTRA_SN			= "sn";

	public static final String	ACTION_PUSH_RECEIVED		= "com.iot.volunteer.receiver.Push";
	public static final String	ACTION_ALARM				= "com.iot.volunteer.receiver.Alarm";
	public static final String	ACTION_PAY_RECEIVED			= "com.iot.volunteer.receiver.Pay";
	public static final String	ACTION_NOTICE_RECEIVED			= "com.iot.volunteer.receiver.Notice";

}
