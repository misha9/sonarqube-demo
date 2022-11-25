package com.tarento.analytics.model.dashboardConfig;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dashboards {

	@JsonProperty("id")
	private Long id;
	@JsonProperty("dashboardId")
	private String dashboardId;
	@JsonProperty("title")
	private String title;
	@JsonProperty("description")
	private String description;
	@JsonProperty("isActive")
	private Boolean isActive;
	@JsonProperty("style")
	private String style;
	@JsonProperty("widgetTitle")
	private String widgetTitle;
	@JsonProperty("showFilters")
	private Boolean showFilters;
	@JsonProperty("showWidgets")
	private Boolean showWidgets;
	@JsonProperty("showWidgetTitle")
	private Boolean showWidgetTitle;
	@JsonProperty("filters")
	private String filters;
	@JsonProperty("widgetCharts")
	private String widgetCharts;
	@JsonProperty("visualizations")
	private String visualizations;
	@JsonProperty("profile")
	private String profile;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(String dashboardId) {
		this.dashboardId = dashboardId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getWidgetTitle() {
		return widgetTitle;
	}
	public void setWidgetTitle(String widgetTitle) {
		this.widgetTitle = widgetTitle;
	}
	public Boolean getShowFilters() {
		return showFilters;
	}
	public void setShowFilters(Boolean showFilters) {
		this.showFilters = showFilters;
	}
	public Boolean getShowWidgets() {
		return showWidgets;
	}
	public void setShowWidgets(Boolean showWidgets) {
		this.showWidgets = showWidgets;
	}
	public Boolean getShowWidgetTitle() {
		return showWidgetTitle;
	}
	public void setShowWidgetTitle(Boolean showWidgetTitle) {
		this.showWidgetTitle = showWidgetTitle;
	}
	public String getFilters() {
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
	}
	public String getWidgetCharts() {
		return widgetCharts;
	}
	public void setWidgetCharts(String widgetCharts) {
		this.widgetCharts = widgetCharts;
	}
	public String getVisualizations() {
		return visualizations;
	}
	public void setVisualizations(String visualizations) {
		this.visualizations = visualizations;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	

}