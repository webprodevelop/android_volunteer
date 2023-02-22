//@formatter:off
package com.iot.volunteer.model;

import com.iot.volunteer.util.Util;

import java.util.Calendar;
import java.util.Date;

public class ItemHeartRate {
	public int			watchId;
	public String		checkDate;
	public long			checkTime;
	public int			heartRate;

	public ItemHeartRate() {

	}

	public ItemHeartRate(int watchId, long checkTime, int heartRate) {
		this.watchId = watchId;
		this.checkDate = Util.getDateFormatStringIgnoreLocale(new Date(checkTime));
		this.checkTime = checkTime;
		this.heartRate = heartRate;
	}

	public float getHourPercent() {
		float hourPercent = 0;

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(checkTime);
		hourPercent = calendar.get(Calendar.HOUR_OF_DAY) + (calendar.get(Calendar.MINUTE) / 60.f);

		return hourPercent;
	}

	public String getDateTimeSecString() {
		return Util.getDateTimeSecFormatString(new Date(checkTime));
	}
}
