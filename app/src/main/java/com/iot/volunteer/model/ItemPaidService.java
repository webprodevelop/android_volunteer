//@formatter:off
package com.iot.volunteer.model;

import java.io.Serializable;

public class ItemPaidService implements Serializable {
	public int			orderId;
	public int			type;
	public int  		deviceId;
	public String  		userPhone;
	public int			amount;
	public int			payType;
	public int			serviceYears;
	public String		serviceStartDate = "";
	public String		serviceEndDate = "";
}
