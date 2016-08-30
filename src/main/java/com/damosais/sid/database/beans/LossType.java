package com.damosais.sid.database.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * This class represents a loss type as defined in a CVE
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "LossTypes")
public class LossType {
    @GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "definition"))
    @Id
    @GeneratedValue(generator = "generator")
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private CVEDefinition definition;

    @Column(name = "availability")
    private Boolean availability;

    @Column(name = "confidentiality")
    private Boolean confidentiality;
    
    @Column(name = "integrity")
    private Boolean integrity;
    
    @Column(name = "adminSecurityProtection")
    private Boolean adminSecurityProtection;
    
    @Column(name = "userSecurityProtection")
    private Boolean userSecurityProtection;
    
    @Column(name = "otherSecurityProtection")
    private Boolean otherSecurityProtection;
    
    public Boolean getAdminSecurityProtection() {
        return adminSecurityProtection;
    }
    
    public Boolean getAvailability() {
        return availability;
    }
    
    public Boolean getConfidentiality() {
        return confidentiality;
    }
    
    public CVEDefinition getDefinition() {
        return definition;
    }
    
    public Long getId() {
        return id;
    }
    
    public Boolean getIntegrity() {
        return integrity;
    }
    
    public Boolean getOtherSecurityProtection() {
        return otherSecurityProtection;
    }
    
    public Boolean getUserSecurityProtection() {
        return userSecurityProtection;
    }
    
    public void setAdminSecurityProtection(Boolean adminSecurityProtection) {
        this.adminSecurityProtection = adminSecurityProtection;
    }
    
    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }
    
    public void setConfidentiality(Boolean confidenciality) {
        confidentiality = confidenciality;
    }
    
    public void setDefinition(CVEDefinition definition) {
        this.definition = definition;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setIntegrity(Boolean integrity) {
        this.integrity = integrity;
    }
    
    public void setOtherSecurityProtection(Boolean otherSecurityProtection) {
        this.otherSecurityProtection = otherSecurityProtection;
    }
    
    public void setUserSecurityProtection(Boolean userSecurityProtection) {
        this.userSecurityProtection = userSecurityProtection;
    }
    
    @Override
    public String toString() {
        return "Avail(" + availability + ") Confid(" + confidentiality + ") Integr(" + integrity + ") AdminSec(" + adminSecurityProtection + ") OtherSec(" + otherSecurityProtection + ") UserSec(" + userSecurityProtection + ")";
    }
}