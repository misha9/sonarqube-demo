package com.tarento.analytics.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.tarento.analytics.dto.DataSourceRequest;
import com.tarento.analytics.dto.RoleDto;
import com.tarento.analytics.exception.AINException;
import com.tarento.analytics.model.dashboardConfig.Dashboard;
import com.tarento.analytics.model.dashboardConfig.Dashboards;
import com.tarento.analytics.model.dashboardConfig.RoleDashboard;
import com.tarento.analytics.model.dashboardConfig.UserRole;
import com.tarento.analytics.model.dashboardConfig.Visualizations;

public interface MetadataService {

	public Object getDashboardConfiguration(String profileName, String dashboardId, String catagory, List<RoleDto> roleIds) throws AINException, IOException;
	public List<Dashboard> getDashboardsForProfile(String profileName, List<RoleDto> roleIds) throws AINException, IOException;
	public Object getReportsConfiguration(String profileName, String dashboardId, String catagory, List<RoleDto> roleIds) throws AINException, IOException;
	public Object getReportsForProfile(String profileName, List<RoleDto> roleIds) throws AINException, IOException;
	public List<String> getUserInfo(String authToken, String userId); 
	
	public Object getDataSourceConfig(DataSourceRequest dataSourceRequest); 
	public JsonNode postChart(Visualizations visualizations); 
	public JsonNode postDashboard(Dashboards dashboard); 
//	public JsonNode roleDashboard(RoleDashboard roleDashboard); 
	public JsonNode postUserRole(UserRole userRole);
	public JsonNode postRoleDashboard(RoleDashboard roleDashboard); 

}
