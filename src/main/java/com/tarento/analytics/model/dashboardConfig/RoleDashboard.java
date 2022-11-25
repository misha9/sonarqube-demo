package com.tarento.analytics.model.dashboardConfig;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
 

public class RoleDashboard {
	
	@JsonProperty("roleId")
	private Integer roleId;
	@JsonProperty("dashboardId")
	private  List<String> dashboardId;
	@JsonProperty("orgId")
	private Integer orgId;
	
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public List<String> getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(List<String> dashboardId) {
		this.dashboardId = dashboardId;
	}
	public Integer getOrgId() {
		return orgId;
	}
	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	
	

	
}
