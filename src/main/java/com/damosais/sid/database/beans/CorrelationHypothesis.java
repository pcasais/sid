package com.damosais.sid.database.beans;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.neovisionaries.i18n.CountryCode;

/**
 * This class represents a correlation hyphotesis we want to test
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "CorrelationHypothesis")
public class CorrelationHypothesis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "startDate", nullable = false)
    private Date startDate;
    
    @Column(name = "endDate", nullable = false)
    private Date endDate;
    
    @Column(name = "sector", nullable = false)
    private Sector sector;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<CountryCode> targetCountries;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<CountryCode> sourceCountries;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<SocioeconomicVariable> variables;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<CorrelationResult> results;
    
    @CreationTimestamp
    @Column(name = "created")
    private Date created;
    
    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    private User createdBy;
    
    @UpdateTimestamp
    @Column(name = "lastUpdate")
    private Date updated;
    
    @ManyToOne
    @JoinColumn(name = "updatedBy")
    private User updatedBy;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CorrelationHypothesis other = (CorrelationHypothesis) obj;
        if (endDate == null) {
            if (other.endDate != null) {
                return false;
            }
        } else if (!endDate.equals(other.endDate)) {
            return false;
        }
        if (sector != other.sector) {
            return false;
        }
        if (sourceCountries == null) {
            if (other.sourceCountries != null && !other.sourceCountries.isEmpty()) {
                return false;
            }
        } else if (!sourceCountries.equals(other.sourceCountries)) {
            return false;
        }
        if (targetCountries == null) {
            if (other.targetCountries != null && !other.targetCountries.isEmpty()) {
                return false;
            }
        } else if (!targetCountries.equals(other.targetCountries)) {
            return false;
        }
        if (variables == null) {
            if (other.variables != null) {
                return false;
            }
        } else if (!variables.equals(other.variables)) {
            return false;
        }
        return true;
    }

    public double getBestCorrelation() {
        double bestResult = 0;
        if (results != null && !results.isEmpty()) {
            for (final CorrelationResult result : results) {
                if (Math.abs(result.getCorrelationCoefficient()) > Math.abs(bestResult)) {
                    bestResult = result.getCorrelationCoefficient();
                }
            }
        }
        return bestResult;
    }

    public Date getCreated() {
        return created;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public Long getId() {
        return id;
    }
    
    public Set<CorrelationResult> getResults() {
        return results;
    }
    
    public Sector getSector() {
        return sector;
    }
    
    public Set<CountryCode> getSourceCountries() {
        return sourceCountries;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public Set<CountryCode> getTargetCountries() {
        return targetCountries;
    }
    
    public Date getUpdated() {
        return updated;
    }
    
    public User getUpdatedBy() {
        return updatedBy;
    }

    public Set<SocioeconomicVariable> getVariables() {
        return variables;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (endDate == null ? 0 : endDate.hashCode());
        result = prime * result + (sector == null ? 0 : sector.hashCode());
        result = prime * result + (sourceCountries == null || sourceCountries.isEmpty() ? 0 : sourceCountries.hashCode());
        result = prime * result + (targetCountries == null || targetCountries.isEmpty() ? 0 : targetCountries.hashCode());
        result = prime * result + (variables == null ? 0 : variables.hashCode());
        return result;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setResults(Set<CorrelationResult> results) {
        this.results = results;
    }
    
    public void setSector(Sector sector) {
        this.sector = sector;
    }
    
    public void setSourceCountries(Set<CountryCode> sourceCountries) {
        this.sourceCountries = sourceCountries;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setTargetCountries(Set<CountryCode> targetCountries) {
        this.targetCountries = targetCountries;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setVariables(Set<SocioeconomicVariable> variables) {
        this.variables = variables;
    }
}