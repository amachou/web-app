package com.jamalahi.amachou.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

import com.jamalahi.amachou.domain.enumeration.HospitalSize;

/**
 * A Hospital.
 */
@Entity
@Table(name = "hospital")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "hospital")
public class Hospital implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "lat", nullable = false)
    private Double lat;

    @NotNull
    @Column(name = "lon", nullable = false)
    private Double lon;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_size", nullable = false)
    private HospitalSize size;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Hospital name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public Hospital lat(Double lat) {
        this.lat = lat;
        return this;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public Hospital lon(Double lon) {
        this.lon = lon;
        return this;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public HospitalSize getSize() {
        return size;
    }

    public Hospital size(HospitalSize size) {
        this.size = size;
        return this;
    }

    public void setSize(HospitalSize size) {
        this.size = size;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Hospital hospital = (Hospital) o;
        if (hospital.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), hospital.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Hospital{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", lat=" + getLat() +
            ", lon=" + getLon() +
            ", size='" + getSize() + "'" +
            "}";
    }
}
