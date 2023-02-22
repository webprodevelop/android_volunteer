//@formatter:off
package com.iot.volunteer.model;

import java.io.Serializable;

public class ItemCashHistory implements Serializable {
	public float		amount;
	public int			integral;
	public String		time;
	public String		status = "";

	public ItemCashHistory() {

	}

	public ItemCashHistory(float amount, int integral, String time, String status) {
		this.amount = amount;
		this.integral = integral;
		this.time = time;
		this.status = status;
	}
}
