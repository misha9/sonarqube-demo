package com.tarento.analytics.org.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tarento.analytics.ConfigurationLoader;
import com.tarento.analytics.actions.ActionFactory;
import com.tarento.analytics.actions.ActionHelper;
import com.tarento.analytics.config.AppConfiguration;
import com.tarento.analytics.constant.Constants;
import com.tarento.analytics.dto.AggregateDto;
import com.tarento.analytics.dto.AggregateRequestDto;
import com.tarento.analytics.dto.CummulativeDataRequestDto;
import com.tarento.analytics.dto.DashboardHeaderDto;
import com.tarento.analytics.dto.Data;
import com.tarento.analytics.dto.RoleDto;
import com.tarento.analytics.enums.AlwaysView;
import com.tarento.analytics.enums.ChartType;
import com.tarento.analytics.exception.AINException;
import com.tarento.analytics.handler.IResponseHandler;
import com.tarento.analytics.handler.InsightsHandler;
import com.tarento.analytics.handler.InsightsHandlerFactory;
import com.tarento.analytics.handler.ResponseHandlerFactory;
import com.tarento.analytics.model.InsightsConfiguration;
import com.tarento.analytics.repository.MetadataRepository;
import com.tarento.analytics.repository.RedisRepository;
import com.tarento.analytics.service.MetadataService;
import com.tarento.analytics.service.impl.RestService;

@Component
public class TarentoServiceImpl implements ClientService {

	public static final Logger logger = LoggerFactory.getLogger(TarentoServiceImpl.class);
	ObjectMapper mapper = new ObjectMapper();
	char insightPrefix = 'i';

	@Autowired
	private RestService restService;

	@Autowired
	private ConfigurationLoader configurationLoader;

	@Autowired
	private ResponseHandlerFactory responseHandlerFactory;

	@Autowired
	private InsightsHandlerFactory insightsHandlerFactory;
	
	@Autowired
	private ActionFactory actionFactory;
	
	@Autowired
	private QueryServiceFactory queryServiceFactory; 
	
	@Autowired
	private RedisRepository redisRepository; 
	
	@Autowired
	private AppConfiguration appConfig; 
	
	@Autowired
	private MetadataRepository metadataRepository; 

	@Override
	public AggregateDto getAggregatedData(String profileName, AggregateRequestDto request, List<RoleDto> roles)
			throws AINException, IOException {
		// Read visualization Code
		String internalChartId = request.getVisualizationCode();
		ObjectNode aggrObjectNode = JsonNodeFactory.instance.objectNode();
		ObjectNode insightAggrObjectNode = JsonNodeFactory.instance.objectNode();
		ObjectNode nodes = JsonNodeFactory.instance.objectNode();
		ObjectNode insightNodes = JsonNodeFactory.instance.objectNode();
		Boolean continueWithInsight = Boolean.FALSE;
		// Load Chart API configuration to Object Node for easy retrieval later
		
		ObjectNode chartNode =  (ObjectNode) metadataRepository.getVisualizationByCode(internalChartId);

//		ObjectNode chartNode = (ObjectNode) node.get(internalChartId);
		InsightsConfiguration insightsConfig = null;
		if (chartNode.get(Constants.JsonPaths.INSIGHT) != null) {
			try { 
				insightsConfig = mapper.treeToValue(chartNode.get(Constants.JsonPaths.INSIGHT),InsightsConfiguration.class);
			} catch (Exception e) { 
				logger.error("Encountered an Exception while fetching the Insights value : "+ e.getMessage());
			}
					
		}
		ChartType chartType = ChartType.fromValue(chartNode.get(Constants.JsonPaths.CHART_TYPE).asText());
		boolean isDefaultPresent = chartType.equals(ChartType.LINE)
				&& chartNode.get(Constants.JsonPaths.INTERVAL) != null;
		boolean isRequestContainsInterval = null == request.getRequestDate() ? false
				: (request.getRequestDate().getInterval() != null && !request.getRequestDate().getInterval().isEmpty());
		String interval = isRequestContainsInterval ? request.getRequestDate().getInterval()
				: (isDefaultPresent ? chartNode.get(Constants.JsonPaths.INTERVAL).asText() : "");
		if (chartNode.get(Constants.JsonPaths.ALWAYS_VIEW) != null) {
			if (AlwaysView.MONTHWISE
					.equals(AlwaysView.fromValue(chartNode.get(Constants.JsonPaths.ALWAYS_VIEW).asText()))) {
				changeDatesToMonthWiseView(request);
			}
		}
		applyVisibilityTint(profileName, request, roles);
		Map<String, List<String>> responseDataSource = new HashMap<String, List<String>>(); 
		executeConfiguredQueries(chartNode, aggrObjectNode, nodes, request, interval, responseDataSource);
		request.setChartNode(chartNode);
		IResponseHandler responseHandler = responseHandlerFactory.getInstance(chartType);
		AggregateDto aggregateDto = new AggregateDto();
		if (aggrObjectNode.fields().hasNext()) {
			aggregateDto = responseHandler.translate(profileName, request, aggrObjectNode, responseDataSource);
		}
		if (insightsConfig != null && StringUtils.isNotBlank(insightsConfig.getInsightInterval())) {
			continueWithInsight = getInsightsDate(request, insightsConfig.getInsightInterval());
			if (continueWithInsight) {
				String insightVisualizationCode = insightPrefix + request.getVisualizationCode();
				request.setVisualizationCode(insightVisualizationCode);
				executeConfiguredQueries(chartNode, insightAggrObjectNode, insightNodes, request, interval, responseDataSource);
				request.setChartNode(chartNode);
				
				responseHandler = responseHandlerFactory.getInstance(chartType);
				if (insightAggrObjectNode.fields().hasNext()) {
					responseHandler.translate(profileName, request, insightAggrObjectNode, responseDataSource);
				}
				InsightsHandler insightsHandler = insightsHandlerFactory.getInstance(chartType);
				aggregateDto = insightsHandler.getInsights(aggregateDto, request.getVisualizationCode(),
						request.getModuleLevel(), insightsConfig);
			}
		}
		return aggregateDto;
	}

	private void applyVisibilityTint(String profileName, AggregateRequestDto request, List<RoleDto> roles) {
		ObjectNode roleMappingNode = configurationLoader.getConfigForProfile(profileName,
				ConfigurationLoader.ROLE_DASHBOARD_CONFIG);
		ArrayNode rolesArray = (ArrayNode) roleMappingNode.findValue(Constants.DashBoardConfig.ROLES);

		rolesArray.forEach(role -> {
			Object roleId = roles.stream()
					.filter(x -> role.get(Constants.DashBoardConfig.ROLE_ID).asLong() == (x.getId())).findAny()
					.orElse(null);
			if (null != roleId) {
				// checks role has given db id
				role.get(Constants.DashBoardConfig.DASHBOARDS).forEach(db -> {
					if (db.get(Constants.DashBoardConfig.ID).asText().equalsIgnoreCase(request.getDashboardId())) {
						ArrayNode visibilityArray = (ArrayNode) db.get(Constants.DashBoardConfig.VISIBILITY);
						if (visibilityArray != null) {
							Map<String, Object> filterMap = new HashMap<>();
							visibilityArray.forEach(visibility -> {
								String key = visibility.get(Constants.DashBoardConfig.KEY).asText();
								ArrayNode valueArray = (ArrayNode) visibility.get(Constants.DashBoardConfig.VALUE);
								List<String> valueList = new ArrayList<>();
								valueArray.forEach(value -> {
									valueList.add(value.asText());
								});
								if (!request.getFilters().containsKey(key)) {
									filterMap.put(key, valueList);
								}
							});
							request.getFilters().putAll(filterMap);
						}
					}
				});
			}
		});
	}

	private void executeConfiguredQueries(ObjectNode chartNode, ObjectNode aggrObjectNode, ObjectNode nodes,
			AggregateRequestDto request, String interval, Map<String, List<String>> responseDataSource) {
		JsonNode lookupNode = null;  
		if(chartNode.get(Constants.JsonPaths.ENRICHMENT_LOOKUP) != null) { 
			Map<String, String> enrichmentLookUpMap = new ObjectMapper().convertValue(chartNode.get(Constants.JsonPaths.ENRICHMENT_LOOKUP), new TypeReference<Map<String, String>>(){});
			if(enrichmentLookUpMap != null) { 
				lookupNode = fetchEnrichmentLookUpValues(enrichmentLookUpMap.get(TABLE_NAME), request, chartNode);
				aggrObjectNode.set(Constants.JsonPaths.ENRICHMENT_LOOKUP, lookupNode); 
			}
		}
		 
		ArrayNode queries = (ArrayNode) chartNode.get(Constants.JsonPaths.QUERIES);
		queries.forEach(query -> {
			String dataSource = query.get(Constants.JsonPaths.DATA_SOURCE).asText();
			String module = query.get(Constants.JsonPaths.MODULE).asText();
			String indexName = query.get(Constants.JsonPaths.INDEX_NAME).asText();
			String rqMs = query.get(Constants.JsonPaths.REQUEST_QUERY_MAP).asText();
			JsonNode requestQueryMaps = null ; 
			try {
				requestQueryMaps = new ObjectMapper().readTree(rqMs);
			} catch (Exception ex) {
				logger.error("Encountered an Exception while converting Request Query Map: " + ex.getMessage());
			}
			if(dataSource.equals(Constants.DataSourceType.ES.name())) { 
				ObjectNode objectNode = queryServiceFactory.getInstance(Constants.DataSourceType.ES)
						.getChartConfigurationQuery(request, query, indexName, interval);
				String instance = query.get(Constants.JsonPaths.ES_INSTANCE).asText();
				try {
					JsonNode aggrNode = restService.search(indexName, objectNode.toString(), instance);
					if (nodes.has(indexName)) {
						indexName = indexName + "_1";
					}
					logger.info("indexName +" + indexName);
					nodes.set(dataSource, aggrNode.get(Constants.JsonPaths.AGGREGATIONS));
					if(responseDataSource.containsKey(dataSource)) { 
						List<String> modules = responseDataSource.get(dataSource);
						modules.add(module); 
					} else { 
						List<String> modules = new ArrayList<>(); 
						modules.add(module); 
						responseDataSource.put(dataSource, modules); 
					}
				} catch (Exception e) {
					logger.error("Encountered an Exception while Executing the Query over ES : " + e.getMessage());
				}
				aggrObjectNode.set(Constants.JsonPaths.AGGREGATIONS, nodes);
			} else if(dataSource.equals(Constants.DataSourceType.DRUID.name())) { 
				
				ObjectNode objectNode = queryServiceFactory.getInstance(Constants.DataSourceType.DRUID)
						.getChartConfigurationQuery(request, query, indexName, interval);
				String aggrQuery = query.get(Constants.JsonPaths.AGGREGATION_QUERY).asText();
				if(request.getFilters() != null) { 
					aggrQuery = replaceFilters(aggrQuery, request, requestQueryMaps);
				}
				try { 
					JsonNode aggrNode = restService.searchDruid(aggrQuery);
					nodes.set(module, aggrNode);
					if(responseDataSource.containsKey(dataSource)) { 
						List<String> modules = responseDataSource.get(dataSource);
						modules.add(module); 
					} else { 
						List<String> modules = new ArrayList<>(); 
						modules.add(module); 
						responseDataSource.put(dataSource, modules); 
					}
				} catch (Exception e) {
					logger.error("Encountered an Exception while Executing the Query over DRUID : " + e.getMessage());
				}
				aggrObjectNode.set(Constants.JsonPaths.AGGREGATIONS, nodes);
			} else if(dataSource.equals(Constants.DataSourceType.REDIS.name())) {
				try { 
					String action = chartNode.get(Constants.JsonPaths.ACTION).asText(); 
					JsonNode aggrNode = redisRepository.getAllForKey(indexName, request, chartNode);
					ActionHelper actionHelper = actionFactory.getInstance(action);
					Map<String, List<Data>> dataMap = new HashMap<String, List<Data>>(); 
					List<Data> dataList = actionHelper.compute(aggrNode, request, dataMap);
					JsonNode finalNode = new ObjectMapper().valueToTree(dataList);
					nodes.set(indexName, finalNode);
					if(responseDataSource.containsKey(dataSource)) { 
						List<String> modules = responseDataSource.get(dataSource);
						modules.add(module); 
					} else { 
						List<String> modules = new ArrayList<>(); 
						modules.add(module); 
						responseDataSource.put(dataSource, modules); 
					}
				} catch (Exception e) {
					logger.error("Encountered an Exception while Executing the Query over DRUID : " + e.getMessage());
				}
				aggrObjectNode.set(Constants.JsonPaths.AGGREGATIONS, nodes);
			}

		});
	}
	
	private JsonNode fetchEnrichmentLookUpValues(String indexName, AggregateRequestDto request, ObjectNode chartNode) { 
		JsonNode aggrNode = redisRepository.getAllForKey(indexName, request, chartNode);
		return aggrNode; 
	}

	private String replaceFilters(String aggrQuery, AggregateRequestDto request, JsonNode requestQueryMaps) {
		String startDate = "" ; 
		String endDate = "" ; 
		if(request != null && 
				request.getRequestDate() != null && 
				StringUtils.isNotBlank(request.getRequestDate().getStartDate()) &&
				StringUtils.isNotBlank(request.getRequestDate().getEndDate())) { 
			startDate = getTimeStampFromEpoch(request.getRequestDate().getStartDate());
			endDate = getTimeStampFromEpoch(request.getRequestDate().getEndDate());
		}
		if(startDate.equals("")) startDate = appConfig.getDefaultStartDate();
		if(endDate.equals("")) endDate = appConfig.getDefaultEndDate();
		aggrQuery = aggrQuery.replace("$StartDate$", startDate.replace("Z", "")); 
		aggrQuery = aggrQuery.replace("$EndDate$", endDate.replace("Z", ""));
		
		if(request != null && request.getFilters() != null && !request.getFilters().isEmpty()) {
			Map<String, Object> filterMap = request.getFilters();
			Iterator<Entry<String, Object>> itr = filterMap.entrySet().iterator();
			while(itr.hasNext()) { 
				Entry<String, Object> entry = itr.next();
				if(requestQueryMaps.get(entry.getKey()) != null && StringUtils.isNotBlank(requestQueryMaps.get(entry.getKey()).asText())) {
					aggrQuery = aggrQuery.replace("$"+entry.getKey()+"$", " AND " + requestQueryMaps.get(entry.getKey()).asText() + " = " + entry.getValue());
				}
			}
		} else { 
			aggrQuery = aggrQuery.replace("$mdo$", ""); 
		}
		return aggrQuery; 
	}
	
	private String getTimeStampFromEpoch(String epoch) { 
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String parsedDate = formatter.format(new Date(Long.parseLong(epoch))); 
		return parsedDate; 
	}
	
	private String getQueryTimeStampFromEpoch(String epoch) { 
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'Z'");
		String parsedDate = formatter.format(new Date(Long.parseLong(epoch))); 
		return parsedDate; 
	}
	
	private void changeDatesToMonthWiseView(AggregateRequestDto request) {
		Long daysBetween = daysBetween(Long.parseLong(request.getRequestDate().getStartDate()),
				Long.parseLong(request.getRequestDate().getEndDate()));
		if (daysBetween <= 30) {
			Calendar startCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			startCal.setTime(new Date(Long.parseLong(request.getRequestDate().getStartDate())));
			startCal.set(Calendar.DAY_OF_MONTH, 1);
			endCal.setTime(new Date(Long.parseLong(request.getRequestDate().getEndDate())));
			int month = endCal.get(Calendar.MONTH) + 1;
			endCal.set(Calendar.MONTH, month + 1);
			request.getRequestDate().setStartDate(String.valueOf(startCal.getTimeInMillis()));
			request.getRequestDate().setEndDate(String.valueOf(endCal.getTimeInMillis()));
		}
	}

	private Boolean getInsightsDate(AggregateRequestDto request, String insightInterval) {
		Long daysBetween = daysBetween(Long.parseLong(request.getRequestDate().getStartDate()),
				Long.parseLong(request.getRequestDate().getEndDate()));
		if (insightInterval.equalsIgnoreCase(Constants.Interval.month.toString()) && daysBetween > 32) {
			return Boolean.FALSE;
		}
		if (insightInterval.equalsIgnoreCase(Constants.Interval.week.toString()) && daysBetween > 8) {
			return Boolean.FALSE;
		}
		if (insightInterval.equalsIgnoreCase(Constants.Interval.year.toString()) && daysBetween > 366) {
			return Boolean.FALSE;
		}
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		startCal.setTime(new Date(Long.parseLong(request.getRequestDate().getStartDate())));
		endCal.setTime(new Date(Long.parseLong(request.getRequestDate().getEndDate())));
		if (insightInterval.equalsIgnoreCase(Constants.Interval.month.toString())) {
			startCal.add(Calendar.MONTH, -1);
			endCal.add(Calendar.MONTH, -1);
		} else if (insightInterval.equalsIgnoreCase(Constants.Interval.week.toString())) {
			startCal.add(Calendar.WEEK_OF_YEAR, -1);
			endCal.add(Calendar.WEEK_OF_YEAR, -1);
		} else if (StringUtils.isBlank(insightInterval)
				|| insightInterval.equalsIgnoreCase(Constants.Interval.year.toString())) {
			startCal.add(Calendar.YEAR, -1);
			endCal.add(Calendar.YEAR, -1);
		}
		request.getRequestDate().setStartDate(String.valueOf(startCal.getTimeInMillis()));
		request.getRequestDate().setEndDate(String.valueOf(endCal.getTimeInMillis()));
		return Boolean.TRUE;
	}

	public long daysBetween(Long start, Long end) {
		return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
	}

	@Override
	public List<DashboardHeaderDto> getHeaderData(CummulativeDataRequestDto requestDto, List<RoleDto> roles)
			throws AINException {
		// TODO Auto-generated method stub
		return null;
	}

}
