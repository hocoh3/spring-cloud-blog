package com.blog.search.config;

import com.blog.search.entity.SearchArticle;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

@Slf4j
@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate(@Autowired RestHighLevelClient restHighLevelClient) {
        ElasticsearchRestTemplate template = new ElasticsearchRestTemplate(restHighLevelClient);
        
        try {
            IndexOperations indexOperations = template.indexOps(SearchArticle.class);
            
            if (!indexOperations.exists()) {
                log.info("创建 Elasticsearch 索引: blog_article");
                indexOperations.create();
                indexOperations.putMapping();
                log.info("Elasticsearch 索引创建成功");
            } else {
                log.info("Elasticsearch 索引已存在: blog_article");
            }
        } catch (Exception e) {
            log.error("初始化 Elasticsearch 索引失败", e);
        }
        
        return template;
    }
}