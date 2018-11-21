package com.jamalahi.amachou.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of HospitalSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class HospitalSearchRepositoryMockConfiguration {

    @MockBean
    private HospitalSearchRepository mockHospitalSearchRepository;

}
