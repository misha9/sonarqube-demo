package com.tarento.analytics.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tarento.analytics.constant.Constants;
import com.tarento.analytics.constant.ErrorCode;
import com.tarento.analytics.dto.AggregateRequestDto;
import com.tarento.analytics.dto.RequestDto;
import com.tarento.analytics.dto.RoleDto;
import com.tarento.analytics.dto.UserDto;
import com.tarento.analytics.exception.AINException;
import com.tarento.analytics.model.ConfigDetail;
import com.tarento.analytics.model.Item;
import com.tarento.analytics.model.dashboardConfig.Dashboard;
import com.tarento.analytics.model.dashboardConfig.Dashboards;
import com.tarento.analytics.model.dashboardConfig.RoleDashboard;
import com.tarento.analytics.model.dashboardConfig.UserRole;
import com.tarento.analytics.model.dashboardConfig.Visualizations;
import com.tarento.analytics.org.service.ClientServiceFactory;
import com.tarento.analytics.org.service.TarentoServiceImpl;
import com.tarento.analytics.producer.AnalyticsProducer;
import com.tarento.analytics.repository.RedisRepository;
import com.tarento.analytics.service.MetadataService;
import com.tarento.analytics.service.impl.RetryTemplate;
import com.tarento.analytics.utils.PathRoutes;
import com.tarento.analytics.utils.ResponseGenerator;

@RestController
@RequestMapping(PathRoutes.DashboardApi.DASHBOARD_ROOT_PATH)
public class DashboardController {

	public static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    TarentoServiceImpl tarentoServiceImpl;

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

	@GetMapping(value = PathRoutes.DashboardApi.TEST_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getTest() throws JsonProcessingException {
		return ResponseGenerator.successResponse("success");

	}
	
	@PostMapping(value = "/item")
	public String addItem(@RequestBody Item item) {
		redisRepository.save(item);
		return item.toString();
	}
	
	@GetMapping(value = "/item")
	public List<String> getItem(@RequestParam(value = "id", required = false) String id) {
		return redisRepository.findAll(); 
	}

	@GetMapping(value = PathRoutes.DashboardApi.GET_DASHBOARD_CONFIG + "/{profileName}" + "/{dashboardId}")
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
	
	@GetMapping(value = PathRoutes.DashboardApi.GET_REPORTS_CONFIG + "/{profileName}" + "/{dashboardId}")
	public String getReportsConfiguration(@PathVariable String profileName, @PathVariable String dashboardId,
			@RequestParam(value = "catagory", required = false) String catagory,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo) throws AINException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);
		
		  /*UserDto user = new UserDto(); 
		  RoleDto role = new RoleDto(); 
		  role.setId(2068l);
		  List<RoleDto> roles = new ArrayList<>(); 
		  roles.add(role);
		  user.setRoles(roles);*/
		ConfigDetail detail = new ConfigDetail(); 
		detail.getConfig(); 
		 
		logger.info("user {} ", xUserInfo);
		return ResponseGenerator.successResponse(
				metadataService.getReportsConfiguration(profileName, dashboardId, catagory, user.getRoles()));
	}

	@GetMapping(value = PathRoutes.DashboardApi.GET_DASHBOARDS_FOR_PROFILE + "/{profileName}")
	public String getDashboardsForProfile(@PathVariable String profileName,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo,
			@RequestHeader(value = "X-Channel-Id", required = false) String channelId,
			@RequestHeader(value = "x-authenticated-userid", required = false) String fullUserId,
			@RequestHeader(value = "x-authenticated-user-token", required = false) String userToken) throws AINException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);
		user = new UserDto(); RoleDto role = new RoleDto(); role.setId(2068l);role.setName("Tarento Admin");
		RoleDto role2 = new RoleDto(); role2.setId(2069l);role2.setName("PUBLIC");
		List<RoleDto> roles = new ArrayList<>(); roles.add(role);roles.add(role2);
		user.setRoles(roles);
		logger.info("user {} ", xUserInfo);
		/*
		 * Gson gson = new GsonBuilder().setPrettyPrinting().create(); UserDto user =
		 * gson.fromJson(xUserInfo, UserDto.class);
		 */
		return ResponseGenerator.successResponse(metadataService.getDashboardsForProfile(profileName, user.getRoles()));
	}
	
	@GetMapping(value = PathRoutes.DashboardApi.GET_REPORTS_FOR_PROFILE + "/{profileName}")
	public String getReportsForProfile(@PathVariable String profileName,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo) throws AINException, IOException {
		/*Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);*/
		
		UserDto user = new UserDto(); 
		  RoleDto role = new RoleDto(); 
		  role.setId(2068l);
		  List<RoleDto> roles = new ArrayList<>(); 
		  roles.add(role);
		  user.setRoles(roles);
		return ResponseGenerator.successResponse(metadataService.getReportsForProfile(profileName, user.getRoles()));
	}

	@PostMapping(value = PathRoutes.DashboardApi.GET_CHART_V2 + "/{profileName}")
	public String getVisualizationChartV2(@PathVariable String profileName, @RequestBody RequestDto requestDto,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo,
			@RequestHeader(value = "Authorization", required = false) String authorization, ServletWebRequest request) throws IOException {

		// Getting the request information only from the Full Request
		AggregateRequestDto requestInfo = requestDto.getAggregationRequestDto();
		Map<String, Object> headers = requestDto.getHeaders();
		// requestInfo.getFilters().putAll(headers);
		String response = "";
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);
		user = new UserDto(); RoleDto role = new RoleDto(); role.setId(2068l);role.setName("Tarento Admin");
		List<RoleDto> roles = new ArrayList<>(); roles.add(role);
		user.setRoles(roles);
		logger.info("user {} ", xUserInfo);
		try {
			if (requestDto.getAggregationRequestDto() == null) {
				logger.error("Please provide requested Visualization Details");
				throw new AINException(ErrorCode.ERR320, "Visualization Request is missing");
			}
//			AggregateDto responseData = tarentoServiceImpl.getAggregatedData(profileName, requestInfo, user.getRoles());
//				
			Object responseData = clientServiceFactory.get(profileName, requestInfo.getVisualizationCode())
					.getAggregatedData(profileName, requestInfo, user.getRoles());
			// clientService.getAggregatedData(requestInfo, user.getRoles());
			response = ResponseGenerator.successResponse(responseData);
			// Commenting the User Request Data Push - This was earlier used for Analytics of Users and the Dashboards that they were consuming. 
			/*
			 * Long responseTime = new Date().getTime();
			 * pushRequestsToLoggers(requestDto, user, requestTime, responseTime);
			 */
		} catch (AINException e) {
			logger.error("error while executing api getVisualizationChart");
			response = ResponseGenerator.failureResponse(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
			logger.error("error while executing api getVisualizationChart {} ", e.getMessage());
		}
		return response;
	}
	
	@PostMapping(value = PathRoutes.DashboardApi.POST_CHART)
	public JsonNode postVisualizationChart(@RequestBody Visualizations visualizations,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo,
			@RequestHeader(value = "Authorization", required = false) String authorization, ServletWebRequest request) throws IOException {

		JsonNode response = null ;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);
		user = new UserDto(); RoleDto role = new RoleDto(); role.setId(2068l);role.setName("Tarento Admin");
		List<RoleDto> roles = new ArrayList<>(); roles.add(role);
		user.setRoles(roles);
		logger.info("user {} ", xUserInfo);
		try {
			response = metadataService.postChart(visualizations); 
		} catch (Exception e) {
			logger.error("error while executing api getVisualizationChart {} ", e.getMessage());
		}
		return response;
	}
	
	@PostMapping(value = PathRoutes.DashboardApi.POST_DASHBOARD)
	public JsonNode postDashboardConfig(@RequestBody Dashboards dashboard,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo,
			@RequestHeader(value = "Authorization", required = false) String authorization, ServletWebRequest request) throws IOException {

		JsonNode response = null ;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);
		user = new UserDto(); RoleDto role = new RoleDto(); role.setId(2068l);role.setName("Tarento Admin");
		List<RoleDto> roles = new ArrayList<>(); roles.add(role);
		user.setRoles(roles);
		logger.info("user {} ", xUserInfo);
		try {
			response = metadataService.postDashboard(dashboard); 
		} catch (Exception e) {
			logger.error("error while executing api postDashboardConfig {} ", e.getMessage());
		}
		return response;
	}
	
	private UserDto buildUserObject(String xUserInfo, String userId, List<String> roleList) { 
		/*Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);*/
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);
		List<RoleDto> roles = new ArrayList<>();
		user = new UserDto(); 
		user.setUserName(userId);
		for(String roleName : roleList) { 
			RoleDto role = new RoleDto(); 
			role.setId(2068l);
			role.setName(roleName);
			roles.add(role);
		}
		user.setRoles(roles);
		logger.info("user {} ", xUserInfo);
		return user; 
	}
	
	@PostMapping(value = PathRoutes.DashboardApi.GET_REPORT)
	public String getReport(@PathVariable String profileName, @RequestBody RequestDto requestDto,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo,
			@RequestHeader(value = "Authorization", required = false) String authorization, ServletWebRequest request)
			throws IOException {
		/*
		 * logger.info("Request Detail:" + requestDto); Gson gson = new
		 * GsonBuilder().setPrettyPrinting().create(); UserDto user =
		 * gson.fromJson(xUserInfo, UserDto.class);
		 */
		UserDto user = new UserDto();
		logger.info("user {} ", xUserInfo);

		// Getting the request information only from the Full Request
		AggregateRequestDto requestInfo = requestDto.getAggregationRequestDto();
		Map<String, Object> headers = requestDto.getHeaders();
		// requestInfo.getFilters().putAll(headers);
		String response = "";
		try {
			if (headers.isEmpty()) {
				logger.error("Please provide header details");
				throw new AINException(ErrorCode.ERR320, "header is missing");
			}
			if (headers.get("tenantId") == null) {
				logger.error("Please provide tenant ID details");
				throw new AINException(ErrorCode.ERR320, "tenant is missing");

			}
			if (requestDto.getAggregationRequestDto() == null) {
				logger.error("Please provide requested Visualization Details");
				throw new AINException(ErrorCode.ERR320, "Visualization Request is missing");
			}

			// To be removed once the development is complete
			if (StringUtils.isBlank(requestInfo.getModuleLevel())) {
				requestInfo.setModuleLevel(Constants.Modules.HOME_REVENUE);
			}
			Object responseData = clientServiceFactory.get(profileName, requestInfo.getVisualizationCode())
					.getAggregatedData(profileName, requestInfo, user.getRoles());
			// clientService.getAggregatedData(requestInfo, user.getRoles());
			response = ResponseGenerator.successResponse(responseData);
		} catch (AINException e) {
			logger.error("error while executing api getVisualizationChart");
			response = ResponseGenerator.failureResponse(e.getErrorCode(), e.getErrorMessage());
		} catch (Exception e) {
			logger.error("error while executing api getVisualizationChart {} ", e.getMessage());
			// could be bad request or internal server error
			// response =
			// ResponseGenerator.failureResponse(HttpStatus.BAD_REQUEST.toString(),"Bad
			// request");
		}
		return response; 	
	}
	
	//new
	//
	//	
	@PostMapping(value = PathRoutes.DashboardApi.POST_USER_ROLE)
	public JsonNode postUserRoleMapping(@RequestBody UserRole userRole,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo,
			@RequestHeader(value = "Authorization", required = false) String authorization, ServletWebRequest request) throws IOException {

		JsonNode response = null ;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		UserDto user = gson.fromJson(xUserInfo, UserDto.class);
		user = new UserDto(); RoleDto role = new RoleDto(); role.setId(2068l);role.setName("Tarento Admin");
		List<RoleDto> roles = new ArrayList<>(); roles.add(role);
		user.setRoles(roles);
		logger.info("user {} ", xUserInfo);
		try {
			response = metadataService.postUserRole(userRole); 
		} catch (Exception e) {
			logger.error("error while executing api postUserRoleMapping {} ", e.getMessage());
		}
		return response;
	}	
	
	//new
	//
	//	
	@PostMapping(value = PathRoutes.DashboardApi.POST_ROLE_DASHBOARD)
	public JsonNode postRoleDashboardConfig(@RequestBody RoleDashboard roleDashboard,
			@RequestHeader(value = "x-user-info", required = false) String xUserInfo,
			@RequestHeader(value = "Authorization", required = false) String authorization, ServletWebRequest request) throws IOException {

		JsonNode response = null ;
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		UserDto user = gson.fromJson(xUserInfo, UserDto.class);
//		user = new UserDto(); RoleDto role = new RoleDto(); role.setId(2068l);role.setName("Tarento Admin");
//		List<RoleDto> roles = new ArrayList<>(); roles.add(role);
//		user.setRoles(roles);
//		logger.info("user {} ", xUserInfo);
		try {
			response = metadataService.postRoleDashboard(roleDashboard); 
		} catch (Exception e) {
			logger.error("error while executing api postRoleDashboardConfig {} ", e.getMessage());
		}
		return response;
	}

}
