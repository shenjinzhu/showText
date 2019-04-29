package com.show.entity;

import java.io.Serializable;

/**
 * 文件类
 * 
 * @author shenj
 *
 */
public class FileSave implements Serializable {

	private String id;
	private String name;
	private String time;
	private String size;
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "FileSave [id=" + id + ", name=" + name + ", time=" + time + ", size=" + size + ", url=" + url + "]";
	}

}
