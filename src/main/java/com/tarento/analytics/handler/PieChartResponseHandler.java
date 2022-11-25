package com.tarento.analytics.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tarento.analytics.constant.Constants;
import com.tarento.analytics.dto.AggregateDto;
import com.tarento.analytics.dto.AggregateRequestDto;
import com.tarento.analytics.dto.Data;
import com.tarento.analytics.dto.Plot;
/**
 * This handles ES response for single index, multiple index to represent data as pie figure
 * Creates plots by merging/computing(by summation) index values for same key
 * AGGS_PATH : this defines the path/key to be used to search the tree
 * VALUE_TYPE : defines the data type for the value formed, this could be amount, percentage, number
 *
 */
@Component
public class PieChartResponseHandler implements IResponseHandler {
    public static final Logger logger = LoggerFactory.getLogger(PieChartResponseHandler.class);


    @Override
    public AggregateDto translate(String profileName, AggregateRequestDto requestDto, ObjectNode aggregations, Map<String, List<String>> responseDataSource) throws IOException {

        List<Data> dataList = new ArrayList<>();
        JsonNode chartNode = requestDto.getChartNode();
        String headerKey = chartNode.get(CHART_NAME).asText();
        List<Plot> headerPlotList = new ArrayList<>();
        List<Double> totalValue = new ArrayList<>();
        String vizType = chartNode.get(IResponseHandler.CHART_TYPE).asText();
        String chartName = chartNode.get(CHART_NAME).asText();

        String symbol = chartNode.get(IResponseHandler.VALUE_TYPE).asText();
        JsonNode aggsPathChartNode = null;
		try {
			String queries = chartNode.get(AGGS_PATH).asText();
			if(StringUtils.isNotBlank(queries)) { 
				aggsPathChartNode = new ObjectMapper().readValue(queries, JsonNode.class);
			} else { 
				aggsPathChartNode = chartNode.get(AGGS_PATH);
			}
			
		} catch (JsonMappingException e) {
			logger.error("Encountered an exception on Aggregation Paths : " + e.getMessage());
		} catch (JsonProcessingException e) {
			logger.error("Encountered an exception on Aggregation Paths : " + e.getMessage());
		}

        ArrayNode aggrsPaths = (ArrayNode) aggsPathChartNode;

		aggrsPaths.forEach(headerPath -> {
			if (responseDataSource.containsKey(Constants.DataSourceType.ES.toString())) {
				JsonNode aggregationNode = aggregations.get(AGGREGATIONS);
				aggregationNode.findValues(headerPath.asText()).stream().parallel().forEach(valueNode -> {
					if (valueNode.has(BUCKETS)) {
						JsonNode buckets = valueNode.findValue(BUCKETS);
						String headerLabel = chartNode.get(HEADER_LABEL) != null ? chartNode.get(HEADER_LABEL).asText()
								: HEADER_LABEL_DEFAULT;
						String valueLabel = chartNode.get(VALUE_LABEL) != null ? chartNode.get(VALUE_LABEL).asText()
								: VALUE_LABEL_DEFAULT;
						buckets.forEach(bucket -> {
							Plot plot = null;
							if (bucket.findValue(VALUE) == null) {
								if (bucket.findValue(DOC_COUNT) != null) {
									totalValue.add(bucket.findValue(DOC_COUNT).asDouble());
									plot = new Plot(bucket.findValue(KEY).asText(),
											bucket.findValue(DOC_COUNT).asDouble(), symbol, headerLabel, valueLabel);
								}
							} else {
								totalValue.add(bucket.findValue(VALUE).asDouble());
								plot = new Plot(bucket.findValue(KEY).asText(), bucket.findValue(VALUE).asDouble(),
										symbol, headerLabel, valueLabel);
							}

							headerPlotList.add(plot);
						});

					} else {
						List<JsonNode> valueNodes = valueNode.findValues(VALUE).isEmpty()
								? valueNode.findValues("doc_count")
								: valueNode.findValues(VALUE);
						double sum = valueNodes.stream().mapToLong(o -> o.asLong()).sum();
						totalValue.add(sum);
						Plot plot = new Plot(headerPath.asText(), sum, symbol);

						headerPlotList.add(plot);
					}
				});
			} else if (responseDataSource.containsKey(Constants.DataSourceType.DRUID.toString())) {
				JsonNode aggregationNode = aggregations.get(AGGREGATIONS);
				List<String> modules = responseDataSource.get(Constants.DataSourceType.DRUID.toString());
				for(String moduleName : modules) { 
					ArrayNode arrayNode = (ArrayNode) aggregationNode.get(moduleName);
					JsonNode controlNode = chartNode.get(Constants.JsonPaths.CONTROLS);
					String pathKey = controlNode.get(Constants.JsonPaths.KEY).asText(); 
					String pathValue = controlNode.get(Constants.JsonPaths.VALUE).asText();
					arrayNode.forEach(eachNode -> {
							String key= eachNode.get(pathKey).asText();
							Object value = chartNode.get(IResponseHandler.IS_DECIMAL).asBoolean() ? eachNode.get(pathValue).asDouble() : eachNode.get(pathValue).asLong();
						if (value != null && StringUtils.isNotBlank(key) && !key.equals("null")) {
							Plot plot = new Plot();
							plot.setLabel("header");
							plot.setName((aggregations.get(Constants.JsonPaths.ENRICHMENT_LOOKUP) != null
									&& aggregations.get(Constants.JsonPaths.ENRICHMENT_LOOKUP).get(key) != null)
											? aggregations.get(Constants.JsonPaths.ENRICHMENT_LOOKUP).get(key).asText()
											: key);
							plot.setValue(value);
							plot.setValueLabel("Value");
							plot.setSymbol("number");
							if(chartNode.get(LIMIT) == null) { 
								headerPlotList.add(plot);
					        } else if(chartNode.get(LIMIT) != null && headerPlotList.size() < chartNode.get(LIMIT).asInt()) {
								headerPlotList.add(plot);
					        }
						}
			        });
				}
			} else if (responseDataSource.containsKey(Constants.DataSourceType.REDIS.toString())) {
				JsonNode aggregationNode = aggregations.get(AGGREGATIONS);
				ArrayNode innerDataNode = (ArrayNode) aggregationNode.get(headerPath.asText());
				innerDataNode.forEach(eachNode -> {
					Plot plot = new Plot(); 
		            plot.setLabel("header");
		            plot.setName((aggregations.get(Constants.JsonPaths.ENRICHMENT_LOOKUP) != null
							&& aggregations.get(Constants.JsonPaths.ENRICHMENT_LOOKUP).get(eachNode.get("headerName").asText()) != null)
									? aggregations.get(Constants.JsonPaths.ENRICHMENT_LOOKUP).get(eachNode.get("headerName").asText()).asText()
									: eachNode.get("headerName").asText());
		            plot.setValue(eachNode.get("headerValue").asLong());
		            plot.setValueLabel("Value");
		            plot.setSymbol("number");
		            if(chartNode.get(LIMIT) == null) { 
						headerPlotList.add(plot);
			        } else if(chartNode.get(LIMIT) != null && headerPlotList.size() < chartNode.get(LIMIT).asInt()) {
						headerPlotList.add(plot);
			        }				});
				if (responseDataSource.size() > 1)
					responseDataSource.remove(Constants.DataSourceType.REDIS.toString());
			}
		});

        Data data = new Data(headerKey, totalValue.stream().reduce(0.0, Double::sum), symbol);
        data.setPlots(headerPlotList);
        if(chartNode.get(COLOR_PALETTE_CODE) != null && chartNode.get(COLOR_PALETTE_ID) != null) { 
        	data.setColorPaletteCode(chartNode.get(COLOR_PALETTE_CODE).asText());
        	data.setColorPaletteId(chartNode.get(COLOR_PALETTE_ID).asLong());
        }
        dataList.add(data);
        
        return getAggregatedDto(chartNode, dataList, requestDto.getVisualizationCode());

    }
}
