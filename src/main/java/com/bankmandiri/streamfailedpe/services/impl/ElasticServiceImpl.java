package com.bankmandiri.streamfailedpe.services.impl;

import com.bankmandiri.streamfailedpe.model.ConsumerData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bankmandiri.streamfailedpe.services.ElasticService;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bankmandiri.streamfailedpe.utils.Constant.CONSUMER_INDEX;
import static com.bankmandiri.streamfailedpe.utils.Constant.CONSUMER_TYPE;

@Service
public class ElasticServiceImpl implements ElasticService {

	private final RestHighLevelClient client;

	private final ObjectMapper objectMapper;

    @Autowired
    public ElasticServiceImpl(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

	@SuppressWarnings("deprecation")
	@Override
	public String insert(ConsumerData data) {
		try {
			 IndexRequest indexRequest = new IndexRequest(CONSUMER_INDEX, CONSUMER_TYPE, data.getConsumerName())
		                .source(convertToMap(data), XContentType.JSON);
			 IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
		     return indexResponse.getResult().name();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ConsumerData getById(String consumerName) {
		try {
			GetRequest getRequest = new GetRequest(CONSUMER_INDEX, CONSUMER_TYPE, consumerName);
			GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
			Map<String, Object> resultMap = getResponse.getSource();
			return convertFromMap(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String deleteById(ConsumerData consumerName) {
		try {
			DeleteRequest deleteRequest = new DeleteRequest(CONSUMER_INDEX, CONSUMER_TYPE, consumerName.getConsumerName());
			DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
			return response.getResult().name();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<ConsumerData> getListConsumerData() {
		try {
			SearchRequest searchRequest = buildSearchRequest();
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.matchAllQuery());
			searchRequest.source(searchSourceBuilder);
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			return getSearchResult(searchResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<ConsumerData> getSearchResult(SearchResponse response) {
		SearchHit[] searchHit = response.getHits().getHits();
		List<ConsumerData> profileDocuments = new ArrayList<>();
		for (SearchHit hit : searchHit) {
			profileDocuments.add(objectMapper.convertValue(hit.getSourceAsMap(), ConsumerData.class));
		}
		return profileDocuments;
	}

	@SuppressWarnings("deprecation")
	private SearchRequest buildSearchRequest() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices(CONSUMER_INDEX);
		searchRequest.types(CONSUMER_TYPE);
		return searchRequest;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> convertToMap(ConsumerData data) {
        return objectMapper.convertValue(data, Map.class);
    }

	private ConsumerData convertFromMap(Map<String, Object> map){
        return objectMapper.convertValue(map, ConsumerData.class);
    }

}
