package com.tarento.analytics.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.analytics.dto.DataSourceRequest;
import com.tarento.analytics.dto.RoleDto;
import com.tarento.analytics.model.DataSource;
import com.tarento.analytics.model.dashboardConfig.Dashboard;
import com.tarento.analytics.model.dashboardConfig.Dashboards;
import com.tarento.analytics.model.dashboardConfig.RoleDashboard;
import com.tarento.analytics.model.dashboardConfig.UserRole;
import com.tarento.analytics.model.dashboardConfig.Visualizations;
import com.tarento.analytics.repository.rowmapper.DataSourceRowMapper;
import com.tarento.analytics.repository.rowmapper.SqlDataMapper;


@Repository
public class MetadataRepository {
	
	public static final Logger logger = LoggerFactory.getLogger(MetadataRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private QueryBuilder queryBuilder;
    
    @Autowired
    private DataSourceRowMapper dataSourceRowMapper;

    public List<DataSource> findForCriteria(final DataSourceRequest dataSourceRequest) {
        final Map<String, Object> preparedStatementValues = new HashMap<>();
        final String queryStr = queryBuilder.getQuery(dataSourceRequest, preparedStatementValues);
        final List<DataSource> dataSources = jdbcTemplate.query(queryStr,  new Object[] { dataSourceRequest.getId() }, dataSourceRowMapper);
        return dataSources;
    }
    
    public List<JsonNode> getDashboardsForProfile(String profile, String roleName) {
    	logger.info("Profile : " + profile);
    	logger.info("Role Name : " + roleName);
    	List<JsonNode> dashboards = new ArrayList<JsonNode>();
		try {
			dashboards = jdbcTemplate.query(Sql.ConfigQueries.GET_DASHBOARDS_FOR_PROFILE, new Object[] { profile, roleName },
					new SqlDataMapper().new RoleDashboardMapper());
		} catch (Exception e) {
			logger.error("Encountered an Exception while fetching all the Dashboards for the Profile on Role ID " + e);
		}
		return dashboards;
    	
    }
    
    public JsonNode getVisualizationByCode(String visualizationCode) {
    	logger.info("Chart ID : " + visualizationCode);
    	List<JsonNode> visualizations = new ArrayList<JsonNode>();
		try {
			visualizations = jdbcTemplate.query(Sql.ConfigQueries.GET_VISUALIZATION_BY_CODE, new Object[] { visualizationCode },
					new SqlDataMapper().new VisualizationMapper());
		} catch (Exception e) {
			logger.error("Encountered an Exception while fetching all the Dashboards for the Profile on Role ID " + e);
		}
		if(visualizations.size() == 1) { 
			return visualizations.get(0); 
		} else { 
			return null; 
		}
    	
    }
    
    public List<JsonNode> getDashboardConfiguration(String profileName, String dashboardId) {
    	List<JsonNode> dashboardConfigs = new ArrayList<JsonNode>();
		try {
			dashboardConfigs = jdbcTemplate.query(Sql.ConfigQueries.GET_DASHBOARD_CONFIG, new Object[] {dashboardId , profileName },
					new SqlDataMapper().new DashboardConfigurationMapper());
		} catch (Exception e) {
			logger.error("Encountered an Exception while fetching all the Dashboards for the Profile on Role ID " + e);
		}
		return dashboardConfigs;
    	
    }
    
    public JsonNode postVisualizations(Visualizations visualizations) {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = con.prepareStatement(Sql.ConfigQueries.SAVE_VISUALIZATIONS, returnValColumn);
					statement.setString(1, visualizations.getVisualizationCode());
					statement.setString(2, visualizations.getChartName());
					statement.setString(3, visualizations.getChartType());
					statement.setBoolean(4, visualizations.getAllowZero());
					statement.setString(5, visualizations.getValueType());
					statement.setBoolean(6, visualizations.getIsDecimal());
					statement.setString(7, visualizations.getDrillChart());
					statement.setString(8, visualizations.getDocumentType());
					statement.setString(9, visualizations.getActions());
					statement.setInt(10, visualizations.getLimits()!=null ? visualizations.getLimits() : 0);
					statement.setString(11, visualizations.getFilters());
					statement.setString(12, visualizations.getComments());
					return statement;
				}
			}, keyHolder);
			Long id = keyHolder.getKey().longValue();
			visualizations.setId(id);
		} catch (Exception e) {
			logger.error("Encountered an exception while saving a new visualization :  " + e);
		}
		return new ObjectMapper().convertValue(visualizations, JsonNode.class);
    	
    }
    
    public JsonNode postDashboards(Dashboards dashboard) {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = con.prepareStatement(Sql.ConfigQueries.SAVE_DASHBOARDS, returnValColumn);
					statement.setString(1, dashboard.getDashboardId());
					statement.setString(2, dashboard.getTitle());
					statement.setString(3, dashboard.getDescription());
					statement.setBoolean(4, dashboard.getIsActive());
					statement.setString(5, dashboard.getStyle());
					statement.setString(6, dashboard.getWidgetTitle());
					statement.setBoolean(7, dashboard.getShowFilters());
					statement.setBoolean(8, dashboard.getShowWidgets());
					statement.setBoolean(9, dashboard.getShowWidgetTitle());
					statement.setString(10, dashboard.getFilters());
					statement.setString(11, dashboard.getWidgetCharts());
					statement.setString(12, dashboard.getVisualizations());
					statement.setString(13, dashboard.getProfile());
					return statement;
				}
			}, keyHolder);
			Long id = keyHolder.getKey().longValue();
			dashboard.setId(id);
		} catch (Exception e) {
			logger.error("Encountered an exception while saving a new dashboard :  " + e);
		}
		return new ObjectMapper().convertValue(dashboard, JsonNode.class);
    	
    }
    
    //new
    //
    
    public JsonNode postUserRoleMappings(UserRole userRole) {
//		try {
//			KeyHolder keyHolder = new GeneratedKeyHolder();
//			jdbcTemplate.update(new PreparedStatementCreator() {
//				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//					String[] returnValColumn = new String[] { "id" };
//					PreparedStatement statement = con.prepareStatement(Sql.ConfigQueries.SAVE_DASHBOARDS, returnValColumn);
//					statement.setString(1, userRole.getDashboardId());
//					statement.setString(2, userRole.getTitle());
//					statement.setString(3, userRole.getDescription());
//					return statement;
//				}
//			}, keyHolder);
//			Integer id = keyHolder.getKey();
//			roleDashboard.setRoleId(id);
//		} catch (Exception e) {
//			logger.error("Encountered an exception while saving a new user role mapping :  " + e);
//		}
//		return new ObjectMapper().convertValue(dashboard, JsonNode.class);
    	return null;
    	
    }

    
    public JsonNode postRoleDashboards(RoleDashboard roleDashboard) {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				PreparedStatement statement;
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					for (int i = 0; i < roleDashboard.getDashboardId().size(); i++) {					
						statement = con.prepareStatement(Sql.ConfigQueries.SAVE_ROLE_DASHBOARDS, returnValColumn);
						statement.setInt(1, roleDashboard.getRoleId());
						statement.setString(2, roleDashboard.getDashboardId().get(i));
						statement.setInt(3, roleDashboard.getOrgId());
					}
					return statement;
				}
			});
			Integer id = (Integer) keyHolder.getKey();
//			roleDashboard.setRoleId(id);
		} catch (Exception e) {
			logger.error("Encountered an exception while saving a new role dashboard :  " + e);
		}
		return new ObjectMapper().convertValue(roleDashboard, JsonNode.class);
    	
    }

    public Boolean updateQueriesForVisualization(String query, String visualizationCode) {
		try {
			int result = jdbcTemplate.update(Sql.ConfigQueries.UPDATE_QUERIES, new Object[] { query, visualizationCode });
			if(result > 0) 
				return Boolean.TRUE;
			else 
				return Boolean.FALSE; 
		} catch (Exception e) {
			logger.error("Encountered an exception while updating query for Visualization : " + e);
		}
		return Boolean.FALSE;
	}
    
    public Boolean updateFiltersForDashboard(String filters, String dashboardId) {
		try {
			int result = jdbcTemplate.update(Sql.ConfigQueries.UPDATE_FILTERS, new Object[] { filters, dashboardId });
			if(result > 0) 
				return Boolean.TRUE;
			else 
				return Boolean.FALSE; 
		} catch (Exception e) {
			logger.error("Encountered an exception while updating filters for Dashboard : " + e);
		}
		return Boolean.FALSE;
	}
    
    public Boolean updateWidgetChartsForDashboard(String widgetCharts, String dashboardId) {
		try {
			int result = jdbcTemplate.update(Sql.ConfigQueries.UPDATE_WIDGET_CHARTS, new Object[] { widgetCharts, dashboardId });
			if(result > 0) 
				return Boolean.TRUE;
			else 
				return Boolean.FALSE; 
		} catch (Exception e) {
			logger.error("Encountered an exception while updating widget charts for Dashboard : " + e);
		}
		return Boolean.FALSE;
	}
    
    public Boolean updateVisualizationsForDashboard(String visualizations, String dashboardId) {
		try {
			int result = jdbcTemplate.update(Sql.ConfigQueries.UPDATE_VISUALIZATIONS, new Object[] { visualizations, dashboardId });
			if(result > 0) 
				return Boolean.TRUE;
			else 
				return Boolean.FALSE; 
		} catch (Exception e) {
			logger.error("Encountered an exception while updating visualizations for Dashboard : " + e);
		}
		return Boolean.FALSE;
	}
    
    public Boolean updateAggregationPathForVisualization(String aggregationPath, String visualizationCode) {
		try {
			int result = jdbcTemplate.update(Sql.ConfigQueries.UPDATE_AGGREGATION_PATH, new Object[] { aggregationPath, visualizationCode });
			if(result > 0) 
				return Boolean.TRUE;
			else 
				return Boolean.FALSE; 
		} catch (Exception e) {
			logger.error("Encountered an exception while updating query for Visualization : " + e);
		}
		return Boolean.FALSE;
	}
}

