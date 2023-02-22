//@formatter:off
package com.iot.volunteer.model;

import android.content.Context;

import com.iot.volunteer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemFence implements Serializable {
	public int						id;
	public String					name;
	public String					address;
	public String					lat;
	public String					lon;
	public int						radius;
	public String					strGuardTimeList;

	public ItemFence() {
		this.id = 0;
		this.name = null;
		this.address = null;
		this.lat = null;
		this.lon = null;
		this.radius = 300;
		this.strGuardTimeList = null;
	}

	public void  setFence(ItemFence itemFence) {
		this.id = itemFence.id;
		this.name = itemFence.name;
		this.address = itemFence.address;
		this.lat = itemFence.lat;
		this.lon = itemFence.lon;
		this.radius = itemFence.radius;
		this.strGuardTimeList = itemFence.strGuardTimeList;
	}

	public String getRadius(Context context) {
		return String.format(context.getResources().getString(R.string.str_radius_format), radius);
	}

	public ArrayList<ItemFencePeriod> getFencePeriodList() {
		ArrayList<ItemFencePeriod> itemFencePeriodArrayList = new ArrayList<>();
		if (strGuardTimeList != null) {
			String[] guardTimeArray = strGuardTimeList.split(",");
			for (String guardTimes : guardTimeArray) {
				String[] fencePeriod = guardTimes.split("-");
				ItemFencePeriod itemFencePeriod = new ItemFencePeriod(fencePeriod[0], fencePeriod[1]);
				itemFencePeriodArrayList.add(itemFencePeriod);
			}
		}

		return itemFencePeriodArrayList;
	}

	public void setFencePeriodList(ArrayList<ItemFencePeriod> itemFencePeriodArrayList) {
		if (itemFencePeriodArrayList == null || itemFencePeriodArrayList.size() == 0) {
			return;
		}

		strGuardTimeList = "";
		for (ItemFencePeriod itemFencePeriod : itemFencePeriodArrayList) {
			if (itemFencePeriod.getPeriodString() != null) {
				strGuardTimeList += itemFencePeriod.getPeriodString() + ",";
			}
		}
	}

	public JSONObject getJSONObject() {
		JSONObject fenceObj = new JSONObject();
		try {
			fenceObj.put("name", name);
			fenceObj.put("address", address);
			fenceObj.put("lat", lat);
			fenceObj.put("lon", lon);
			fenceObj.put("radius", radius);
			fenceObj.put("guardtime_list", strGuardTimeList);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return fenceObj;
	}

	public ItemFence(JSONObject fenceObj) {
		try {
			this.name = fenceObj.getString("name");
			this.address = fenceObj.getString("address");
			this.lat = fenceObj.getString("lat");
			this.lon = fenceObj.getString("lon");
			this.radius = fenceObj.getInt("radius");
			this.strGuardTimeList = fenceObj.getString("guardtime_list");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
