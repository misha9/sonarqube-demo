package com.tarento.analytics.repository.rowmapper;
 
import java.sql.ResultSet;
import java.sql.SQLException;
 
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
 
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
 
public class SqlDataMapper {
 
	public static final Logger LOGGER = LoggerFactory.getLogger(SqlDataMapper.class);
 
	public class RoleDashboardMapper implements RowMapper<JsonNode> {
		public JsonNode mapRow(ResultSet rs, int rowNum) throws SQLException {
			ObjectNode node = JsonNodeFactory.instance.objectNode();
			node.put("id", rs.getString("dashboard_id")); 
			node.put("name", rs.getString("title")); 
			return node;
		}
	}
	
	public class VisualizationMapper implements RowMapper<JsonNode> {
		  public JsonNode mapRow(ResultSet rs, int rowNum) throws SQLException {
		   ObjectNode node = JsonNodeFactory.instance.objectNode();
		   node.put("id", rs.getString("id")); 
		   node.put("chartName", rs.getString("chart_name")); 
		   node.put("description", rs.getString("description")); 
		   
		   String queries = rs.getString("queries");
		   try {
			JsonNode queryNode = new ObjectMapper().readValue(queries, JsonNode.class);
			node.put("queries", queryNode); 
		} catch (JsonMappingException e) {
			LOGGER.error("Encountered an exception on Visualization Mapper : " + e.getMessage());
		} catch (JsonProcessingException e) {
			LOGGER.error("Encountered an exception on Visualization Mapper : " + e.getMessage());
		} 
		   node.put("filter", rs.getString("filter_keys")); 
		   node.put("chartType", rs.getString("chart_type")); 
		   node.put("resultType", rs.getString("result_type")); 
		   node.put("allowZero", rs.getBoolean("allow_zero")); 
		   node.put("valueType", rs.getString("value_type")); 
		   node.put("filters", rs.getString("filters")); 
		   node.put("enrichmentLookUp", rs.getString("enrichment_look_up")); 
		   node.put("limits", rs.getInt("limits")); 
		   node.put("isDecimal", rs.getBoolean("is_decimal")); 
		   node.put("isCumulative", rs.getBoolean("is_cumulative")); 
		   node.put("intervals", rs.getString("intervals")); 
		   node.put("alwaysView", rs.getString("always_view")); 
		   node.put("drillChart", rs.getString("drill_chart")); 
		   node.put("drillFields", rs.getString("drill_fields")); 
		   node.put("documentType", rs.getString("document_type")); 
		   node.put("actions", rs.getString("actions")); 
		   node.put("plotLabel", rs.getString("plot_label")); 
		   node.put("aggregationPaths", rs.getString("aggregation_paths")); 
		   node.put("pathDatatypeMapping", rs.getString("path_datatype_mapping")); 
		   node.put("controls", rs.getString("controls")); 
		   node.put("insight", rs.getString("insight")); 
		   node.put("comments", rs.getString("comments")); 
		   return node;
		  }
		 }
	
	public class DashboardConfigurationMapper implements RowMapper<JsonNode> {
		public JsonNode mapRow(ResultSet rs, int rowNum) throws SQLException {
			ObjectNode node = JsonNodeFactory.instance.objectNode();
			node.put("id", rs.getString("dashboard_id")); 
			node.put("name", rs.getString("title")); 
			node.put("style", rs.getString("style")); 
			node.put("isActive", rs.getBoolean("is_active")); 
			node.put("widgetTitle", rs.getString("widget_title")); 
			node.put("showFilters", rs.getString("show_filters")); 
			node.put("showWidgets", rs.getString("show_widgets"));  
			node.put("showWidgetTitle", rs.getString("show_widget_title")); 
			try {
				node.put("widgetCharts", new ObjectMapper().readTree(rs.getString("widget_charts")));
				node.put("filters", StringUtils.isNotBlank(rs.getString("filters")) ? new ObjectMapper().readTree(rs.getString("filters")): null);
				node.put("visualizations", new ObjectMapper().readTree(rs.getString("visualizations")));
			} catch (JsonMappingException e) {
				LOGGER.error("Encountered an Exception while Mapping Result Set to Json Node in Dashboard Configuration Mapper : " + e.getMessage());
			} catch (JsonProcessingException e) {
				LOGGER.error("Encountered an Exception while Mapping Result Set to Json Node in Dashboard Configuration Mapper : " + e.getMessage());
			} catch (SQLException e) {
				LOGGER.error("Encountered an Exception while Mapping Result Set to Json Node in Dashboard Configuration Mapper : " + e.getMessage());
			}
			
			return node;
		}
	}
 
	
 
}