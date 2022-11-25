package com.tarento.analytics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@SuppressWarnings("all")
@Getter
@PropertySource(value = { "/application.properties" })
public class AppConfiguration {

	@Value("${spring.datasource.url}")
	private String springDataSourceUrl; 
	
	@Value("${spring.datasource.username}")
	private String springDataSourceUsername; 
	
	@Value("${spring.datasource.password}")
	private String springDataSourcePassword; 
	
	@Value("${spring.datasource.driver-class-name}")
	private String springDataSourceDriverClassName; 

	@Value("${services.esindexer.primary.host}")
	private String elasticHost;
	
	@Value("${services.user.read.host}")
	private String userReadHost; 
	
	@Value("${services.user.read.api}")
	private String userReadApi; 
	
	@Value("${services.user.read.api.key}")
	private String userReadApiKey; 
	
	@Value("${druid.service.default.startDate}")
	private String defaultStartDate;
	
	@Value("${druid.service.default.endDate}")
	private String defaultEndDate;
	
	public String getSpringDataSourceUrl() {
		return springDataSourceUrl;
	}

	public void setSpringDataSourceUrl(String springDataSourceUrl) {
		this.springDataSourceUrl = springDataSourceUrl;
	}

	public String getSpringDataSourceUsername() {
		return springDataSourceUsername;
	}

	public void setSpringDataSourceUsername(String springDataSourceUsername) {
		this.springDataSourceUsername = springDataSourceUsername;
	}

	public String getSpringDataSourcePassword() {
		return springDataSourcePassword;
	}

	public void setSpringDataSourcePassword(String springDataSourcePassword) {
		this.springDataSourcePassword = springDataSourcePassword;
	}

	public String getSpringDataSourceDriverClassName() {
		return springDataSourceDriverClassName;
	}

	public void setSpringDataSourceDriverClassName(String springDataSourceDriverClassName) {
		this.springDataSourceDriverClassName = springDataSourceDriverClassName;
	}

	public String getElasticHost() {
		return elasticHost;
	}

	public void setElasticHost(String elasticHost) {
		this.elasticHost = elasticHost;
	}

	public String getUserReadHost() {
		return userReadHost;
	}

	public void setUserReadHost(String userReadHost) {
		this.userReadHost = userReadHost;
	}

	public String getUserReadApi() {
		return userReadApi;
	}

	public void setUserReadApi(String userReadApi) {
		this.userReadApi = userReadApi;
	}

	public String getUserReadApiKey() {
		return userReadApiKey;
	}

	public void setUserReadApiKey(String userReadApiKey) {
		this.userReadApiKey = userReadApiKey;
	}

	public String getDefaultStartDate() {
		return defaultStartDate;
	}

	public void setDefaultStartDate(String defaultStartDate) {
		this.defaultStartDate = defaultStartDate;
	}

	public String getDefaultEndDate() {
		return defaultEndDate;
	}

	public void setDefaultEndDate(String defaultEndDate) {
		this.defaultEndDate = defaultEndDate;
	}
}
