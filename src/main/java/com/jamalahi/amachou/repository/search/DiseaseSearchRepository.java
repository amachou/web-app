package com.jamalahi.amachou.repository.search;

import com.jamalahi.amachou.domain.Disease;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Disease entity.
 */
public interface DiseaseSearchRepository extends ElasticsearchRepository<Disease, Long> {
}
