package com.tarento.analytics.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tarento.analytics.dto.AggregateRequestDto;

public interface QueryService {
/*
	List<Aggregation> getAggregateData(AggregateRequestDto aggregateDto, String orgId) throws AINException;
*/
	public static final String API_CONFIG_JSON = "ChartApiConfig.json";
	public static final String DATE_SOURCE_FIELD = "dateSourceField"; 
	public static final String AGG_QUERY_JSON = "aggregationQueryJson"; 
	public static final String INDEX_NAME = "indexName" ;
	public static final String DOCUMENT_TYPE = "documentType"; 
/*	Aggregations getAggregateData(AggregateRequestDto aggregateDto, String orgId) throws AINException;
	Aggregations getAggregateDataV2(AggregateRequestDtoV2 aggregateDto, String orgId) throws AINException, JsonParseException, JsonMappingException, IOException;
	*/
	ObjectNode getChartConfigurationQuery(AggregateRequestDto req, JsonNode query, String indexName, String interval);
	
	
}
