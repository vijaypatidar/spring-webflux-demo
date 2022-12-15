package com.vkpapps.demo.services.ealsticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchService {
    private final ElasticsearchClient client;

    public <T> String index(IndexRequest<T> userIndexRequest) {
        try {
            IndexResponse index = client.index(userIndexRequest);
            return index.result().jsonValue();
        } catch (Exception e) {
            log.error("Elasticsearch index operation failed.", e);
            return "failed";
        }
    }

    public <T> List<T> search(Map<String, Object> term, String indexName, Class<T> tClass) {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(indexName)
                .query(q -> {
                    term.forEach((k, v) -> q.term(t -> {
                        t.field(k).value(String.valueOf(v));
                        return t;
                    }));
                    return q;
                })
                .build();
        return search(tClass, searchRequest);
    }

    public <T> List<T> search(Class<T> tClass, SearchRequest searchRequest) {
        try {
            SearchResponse<T> search = client.search(searchRequest, tClass);
            return search.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Elasticsearch search operation failed.", e);
            return List.of();
        }
    }
}
