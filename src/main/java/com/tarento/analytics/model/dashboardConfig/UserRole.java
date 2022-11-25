package com.tarento.analytics.model.dashboardConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRole {
	
	@JsonProperty("userId")
	private Integer userId;
	@JsonProperty("roleId")
	private Integer roleId;
	@JsonProperty("orgId")
	private Integer orgId;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public Integer getOrgId() {
		return orgId;
	}
	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

}
