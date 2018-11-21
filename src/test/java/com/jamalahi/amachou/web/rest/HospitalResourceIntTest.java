package com.jamalahi.amachou.web.rest;

import com.jamalahi.amachou.AmachouApp;

import com.jamalahi.amachou.domain.Hospital;
import com.jamalahi.amachou.repository.HospitalRepository;
import com.jamalahi.amachou.repository.search.HospitalSearchRepository;
import com.jamalahi.amachou.service.HospitalService;
import com.jamalahi.amachou.service.dto.HospitalDTO;
import com.jamalahi.amachou.service.mapper.HospitalMapper;
import com.jamalahi.amachou.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static com.jamalahi.amachou.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jamalahi.amachou.domain.enumeration.HospitalSize;
/**
 * Test class for the HospitalResource REST controller.
 *
 * @see HospitalResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AmachouApp.class)
public class HospitalResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_LAT = 1D;
    private static final Double UPDATED_LAT = 2D;

    private static final Double DEFAULT_LON = 1D;
    private static final Double UPDATED_LON = 2D;

    private static final HospitalSize DEFAULT_SIZE = HospitalSize.SMALL;
    private static final HospitalSize UPDATED_SIZE = HospitalSize.MEDIUM;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private HospitalService hospitalService;

    /**
     * This repository is mocked in the com.jamalahi.amachou.repository.search test package.
     *
     * @see com.jamalahi.amachou.repository.search.HospitalSearchRepositoryMockConfiguration
     */
    @Autowired
    private HospitalSearchRepository mockHospitalSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restHospitalMockMvc;

    private Hospital hospital;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final HospitalResource hospitalResource = new HospitalResource(hospitalService);
        this.restHospitalMockMvc = MockMvcBuilders.standaloneSetup(hospitalResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hospital createEntity(EntityManager em) {
        Hospital hospital = new Hospital()
            .name(DEFAULT_NAME)
            .lat(DEFAULT_LAT)
            .lon(DEFAULT_LON)
            .size(DEFAULT_SIZE);
        return hospital;
    }

    @Before
    public void initTest() {
        hospital = createEntity(em);
    }

    @Test
    @Transactional
    public void createHospital() throws Exception {
        int databaseSizeBeforeCreate = hospitalRepository.findAll().size();

        // Create the Hospital
        HospitalDTO hospitalDTO = hospitalMapper.toDto(hospital);
        restHospitalMockMvc.perform(post("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospitalDTO)))
            .andExpect(status().isCreated());

        // Validate the Hospital in the database
        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeCreate + 1);
        Hospital testHospital = hospitalList.get(hospitalList.size() - 1);
        assertThat(testHospital.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testHospital.getLat()).isEqualTo(DEFAULT_LAT);
        assertThat(testHospital.getLon()).isEqualTo(DEFAULT_LON);
        assertThat(testHospital.getSize()).isEqualTo(DEFAULT_SIZE);

        // Validate the Hospital in Elasticsearch
        verify(mockHospitalSearchRepository, times(1)).save(testHospital);
    }

    @Test
    @Transactional
    public void createHospitalWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = hospitalRepository.findAll().size();

        // Create the Hospital with an existing ID
        hospital.setId(1L);
        HospitalDTO hospitalDTO = hospitalMapper.toDto(hospital);

        // An entity with an existing ID cannot be created, so this API call must fail
        restHospitalMockMvc.perform(post("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospitalDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Hospital in the database
        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeCreate);

        // Validate the Hospital in Elasticsearch
        verify(mockHospitalSearchRepository, times(0)).save(hospital);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = hospitalRepository.findAll().size();
        // set the field null
        hospital.setName(null);

        // Create the Hospital, which fails.
        HospitalDTO hospitalDTO = hospitalMapper.toDto(hospital);

        restHospitalMockMvc.perform(post("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospitalDTO)))
            .andExpect(status().isBadRequest());

        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLatIsRequired() throws Exception {
        int databaseSizeBeforeTest = hospitalRepository.findAll().size();
        // set the field null
        hospital.setLat(null);

        // Create the Hospital, which fails.
        HospitalDTO hospitalDTO = hospitalMapper.toDto(hospital);

        restHospitalMockMvc.perform(post("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospitalDTO)))
            .andExpect(status().isBadRequest());

        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLonIsRequired() throws Exception {
        int databaseSizeBeforeTest = hospitalRepository.findAll().size();
        // set the field null
        hospital.setLon(null);

        // Create the Hospital, which fails.
        HospitalDTO hospitalDTO = hospitalMapper.toDto(hospital);

        restHospitalMockMvc.perform(post("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospitalDTO)))
            .andExpect(status().isBadRequest());

        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSizeIsRequired() throws Exception {
        int databaseSizeBeforeTest = hospitalRepository.findAll().size();
        // set the field null
        hospital.setSize(null);

        // Create the Hospital, which fails.
        HospitalDTO hospitalDTO = hospitalMapper.toDto(hospital);

        restHospitalMockMvc.perform(post("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospitalDTO)))
            .andExpect(status().isBadRequest());

        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllHospitals() throws Exception {
        // Initialize the database
        hospitalRepository.saveAndFlush(hospital);

        // Get all the hospitalList
        restHospitalMockMvc.perform(get("/api/hospitals?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hospital.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].lat").value(hasItem(DEFAULT_LAT.doubleValue())))
            .andExpect(jsonPath("$.[*].lon").value(hasItem(DEFAULT_LON.doubleValue())))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.toString())));
    }
    
    @Test
    @Transactional
    public void getHospital() throws Exception {
        // Initialize the database
        hospitalRepository.saveAndFlush(hospital);

        // Get the hospital
        restHospitalMockMvc.perform(get("/api/hospitals/{id}", hospital.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(hospital.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.lat").value(DEFAULT_LAT.doubleValue()))
            .andExpect(jsonPath("$.lon").value(DEFAULT_LON.doubleValue()))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingHospital() throws Exception {
        // Get the hospital
        restHospitalMockMvc.perform(get("/api/hospitals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHospital() throws Exception {
        // Initialize the database
        hospitalRepository.saveAndFlush(hospital);

        int databaseSizeBeforeUpdate = hospitalRepository.findAll().size();

        // Update the hospital
        Hospital updatedHospital = hospitalRepository.findById(hospital.getId()).get();
        // Disconnect from session so that the updates on updatedHospital are not directly saved in db
        em.detach(updatedHospital);
        updatedHospital
            .name(UPDATED_NAME)
            .lat(UPDATED_LAT)
            .lon(UPDATED_LON)
            .size(UPDATED_SIZE);
        HospitalDTO hospitalDTO = hospitalMapper.toDto(updatedHospital);

        restHospitalMockMvc.perform(put("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospitalDTO)))
            .andExpect(status().isOk());

        // Validate the Hospital in the database
        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeUpdate);
        Hospital testHospital = hospitalList.get(hospitalList.size() - 1);
        assertThat(testHospital.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testHospital.getLat()).isEqualTo(UPDATED_LAT);
        assertThat(testHospital.getLon()).isEqualTo(UPDATED_LON);
        assertThat(testHospital.getSize()).isEqualTo(UPDATED_SIZE);

        // Validate the Hospital in Elasticsearch
        verify(mockHospitalSearchRepository, times(1)).save(testHospital);
    }

    @Test
    @Transactional
    public void updateNonExistingHospital() throws Exception {
        int databaseSizeBeforeUpdate = hospitalRepository.findAll().size();

        // Create the Hospital
        HospitalDTO hospitalDTO = hospitalMapper.toDto(hospital);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHospitalMockMvc.perform(put("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospitalDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Hospital in the database
        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Hospital in Elasticsearch
        verify(mockHospitalSearchRepository, times(0)).save(hospital);
    }

    @Test
    @Transactional
    public void deleteHospital() throws Exception {
        // Initialize the database
        hospitalRepository.saveAndFlush(hospital);

        int databaseSizeBeforeDelete = hospitalRepository.findAll().size();

        // Get the hospital
        restHospitalMockMvc.perform(delete("/api/hospitals/{id}", hospital.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Hospital in Elasticsearch
        verify(mockHospitalSearchRepository, times(1)).deleteById(hospital.getId());
    }

    @Test
    @Transactional
    public void searchHospital() throws Exception {
        // Initialize the database
        hospitalRepository.saveAndFlush(hospital);
        when(mockHospitalSearchRepository.search(queryStringQuery("id:" + hospital.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(hospital), PageRequest.of(0, 1), 1));
        // Search the hospital
        restHospitalMockMvc.perform(get("/api/_search/hospitals?query=id:" + hospital.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hospital.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].lat").value(hasItem(DEFAULT_LAT.doubleValue())))
            .andExpect(jsonPath("$.[*].lon").value(hasItem(DEFAULT_LON.doubleValue())))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Hospital.class);
        Hospital hospital1 = new Hospital();
        hospital1.setId(1L);
        Hospital hospital2 = new Hospital();
        hospital2.setId(hospital1.getId());
        assertThat(hospital1).isEqualTo(hospital2);
        hospital2.setId(2L);
        assertThat(hospital1).isNotEqualTo(hospital2);
        hospital1.setId(null);
        assertThat(hospital1).isNotEqualTo(hospital2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HospitalDTO.class);
        HospitalDTO hospitalDTO1 = new HospitalDTO();
        hospitalDTO1.setId(1L);
        HospitalDTO hospitalDTO2 = new HospitalDTO();
        assertThat(hospitalDTO1).isNotEqualTo(hospitalDTO2);
        hospitalDTO2.setId(hospitalDTO1.getId());
        assertThat(hospitalDTO1).isEqualTo(hospitalDTO2);
        hospitalDTO2.setId(2L);
        assertThat(hospitalDTO1).isNotEqualTo(hospitalDTO2);
        hospitalDTO1.setId(null);
        assertThat(hospitalDTO1).isNotEqualTo(hospitalDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(hospitalMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(hospitalMapper.fromId(null)).isNull();
    }
}
