package com.jamalahi.amachou.service.mapper;

import com.jamalahi.amachou.domain.*;
import com.jamalahi.amachou.service.dto.HospitalDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Hospital and its DTO HospitalDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface HospitalMapper extends EntityMapper<HospitalDTO, Hospital> {



    default Hospital fromId(Long id) {
        if (id == null) {
            return null;
        }
        Hospital hospital = new Hospital();
        hospital.setId(id);
        return hospital;
    }
}
