package com.tarento.analytics.dto;

import java.util.List;

import com.tarento.analytics.model.dashboardConfig.DashboardData;

public class DashboardDto {
	private Long id; 
	private String title; 
	private Long timestamp; 
	private Boolean isPinned; 
	private String type; 
	private List<DashboardData> data; 
	
	
}
