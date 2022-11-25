package com.tarento.analytics.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Layout {
	private List<LayoutDetail> layoutConfig;

	public List<LayoutDetail> getLayoutConfig() {
		return layoutConfig;
	}

	public void setLayoutConfig(List<LayoutDetail> layoutConfig) {
		this.layoutConfig = layoutConfig;
	}
	
}
