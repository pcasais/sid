package com.damosais.sid.database.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.neovisionaries.i18n.CountryCode;

/**
 * This class represents the value of a variable we are studying for a country
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "CountryVariableValues", uniqueConstraints = @UniqueConstraint(columnNames = { "variable", "country", "date" }))
public class CountryVariableValue {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "variable", nullable = false)
    private SocioeconomicVariable variable;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false)
    private CountryCode country;
    
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "value", nullable = false)
    private Double value;
    
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
    
    public Date getDate() {
        return date;
    }
    
    public Long getId() {
        return id;
    }
    
    public Date getUpdated() {
        return updated;
    }
    
    public User getUpdatedBy() {
        return updatedBy;
    }
    
    public Double getValue() {
        return value;
    }
    
    public SocioeconomicVariable getVariable() {
        return variable;
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
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    public void setVariable(SocioeconomicVariable variable) {
        this.variable = variable;
    }
}