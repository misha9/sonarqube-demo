package com.tarento.analytics.model.dashboardConfig;
 
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
 
@JsonInclude(JsonInclude.Include.NON_NULL)
 
public class Visualizations {
 
	@JsonProperty("id")
	private Long id;
	@JsonProperty("chartName")
	private String chartName;
	@JsonProperty("description")
	private String description;
	@JsonProperty("queries")
	private Object queries; 
	@JsonProperty("filterKeys")
	private String filterKeys;
	@JsonProperty("chartType")
	private String chartType;
	@JsonProperty("resultType")
	private String resultType;
	@JsonProperty("allowZero")
	private Boolean allowZero; 
	@JsonProperty("valueType")
	private String valueType;
	@JsonProperty("filters")
	private String filters;
	@JsonProperty("enrichmentLookUp")
	private String enrichmentLookUp;
	@JsonProperty("limits")
	private Integer limits; 	
	@JsonProperty("isDecimal")
	private Boolean isDecimal;
	@JsonProperty("isCumulative")
	private Boolean isCumulative;
	@JsonProperty("intervals")
	private String intervals;
	@JsonProperty("alwaysView")
	private String alwaysView;
	@JsonProperty("drillChart")
	private String drillChart; 
	@JsonProperty("drillFields")
	private String drillFields; 
	@JsonProperty("documentType")
	private String documentType; 
	@JsonProperty("actions")
	private String actions; 
	@JsonProperty("plotLabel")
	private String plotLabel; 
	@JsonProperty("aggregationPaths")
	private String aggregationPaths; 
	@JsonProperty("pathDatatypeMapping")
	private String pathDatatypeMapping; 
	@JsonProperty("controls")
	private String controls; 
	@JsonProperty("insight")
	private String insight; 
	@JsonProperty("comments")
	private String comments;
	@JsonProperty("profileName")
	private String profileName;
	@JsonProperty("visualizationCode")
	private String visualizationCode;

	public String getVisualizationCode() {
		return visualizationCode;
	}
	public void setVisualizationCode(String visualizationCode) {
		this.visualizationCode = visualizationCode;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getChartName() {
		return chartName;
	}
	public void setChartName(String chartName) {
		this.chartName = chartName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Object getQueries() {
		return queries;
	}
	public void setQueries(Object queries) {
		this.queries = queries;
	}
	public String getFilterKeys() {
		return filterKeys;
	}
	public void setFilterKeys(String filterKeys) {
		this.filterKeys = filterKeys;
	}
	public String getChartType() {
		return chartType;
	}
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public Boolean getAllowZero() {
		return allowZero;
	}
	public void setAllowZero(Boolean allowZero) {
		this.allowZero = allowZero;
	}
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	public String getFilters() {
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
	}
	public String getEnrichmentLookUp() {
		return enrichmentLookUp;
	}
	public void setEnrichmentLookUp(String enrichmentLookUp) {
		this.enrichmentLookUp = enrichmentLookUp;
	}
	public Integer getLimits() {
		return limits;
	}
	public void setLimits(Integer limits) {
		this.limits = limits;
	}
	public Boolean getIsDecimal() {
		return isDecimal;
	}
	public void setIsDecimal(Boolean isDecimal) {
		this.isDecimal = isDecimal;
	}
	public Boolean getIsCumulative() {
		return isCumulative;
	}
	public void setIsCumulative(Boolean isCumulative) {
		this.isCumulative = isCumulative;
	}
	public String getIntervals() {
		return intervals;
	}
	public void setIntervals(String intervals) {
		this.intervals = intervals;
	}
	public String getAlwaysView() {
		return alwaysView;
	}
	public void setAlwaysView(String alwaysView) {
		this.alwaysView = alwaysView;
	}
	public String getDrillChart() {
		return drillChart;
	}
	public void setDrillChart(String drillChart) {
		this.drillChart = drillChart;
	}
	public String getDrillFields() {
		return drillFields;
	}
	public void setDrillFields(String drillFields) {
		this.drillFields = drillFields;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getActions() {
		return actions;
	}
	public void setActions(String actions) {
		this.actions = actions;
	}
	public String getPlotLabel() {
		return plotLabel;
	}
	public void setPlotLabel(String plotLabel) {
		this.plotLabel = plotLabel;
	}
	public String getAggregationPaths() {
		return aggregationPaths;
	}
	public void setAggregationPaths(String aggregationPaths) {
		this.aggregationPaths = aggregationPaths;
	}
	public String getPathDatatypeMapping() {
		return pathDatatypeMapping;
	}
	public void setPathDatatypeMapping(String pathDatatypeMapping) {
		this.pathDatatypeMapping = pathDatatypeMapping;
	}
	public String getControls() {
		return controls;
	}
	public void setControls(String controls) {
		this.controls = controls;
	}
	public String getInsight() {
		return insight;
	}
	public void setInsight(String insight) {
		this.insight = insight;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	