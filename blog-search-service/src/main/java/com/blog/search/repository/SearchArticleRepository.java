package com.blog.search.repository;

import com.blog.search.entity.SearchArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchArticleRepository extends ElasticsearchRepository<SearchArticle, Long> {

    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^3\", \"summary^2\", \"content^1\"]}}")
    Page<SearchArticle> searchArticles(String keyword, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^3\", \"summary^2\", \"content^1\"]}}, {\"term\": {\"categoryId\": \"?1\"}}]}}")
    Page<SearchArticle> searchArticlesByCategory(String keyword, Long categoryId, Pageable pageable);
}