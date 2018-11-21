package com.jamalahi.amachou.web.rest;

import com.jamalahi.amachou.AmachouApp;

import com.jamalahi.amachou.domain.Disease;
import com.jamalahi.amachou.repository.DiseaseRepository;
import com.jamalahi.amachou.repository.search.DiseaseSearchRepository;
import com.jamalahi.amachou.service.DiseaseService;
import com.jamalahi.amachou.service.dto.DiseaseDTO;
import com.jamalahi.amachou.service.mapper.DiseaseMapper;
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

import com.jamalahi.amachou.domain.enumeration.DiseaseSeverity;
/**
 * Test class for the DiseaseResource REST controller.
 *
 * @see DiseaseResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AmachouApp.class)
public class DiseaseResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final DiseaseSeverity DEFAULT_SEVERITY = DiseaseSeverity.LOW;
    private static final DiseaseSeverity UPDATED_SEVERITY = DiseaseSeverity.MEDIUM;

    private static final String DEFAULT_SYMPTOMS = "AAAAAAAAAA";
    private static final String UPDATED_SYMPTOMS = "BBBBBBBBBB";

    private static final String DEFAULT_TIPS = "AAAAAAAAAA";
    private static final String UPDATED_TIPS = "BBBBBBBBBB";

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private DiseaseMapper diseaseMapper;

    @Autowired
    private DiseaseService diseaseService;

    /**
     * This repository is mocked in the com.jamalahi.amachou.repository.search test package.
     *
     * @see com.jamalahi.amachou.repository.search.DiseaseSearchRepositoryMockConfiguration
     */
    @Autowired
    private DiseaseSearchRepository mockDiseaseSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDiseaseMockMvc;

    private Disease disease;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DiseaseResource diseaseResource = new DiseaseResource(diseaseService);
        this.restDiseaseMockMvc = MockMvcBuilders.standaloneSetup(diseaseResource)
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
    public static Disease createEntity(EntityManager em) {
        Disease disease = new Disease()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .severity(DEFAULT_SEVERITY)
            .symptoms(DEFAULT_SYMPTOMS)
            .tips(DEFAULT_TIPS);
        return disease;
    }

    @Before
    public void initTest() {
        disease = createEntity(em);
    }

    @Test
    @Transactional
    public void createDisease() throws Exception {
        int databaseSizeBeforeCreate = diseaseRepository.findAll().size();

        // Create the Disease
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);
        restDiseaseMockMvc.perform(post("/api/diseases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diseaseDTO)))
            .andExpect(status().isCreated());

        // Validate the Disease in the database
        List<Disease> diseaseList = diseaseRepository.findAll();
        assertThat(diseaseList).hasSize(databaseSizeBeforeCreate + 1);
        Disease testDisease = diseaseList.get(diseaseList.size() - 1);
        assertThat(testDisease.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDisease.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testDisease.getSeverity()).isEqualTo(DEFAULT_SEVERITY);
        assertThat(testDisease.getSymptoms()).isEqualTo(DEFAULT_SYMPTOMS);
        assertThat(testDisease.getTips()).isEqualTo(DEFAULT_TIPS);

        // Validate the Disease in Elasticsearch
        verify(mockDiseaseSearchRepository, times(1)).save(testDisease);
    }

    @Test
    @Transactional
    public void createDiseaseWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = diseaseRepository.findAll().size();

        // Create the Disease with an existing ID
        disease.setId(1L);
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiseaseMockMvc.perform(post("/api/diseases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diseaseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Disease in the database
        List<Disease> diseaseList = diseaseRepository.findAll();
        assertThat(diseaseList).hasSize(databaseSizeBeforeCreate);

        // Validate the Disease in Elasticsearch
        verify(mockDiseaseSearchRepository, times(0)).save(disease);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = diseaseRepository.findAll().size();
        // set the field null
        disease.setName(null);

        // Create the Disease, which fails.
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        restDiseaseMockMvc.perform(post("/api/diseases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diseaseDTO)))
            .andExpect(status().isBadRequest());

        List<Disease> diseaseList = diseaseRepository.findAll();
        assertThat(diseaseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSeverityIsRequired() throws Exception {
        int databaseSizeBeforeTest = diseaseRepository.findAll().size();
        // set the field null
        disease.setSeverity(null);

        // Create the Disease, which fails.
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        restDiseaseMockMvc.perform(post("/api/diseases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diseaseDTO)))
            .andExpect(status().isBadRequest());

        List<Disease> diseaseList = diseaseRepository.findAll();
        assertThat(diseaseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSymptomsIsRequired() throws Exception {
        int databaseSizeBeforeTest = diseaseRepository.findAll().size();
        // set the field null
        disease.setSymptoms(null);

        // Create the Disease, which fails.
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        restDiseaseMockMvc.perform(post("/api/diseases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diseaseDTO)))
            .andExpect(status().isBadRequest());

        List<Disease> diseaseList = diseaseRepository.findAll();
        assertThat(diseaseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTipsIsRequired() throws Exception {
        int databaseSizeBeforeTest = diseaseRepository.findAll().size();
        // set the field null
        disease.setTips(null);

        // Create the Disease, which fails.
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        restDiseaseMockMvc.perform(post("/api/diseases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diseaseDTO)))
            .andExpect(status().isBadRequest());

        List<Disease> diseaseList = diseaseRepository.findAll();
        assertThat(diseaseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDiseases() throws Exception {
        // Initialize the database
        diseaseRepository.saveAndFlush(disease);

        // Get all the diseaseList
        restDiseaseMockMvc.perform(get("/api/diseases?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(disease.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].severity").value(hasItem(DEFAULT_SEVERITY.toString())))
            .andExpect(jsonPath("$.[*].symptoms").value(hasItem(DEFAULT_SYMPTOMS.toString())))
            .andExpect(jsonPath("$.[*].tips").value(hasItem(DEFAULT_TIPS.toString())));
    }
    
    @Test
    @Transactional
    public void getDisease() throws Exception {
        // Initialize the database
        diseaseRepository.saveAndFlush(disease);

        // Get the disease
        restDiseaseMockMvc.perform(get("/api/diseases/{id}", disease.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(disease.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.severity").value(DEFAULT_SEVERITY.toString()))
            .andExpect(jsonPath("$.symptoms").value(DEFAULT_SYMPTOMS.toString()))
            .andExpect(jsonPath("$.tips").value(DEFAULT_TIPS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDisease() throws Exception {
        // Get the disease
        restDiseaseMockMvc.perform(get("/api/diseases/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDisease() throws Exception {
        // Initialize the database
        diseaseRepository.saveAndFlush(disease);

        int databaseSizeBeforeUpdate = diseaseRepository.findAll().size();

        // Update the disease
        Disease updatedDisease = diseaseRepository.findById(disease.getId()).get();
        // Disconnect from session so that the updates on updatedDisease are not directly saved in db
        em.detach(updatedDisease);
        updatedDisease
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .severity(UPDATED_SEVERITY)
            .symptoms(UPDATED_SYMPTOMS)
            .tips(UPDATED_TIPS);
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(updatedDisease);

        restDiseaseMockMvc.perform(put("/api/diseases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diseaseDTO)))
            .andExpect(status().isOk());

        // Validate the Disease in the database
        List<Disease> diseaseList = diseaseRepository.findAll();
        assertThat(diseaseList).hasSize(databaseSizeBeforeUpdate);
        Disease testDisease = diseaseList.get(diseaseList.size() - 1);
        assertThat(testDisease.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDisease.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDisease.getSeverity()).isEqualTo(UPDATED_SEVERITY);
        assertThat(testDisease.getSymptoms()).isEqualTo(UPDATED_SYMPTOMS);
        assertThat(testDisease.getTips()).isEqualTo(UPDATED_TIPS);

        // Validate the Disease in Elasticsearch
        verify(mockDiseaseSearchRepository, times(1)).save(testDisease);
    }

    @Test
    @Transactional
    public void updateNonExistingDisease() throws Exception {
        int databaseSizeBeforeUpdate = diseaseRepository.findAll().size();

        // Create the Disease
        DiseaseDTO diseaseDTO = diseaseMapper.toDto(disease);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiseaseMockMvc.perform(put("/api/diseases")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diseaseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Disease in the database
        List<Disease> diseaseList = diseaseRepository.findAll();
        assertThat(diseaseList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Disease in Elasticsearch
        verify(mockDiseaseSearchRepository, times(0)).save(disease);
    }

    @Test
    @Transactional
    public void deleteDisease() throws Exception {
        // Initialize the database
        diseaseRepository.saveAndFlush(disease);

        int databaseSizeBeforeDelete = diseaseRepository.findAll().size();

        // Get the disease
        restDiseaseMockMvc.perform(delete("/api/diseases/{id}", disease.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Disease> diseaseList = diseaseRepository.findAll();
        assertThat(diseaseList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Disease in Elasticsearch
        verify(mockDiseaseSearchRepository, times(1)).deleteById(disease.getId());
    }

    @Test
    @Transactional
    public void searchDisease() throws Exception {
        // Initialize the database
        diseaseRepository.saveAndFlush(disease);
        when(mockDiseaseSearchRepository.search(queryStringQuery("id:" + disease.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(disease), PageRequest.of(0, 1), 1));
        // Search the disease
        restDiseaseMockMvc.perform(get("/api/_search/diseases?query=id:" + disease.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(disease.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].severity").value(hasItem(DEFAULT_SEVERITY.toString())))
            .andExpect(jsonPath("$.[*].symptoms").value(hasItem(DEFAULT_SYMPTOMS)))
            .andExpect(jsonPath("$.[*].tips").value(hasItem(DEFAULT_TIPS)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Disease.class);
        Disease disease1 = new Disease();
        disease1.setId(1L);
        Disease disease2 = new Disease();
        disease2.setId(disease1.getId());
        assertThat(disease1).isEqualTo(disease2);
        disease2.setId(2L);
        assertThat(disease1).isNotEqualTo(disease2);
        disease1.setId(null);
        assertThat(disease1).isNotEqualTo(disease2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DiseaseDTO.class);
        DiseaseDTO diseaseDTO1 = new DiseaseDTO();
        diseaseDTO1.setId(1L);
        DiseaseDTO diseaseDTO2 = new DiseaseDTO();
        assertThat(diseaseDTO1).isNotEqualTo(diseaseDTO2);
        diseaseDTO2.setId(diseaseDTO1.getId());
        assertThat(diseaseDTO1).isEqualTo(diseaseDTO2);
        diseaseDTO2.setId(2L);
        assertThat(diseaseDTO1).isNotEqualTo(diseaseDTO2);
        diseaseDTO1.setId(null);
        assertThat(diseaseDTO1).isNotEqualTo(diseaseDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(diseaseMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(diseaseMapper.fromId(null)).isNull();
    }
}
