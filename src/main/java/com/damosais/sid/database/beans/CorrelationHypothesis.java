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
    
    @Column(name = "startDate")
    private Date startDate;
    
    @Column(name = "endDate")
    private Date endDate;
    
    @ManyToOne
    @JoinColumn(name = "attackId")
    private Conflict conflict;
    
    @Column(name = "sector", nullable = false)
    private Sector sector;

    @Column(name = "targetCountry", nullable = false)
    private CountryCode targetCountry;

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
        if (startDate == null) {
            if (other.startDate != null) {
                return false;
            }
        } else if (!startDate.equals(other.startDate)) {
            return false;
        }
        if (endDate == null) {
            if (other.endDate != null) {
                return false;
            }
        } else if (!endDate.equals(other.endDate)) {
            return false;
        }
        if (conflict == null) {
            if (other.conflict != null) {
                return false;
            }
        } else if (!conflict.equals(other.conflict)) {
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
        if (targetCountry == null) {
            if (other.targetCountry != null) {
                return false;
            }
        } else if (targetCountry != other.targetCountry) {
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
    
    public Conflict getConflict() {
        return conflict;
    }

    public Date getCreated() {
        return created;
    }

    public User getCreatedBy() {
        return createdBy;
    }
    
    /**
     * This method returns the effective end date to be used which is: if the hypothesis uses a conflict either the end date of it doesn't have one today, if is
     * not based on a conflict then is end date defined
     *
     * @return The end date of the conflict (or today if none defined) or the end date if no conflict assigned
     */
    public Date getEffectiveEndDate() {
        if (conflict != null) {
            return conflict.getEnd() != null ? conflict.getEnd() : new Date();
        } else {
            return getEndDate();
        }
    }
    
    /**
     * This method returns the countries involved in the attacks
     *
     * @return If the hypothesis is based on a conflict it returns the parties involved, otherwise the source countries defined
     */
    public Set<CountryCode> getEffectiveSourceCountries() {
        if (conflict != null) {
            return conflict.getPartiesInvolved();
        } else {
            return sourceCountries;
        }
    }
    
    /**
     * This method returns the effective start date to be used which is: if the hypothesis uses a conflict either the start date, if is not based on a conflict
     * then is start date defined
     *
     * @return The start date of the conflict r the start date if no conflict assigned
     */
    public Date getEffectiveStartDate() {
        if (conflict != null) {
            return conflict.getStart();
        } else {
            return getStartDate();
        }
    }
    
    /**
     * This method returns the location where the conflict is taking place or if there's no conflict the selected target countries
     *
     * @return the location where the conflict is taking place or if there's no conflict the selected target countries
     */
    public CountryCode getEffectiveTargetCountry() {
        if (conflict != null) {
            return conflict.getLocation();
        } else {
            return getTargetCountry();
        }
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
    
    public CountryCode getTargetCountry() {
        return targetCountry;
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
        result = prime * result + (startDate == null ? 0 : startDate.hashCode());
        result = prime * result + (endDate == null ? 0 : endDate.hashCode());
        result = prime * result + (conflict == null ? 0 : conflict.hashCode());
        result = prime * result + (sector == null ? 0 : sector.hashCode());
        result = prime * result + (sourceCountries == null || sourceCountries.isEmpty() ? 0 : sourceCountries.hashCode());
        result = prime * result + (targetCountry == null ? 0 : targetCountry.hashCode());
        result = prime * result + (variables == null ? 0 : variables.hashCode());
        return result;
    }
    
    public void setConflict(Conflict conflict) {
        this.conflict = conflict;
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
    
    public void setTargetCountry(CountryCode targetCountry) {
        this.targetCountry = targetCountry;
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