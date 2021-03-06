package com.damosais.sid.database.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "CorrelationResults")
public class CorrelationResult {
    public static final double NON_NULL_HYPOTHESIS_LEVEL = 0.05d;
    public static final double SIGNIFICATIVE_LEVEL = 0.8d;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "country", nullable = false)
    private CountryCode country;
    
    @Column(name = "variable", nullable = false)
    private SocioeconomicVariable variable;
    
    @Column(name = "pearsonCorrelationCoefficient", nullable = false)
    private double pearsonCorrelationCoefficient;
    
    @Column(name = "pValuePearson", nullable = false)
    private double pValuePearson;

    @Column(name = "spearmanCorrelationCoefficient", nullable = false)
    private double spearmanCorrelationCoefficient;
    
    @Column(name = "pValueSpearman", nullable = false)
    private double pValueSpearman;

    @Column(name = "standardError", nullable = false)
    private double standardError;

    @Column(name = "interpolated", nullable = false)
    private boolean interpolatedData;

    @Column(name = "valuesNormality", nullable = false)
    private double valuesNormality;

    @Column(name = "eventsNormality", nullable = false)
    private double eventsNormality;

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
    
    public CountryCode getCountry() {
        return country;
    }
    
    public Date getCreated() {
        return created;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public double getEventsNormality() {
        return eventsNormality;
    }

    public Long getId() {
        return id;
    }

    public double getPearsonCorrelationCoefficient() {
        return pearsonCorrelationCoefficient;
    }
    
    public double getpValuePearson() {
        return pValuePearson;
    }
    
    public double getpValueSpearman() {
        return pValueSpearman;
    }
    
    public double getSpearmanCorrelationCoefficient() {
        return spearmanCorrelationCoefficient;
    }

    public double getStandardError() {
        return standardError;
    }

    public Date getUpdated() {
        return updated;
    }
    
    public User getUpdatedBy() {
        return updatedBy;
    }

    public double getValuesNormality() {
        return valuesNormality;
    }
    
    public SocioeconomicVariable getVariable() {
        return variable;
    }
    
    public boolean isInterpolatedData() {
        return interpolatedData;
    }

    public void setCountry(CountryCode country) {
        this.country = country;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public void setEventsNormality(double eventsNormality) {
        this.eventsNormality = eventsNormality;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setInterpolatedData(boolean interpolatedData) {
        this.interpolatedData = interpolatedData;
    }
    
    public void setPearsonCorrelationCoefficient(double pearsonCorrelationCoefficient) {
        this.pearsonCorrelationCoefficient = pearsonCorrelationCoefficient;
    }
    
    public void setpValuePearson(double pValuePearson) {
        this.pValuePearson = pValuePearson;
    }
    
    public void setpValueSpearman(double pValueSpearman) {
        this.pValueSpearman = pValueSpearman;
    }
    
    public void setSpearmanCorrelationCoefficient(double spearmanCorrelationCoefficient) {
        this.spearmanCorrelationCoefficient = spearmanCorrelationCoefficient;
    }
    
    public void setStandardError(double standardError) {
        this.standardError = standardError;
    }
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public void setValuesNormality(double valuesNormality) {
        this.valuesNormality = valuesNormality;
    }
    
    public void setVariable(SocioeconomicVariable variable) {
        this.variable = variable;
    }
}