//@formatter:off
package com.iot.volunteer.model;

import java.io.Serializable;

public class ItemRescue implements Serializable {
	public int			rescueId;
	public String		victim = "";
	public String  		rescueTime = "";
	public int  		status;
	public int			integral;
	public String		label = "";
	public String		contactPhone = "";
	public String		alarmContent = "";
	public String		alertType = "";
	public String		reportTime = "";
	public String		startTime = "";
	public String		deviceType;
	public String		deviceSerial;
	public String		endTime = "";
}
