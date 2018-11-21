package com.jamalahi.amachou.repository.search;

import com.jamalahi.amachou.domain.Hospital;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Hospital entity.
 */
public interface HospitalSearchRepository extends ElasticsearchRepository<Hospital, Long> {
}
