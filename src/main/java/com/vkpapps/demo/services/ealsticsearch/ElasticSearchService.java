package com.vkpapps.demo.services.ealsticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchService {
    private final ElasticsearchClient client;

    public <T> Result index(IndexRequest<T> userIndexRequest) throws IOException {
        try {
            IndexResponse index = client.index(userIndexRequest);
            return index.result();
        } catch (Exception e) {
            log.error("Elasticsearch index operation failed.", e);
            throw e;
        }
    }

    public <T> List<T> termSearch(Map<String, Object> term, String indexName, Class<T> tClass) {
        var searchRequest = new SearchRequest.Builder()
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

    public List<Map> querySearch(String query, List<String> fields, String indexName) {
        var searchRequest = new SearchRequest.Builder()
                .index(indexName)
                .query(q -> {
                    q.multiMatch(builder -> {
                        builder.query(query).fields(fields);
                        return builder;
                    });
                    return q;
                })
                .build();
        return search(Map.class, searchRequest);
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
