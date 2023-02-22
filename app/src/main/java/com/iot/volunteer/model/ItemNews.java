//@formatter:off
package com.iot.volunteer.model;

import java.io.Serializable;

public class ItemNews implements Serializable {
	public int			id;
	public String		createTime;
	public String		updateTime;
	public String		releaseTime;
	public String		status;
	public String		title;
	public String		content;
	public String		picture;
	public String		newsType;
	public String		publishTo;
	public String		newsBranch;
	public int			readCnt = 0;

	public ItemNews() {

	}

	public ItemNews(int id, String createTime, String updateTime, String releaseTime, String status, String title, String content, String picture, String newsType, String publishTo, String newsBranch) {
		this.id = id;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.releaseTime = releaseTime;
		this.status = status;
		this.title = title;
		this.content = content;
		this.picture = picture;
		this.newsType = newsType;
		this.publishTo = publishTo;
		this.newsBranch = newsBranch;
	}
}
