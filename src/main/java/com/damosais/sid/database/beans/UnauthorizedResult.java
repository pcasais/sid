package com.damosais.sid.database.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private UnathourizedResultType type;
    
    @Column(name = "adminAccess")
    private Boolean adminAccess;
    
    @Column(name = "userAccess")
    private Boolean userAcces;
    
    @Column(name = "numRegistros")
    private Long numRegistros;
    
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
    
    public Boolean getAdminAccess() {
        return adminAccess;
    }
    
    public Double getAverageTraffic() {
        return averageTraffic;
    }
    
    public ConfidencialityLevel getConfidetialityLevel() {
        return confidetialityLevel;
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
    
    public Long getNumRegistros() {
        return numRegistros;
    }
    
    public Double getPeakTraffic() {
        return peakTraffic;
    }
    
    public UnathourizedResultType getType() {
        return type;
    }
    
    public Boolean getUserAcces() {
        return userAcces;
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
    
    public void setDownTime(Long downTime) {
        this.downTime = downTime;
    }
    
    public void setEconomicImpact(Double economicImpact) {
        this.economicImpact = economicImpact;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setNumRegistros(Long numRegistros) {
        this.numRegistros = numRegistros;
    }
    
    public void setPeakTraffic(Double peakTraffic) {
        this.peakTraffic = peakTraffic;
    }
    
    public void setType(UnathourizedResultType type) {
        this.type = type;
    }
    
    public void setUserAcces(Boolean userAcces) {
        this.userAcces = userAcces;
    }
}