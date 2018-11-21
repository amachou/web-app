package com.jamalahi.amachou.service;

import com.jamalahi.amachou.service.dto.DiseaseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Disease.
 */
public interface DiseaseService {

    /**
     * Save a disease.
     *
     * @param diseaseDTO the entity to save
     * @return the persisted entity
     */
    DiseaseDTO save(DiseaseDTO diseaseDTO);

    /**
     * Get all the diseases.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<DiseaseDTO> findAll(Pageable pageable);


    /**
     * Get the "id" disease.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<DiseaseDTO> findOne(Long id);

    /**
     * Delete the "id" disease.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the disease corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<DiseaseDTO> search(String query, Pageable pageable);
}
