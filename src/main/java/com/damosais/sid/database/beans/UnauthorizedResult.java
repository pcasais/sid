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

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * This class represents the unauthorised result as defined in a CVE definition
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "UnauthorizedResults")
public class UnauthorizedResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UnathourizedResultType type;
    
    @Column(name = "adminAccess")
    private Boolean adminAccess;
    
    @Column(name = "userAccess")
    private Boolean userAccess;
    
    @Column(name = "numRegisters")
    private Long numRegisters;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "confidetialityLevel")
    private ConfidencialityLevel confidetialityLevel;
    
    @Column(name = "downTime")
    private Long downTime;
    
    @Column(name = "averageTraffic")
    private Double averageTraffic;
    
    @Column(name = "peakTraffic")
    private Double peakTraffic;
    
    @Column(name = "economicImpact")
    private Double economicImpact;
    
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
    
    public Boolean getAdminAccess() {
        return adminAccess;
    }

    public Double getAverageTraffic() {
        return averageTraffic;
    }

    public ConfidencialityLevel getConfidetialityLevel() {
        return confidetialityLevel;
    }

    public Date getCreated() {
        return created;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Long getDownTime() {
        return downTime;
    }

    public Double getEconomicImpact() {
        return economicImpact;
    }

    public Long getId() {
        return id;
    }

    public Long getNumRegisters() {
        return numRegisters;
    }
    
    public Double getPeakTraffic() {
        return peakTraffic;
    }
    
    public UnathourizedResultType getType() {
        return type;
    }
    
    public Date getUpdated() {
        return updated;
    }
    
    public User getUpdatedBy() {
        return updatedBy;
    }
    
    public Boolean getUserAccess() {
        return userAccess;
    }
    
    public void setAdminAccess(Boolean adminAccess) {
        this.adminAccess = adminAccess;
    }
    
    public void setAverageTraffic(Double averageTraffic) {
        this.averageTraffic = averageTraffic;
    }
    
    public void setConfidetialityLevel(ConfidencialityLevel confidetialityLevel) {
        this.confidetialityLevel = confidetialityLevel;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public void setDownTime(Long downTime) {
        this.downTime = downTime;
    }
    
    public void setEconomicImpact(Double economicImpact) {
        this.economicImpact = economicImpact;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setNumRegisters(Long numRegisters) {
        this.numRegisters = numRegisters;
    }
    
    public void setPeakTraffic(Double peakTraffic) {
        this.peakTraffic = peakTraffic;
    }
    
    public void setType(UnathourizedResultType type) {
        this.type = type;
    }
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public void setUserAccess(Boolean userAccess) {
        this.userAccess = userAccess;
    }
}