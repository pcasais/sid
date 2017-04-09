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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "country", nullable = false)
    private CountryCode country;

    @Column(name = "variable", nullable = false)
    private SocioeconomicVariable variable;

    @Column(name = "correlationCoefficient", nullable = false)
    private double correlationCoefficient;

    @Column(name = "pValue", nullable = false)
    private double pValue;

    @Column(name = "interpolated", nullable = false)
    private boolean interpolatedData;

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
    
    public double getCorrelationCoefficient() {
        return correlationCoefficient;
    }

    public CountryCode getCountry() {
        return country;
    }
    
    public Date getCreated() {
        return created;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Long getId() {
        return id;
    }
    
    public double getpValue() {
        return pValue;
    }

    public Date getUpdated() {
        return updated;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public SocioeconomicVariable getVariable() {
        return variable;
    }

    public boolean isInterpolatedData() {
        return interpolatedData;
    }

    public void setCorrelationCoefficient(double correlationCoefficient) {
        this.correlationCoefficient = correlationCoefficient;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setInterpolatedData(boolean interpolatedData) {
        this.interpolatedData = interpolatedData;
    }

    public void setpValue(double pValue) {
        this.pValue = pValue;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setVariable(SocioeconomicVariable variable) {
        this.variable = variable;
    }
}