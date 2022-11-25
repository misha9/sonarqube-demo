package com.tarento.analytics.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigDetail {
	private String config; 
	private Map<String, Object> configMap;
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	public Map<String, Object> getConfigMap() {
		return configMap;
	}
	public void setConfigMap(Map<String, Object> configMap) {
		this.configMap = configMap;
	} 

	
}
