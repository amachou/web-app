package com.jamalahi.amachou.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.jamalahi.amachou.domain.enumeration.DiseaseSeverity;

/**
 * A DTO for the Disease entity.
 */
public class DiseaseDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private DiseaseSeverity severity;

    @NotNull
    private String symptoms;

    @NotNull
    private String tips;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiseaseSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(DiseaseSeverity severity) {
        this.severity = severity;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DiseaseDTO diseaseDTO = (DiseaseDTO) o;
        if (diseaseDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), diseaseDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DiseaseDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", symptoms='" + getSymptoms() + "'" +
            ", tips='" + getTips() + "'" +
            "}";
    }
}
