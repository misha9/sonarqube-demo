package com.tarento.analytics.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Columns {
	private Long id;
	private Object data;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
