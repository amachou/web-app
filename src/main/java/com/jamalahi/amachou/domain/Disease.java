package com.jamalahi.amachou.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

import com.jamalahi.amachou.domain.enumeration.DiseaseSeverity;

/**
 * A Disease.
 */
@Entity
@Table(name = "disease")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "disease")
public class Disease implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private DiseaseSeverity severity;

    @Size(max = 10000000)
    @Column(name = "description", length = 10000000)
    private String description;

    @NotNull
    @Size(max = 10000000)
    @Column(name = "symptoms", length = 10000000, nullable = false)
    private String symptoms;

    @Size(max = 10000000)
    @Column(name = "tips", length = 10000000)
    private String tips;

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

    public Disease name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiseaseSeverity getSeverity() {
        return severity;
    }

    public Disease severity(DiseaseSeverity severity) {
        this.severity = severity;
        return this;
    }

    public void setSeverity(DiseaseSeverity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public Disease description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public Disease symptoms(String symptoms) {
        this.symptoms = symptoms;
        return this;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getTips() {
        return tips;
    }

    public Disease tips(String tips) {
        this.tips = tips;
        return this;
    }

    public void setTips(String tips) {
        this.tips = tips;
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
        Disease disease = (Disease) o;
        if (disease.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), disease.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Disease{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", description='" + getDescription() + "'" +
            ", symptoms='" + getSymptoms() + "'" +
            ", tips='" + getTips() + "'" +
            "}";
    }
}
