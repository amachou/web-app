package com.jamalahi.amachou.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.jamalahi.amachou.domain.enumeration.HospitalSize;

/**
 * A DTO for the Hospital entity.
 */
public class HospitalDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Double lat;

    @NotNull
    private Double lon;

    @NotNull
    private HospitalSize size;

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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public HospitalSize getSize() {
        return size;
    }

    public void setSize(HospitalSize size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HospitalDTO hospitalDTO = (HospitalDTO) o;
        if (hospitalDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), hospitalDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "HospitalDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", lat=" + getLat() +
            ", lon=" + getLon() +
            ", size='" + getSize() + "'" +
            "}";
    }
}
