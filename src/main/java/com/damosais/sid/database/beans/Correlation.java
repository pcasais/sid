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
 * This class represents a military conflict.
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Correlations")
public class Correlation {
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

    @Column(name = "coeficient")
    private double coeficient;

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

    public double getCoeficient() {
        return coeficient;
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

    public void setCoeficient(double coeficient) {
        this.coeficient = coeficient;
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