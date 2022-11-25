package com.tarento.analytics.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tarento.analytics.dto.DataSourceRequest;
import com.tarento.analytics.dto.RoleDto;
import com.tarento.analytics.dto.UserDto;
import com.tarento.analytics.exception.AINException;
import com.tarento.analytics.org.service.ClientServiceFactory;
import com.tarento.analytics.producer.AnalyticsProducer;
import com.tarento.analytics.repository.RedisRepository;
import com.tarento.analytics.service.MetadataService;
import com.tarento.analytics.service.impl.RetryTemplate;
import com.tarento.analytics.utils.PathRoutes;
import com.tarento.analytics.utils.ResponseGenerator;

@RestController
@RequestMapping(PathRoutes.DashboardApi.METADATA_ROOT_PATH)
public class MetadataController {

	public static final Logger logger = LoggerFactory.getLogger(MetadataController.class);

	@Autowired
	private MetadataService metadataService;

	@Autowired
	private ClientServiceFactory clientServiceFactory;

	@Autowired
	private AnalyticsProducer analyticsProducer;

	@Autowired
	private RetryTemplate retryTemplate;

	@Autowired
	private RedisRepository redisRepository;

	private static final String MESSAGE = "message";

	@GetMapping(value = PathRoutes.DashboardApi.GET_DATASOURCE_CONFIG)
	public String getDataSourceConfiguration() throws AINException, IOException {
		DataSourceRequest dataSourceRequest = new DataSourceRequest(); 
		return ResponseGenerator.successResponse(metadataService.getDataSourceConfig(dataSourceRequest));
	}
	
	@PostMapping(value = PathRoutes.MetadataApi.SAVE_DASHBOARD_CONFIG)
	public String saveDashboardConfiguration(@PathVariable String profileName, @PathVariable String dashboardId,
			@RequestParam(value = "catagory", required = false) String catagory,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo) throws AINException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);
		user = new UserDto(); RoleDto role = new RoleDto(); role.setId(2068l); 
		List<RoleDto> roles = new ArrayList<>(); roles.add(role);
		user.setRoles(roles);
		logger.info("user {} ", xUserInfo);
		return ResponseGenerator.successResponse(
				metadataService.getDashboardConfiguration(profileName, dashboardId, catagory, user.getRoles()));
	}
	
	@PostMapping(value = PathRoutes.MetadataApi.GET_DASHBOARD_CONFIG)
	public String getDashboardConfiguration(@PathVariable String profileName, @PathVariable String dashboardId,
			@RequestParam(value = "catagory", required = false) String catagory,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo) throws AINException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);
		user = new UserDto(); RoleDto role = new RoleDto(); role.setId(2068l); 
		List<RoleDto> roles = new ArrayList<>(); roles.add(role);
		user.setRoles(roles);
		logger.info("user {} ", xUserInfo);
		return ResponseGenerator.successResponse(
				metadataService.getDashboardConfiguration(profileName, dashboardId, catagory, user.getRoles()));
	}

}
