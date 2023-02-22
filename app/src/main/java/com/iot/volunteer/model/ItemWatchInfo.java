//@formatter:off
package com.iot.volunteer.model;

import java.io.Serializable;

public class ItemWatchInfo extends ItemDeviceInfo implements Serializable {

	public String		name = "";
	public String		phone = "";
	public int  		sex;
	public String		birthday = "";
	public int			tall;
	public int			weight;
	public String		blood = "";
	public String		ill_history = "";
	public String		lat = "";
	public String		lon = "";
	public String		province = "";
	public String		city = "";
	public String		district = "";
	public String		address = "";
	public int			chargeStatus;
	public String		sosContactName1 = "";
	public String		sosContactPhone1 = "";
	public String		sosContactName2 = "";
	public String		sosContactPhone2 = "";
	public String		sosContactName3 = "";
	public String		sosContactPhone3 = "";
	public int			lowRate = 60;
	public int			highRate = 100;
	public int			posUpdateMode = 1;

	public ItemWatchInfo() {

	}

	/*public ItemWatchInfo(int type, int id, String serial, boolean isManager, String serviceStartDate, String serviceEndDate, boolean netStatus, String name, String phone, int sex, String birthday, int tall,
						 int weight, String blood, String illHistory, String lat, String lon, String province, String city, String district, String address, int chargeStatus) {
		this.type = type;
		this.id = id;
		this.serial = serial;
		this.isManager = isManager;
		this.serviceStartDate = serviceStartDate;
		this.serviceEndDate = serviceEndDate;
		this.netStatus = netStatus;
		this.name = name;
		this.phone = phone;
		this.sex = sex;
		this.birthday = birthday;
		this.tall = tall;
		this.weight = weight;
		this.blood = blood;
		this.ill_history = illHistory;
		this.lat = lat;
		this.lon = lon;
		this.province = province;
		this.city = city;
		this.district = district;
		this.address = address;
		this.chargeStatus = chargeStatus;
	}*/
}
