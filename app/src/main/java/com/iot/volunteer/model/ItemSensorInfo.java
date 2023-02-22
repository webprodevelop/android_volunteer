//@formatter:off
package com.iot.volunteer.model;

import java.io.Serializable;

public class ItemSensorInfo extends ItemDeviceInfo implements Serializable {

	public final static String	TYPE_STR_FIRE_SENSOR	= "YG";
	public final static String	TYPE_STR_SMOKE_SENSOR	= "QG";

	public String		sensorType = "";
	public String		contactName = "";
	public String		contactPhone = "";
	public String		locationLabel = "";
	public String		lat = "";
	public String		lon = "";
	public String		province = "";
	public String		city = "";
	public String		district = "";
	public String		address = "";
	public boolean		batteryStatus;
	public boolean		alarmStatus;

	public ItemSensorInfo() {

	}

	public ItemSensorInfo(int type, int id, String serial, boolean isManager, String serviceStartDate, String serviceEndDate, boolean netStatus, String sensorType, String contactName, String contactPhone,
						  String locationLabel, String lat, String lon, String province, String city, String district, String address, boolean batteryStatus, boolean alarmStatus) {
		this.type = type;
		this.id = id;
		this.serial = serial;
		this.isManager = isManager;
		this.serviceStartDate = serviceStartDate;
		this.serviceEndDate = serviceEndDate;
		this.netStatus = netStatus;
		this.sensorType = sensorType;
		this.contactName = contactName;
		this.contactPhone = contactPhone;
		this.locationLabel = locationLabel;
		this.lat = lat;
		this.lon = lon;
		this.province = province;
		this.city = city;
		this.district = district;
		this.address = address;
		this.batteryStatus = batteryStatus;
		this.alarmStatus = alarmStatus;
	}
}
