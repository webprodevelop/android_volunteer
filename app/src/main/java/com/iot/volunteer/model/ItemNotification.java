//@formatter:off
package com.iot.volunteer.model;

import java.io.Serializable;

public class ItemNotification implements Serializable {
	public final static String			TYPE_ALLOCK_TASK			= "create";
	public final static String			TYPE_FINISH_TASK			= "finish";
	public final static String			TYPE_CANCEL_TASK			= "cancel";

	public final static String			PUSH_TYPE_CHART 			= "chat";

	public static String 			NOTICE_TYPE_BIRTHDAY	= "birthday_msg";
	public static String 			NOTICE_TYPE_HEALTH 		= "health_msg";

	public int			id;
	public String		title;
	public long			time;
	public String		content;
	public String		taskId;
	public String			type;
	public int			isRead = 0;
	public String		taskStatus;
	public String		noticeType;


	public ItemNotification() {

	}

	public ItemNotification(int id, String title, String content, String taskId, long time, String type, String taskStatus, String noticeType) {
		this.id		= id;
		this.title = title;
		this.time = time;
		this.taskId = taskId;
		this.content = content;
		this.type = type;
		this.taskStatus = taskStatus;
		this.noticeType = noticeType;
		isRead = 0;
	}
}
