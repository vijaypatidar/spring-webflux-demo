package com.vkpapps.demo.configs.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {
    private final ElasticsearchClient client;

    public ElasticSearchService(ElasticSearchConfig config) {

        RestClientBuilder builder = getRestClientBuilder(config);
        RestClient restClient = builder.build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        this.client = new ElasticsearchClient(transport);
    }

    private static RestClientBuilder getRestClientBuilder(ElasticSearchConfig config) {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(config.getHostname(), 9200));
        if (config.getUsername() != null && config.getPassword() != null) {
            final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(config.getUsername(), config.getPassword()));

            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider));
        }

        return builder;
    }

    public <T> String index(IndexRequest<T> userIndexRequest) {
        try {
            IndexResponse index = client.index(userIndexRequest);
            return index.result().jsonValue();
        } catch (Exception e) {
            return "failed";
        }
    }

    public <T> List<T> search(Map<String, Object> term, String indexName, Class<T> tClass) {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(indexName)
                .query(q -> {
                    term.forEach((k, v) -> {
                        q.term(t -> {
                            t.field(k).value(String.valueOf(v));
                            return t;
                        });
                    });
                    return q;
                })
                .build();
        return search(tClass, searchRequest);
    }

    public  <T> List<T> search(Class<T> tClass, SearchRequest searchRequest) {
        try {
            SearchResponse<T> search = client.search(searchRequest, tClass);
            return search.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
}
