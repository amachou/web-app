package com.jamalahi.amachou.service.mapper;

import com.jamalahi.amachou.domain.*;
import com.jamalahi.amachou.service.dto.DiseaseDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Disease and its DTO DiseaseDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DiseaseMapper extends EntityMapper<DiseaseDTO, Disease> {



    default Disease fromId(Long id) {
        if (id == null) {
            return null;
        }
        Disease disease = new Disease();
        disease.setId(id);
        return disease;
    }
}
