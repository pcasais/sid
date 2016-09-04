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
    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "definition"))
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private CVEDefinition definition;

    @Column(name = "availability", nullable = false)
    private Boolean availability = false;
    
    @Column(name = "confidentiality", nullable = false)
    private Boolean confidentiality = false;
    
    @Column(name = "integrity", nullable = false)
    private Boolean integrity = false;
    
    @Column(name = "adminSecurityProtection", nullable = false)
    private Boolean adminSecurityProtection = false;

    @Column(name = "userSecurityProtection", nullable = false)
    private Boolean userSecurityProtection = false;

    @Column(name = "otherSecurityProtection", nullable = false)
    private Boolean otherSecurityProtection = false;

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
        final LossType other = (LossType) obj;
        if (adminSecurityProtection == null) {
            if (other.adminSecurityProtection != null) {
                return false;
            }
        } else if (!adminSecurityProtection.equals(other.adminSecurityProtection)) {
            return false;
        }
        if (availability == null) {
            if (other.availability != null) {
                return false;
            }
        } else if (!availability.equals(other.availability)) {
            return false;
        }
        if (confidentiality == null) {
            if (other.confidentiality != null) {
                return false;
            }
        } else if (!confidentiality.equals(other.confidentiality)) {
            return false;
        }
        if (integrity == null) {
            if (other.integrity != null) {
                return false;
            }
        } else if (!integrity.equals(other.integrity)) {
            return false;
        }
        if (otherSecurityProtection == null) {
            if (other.otherSecurityProtection != null) {
                return false;
            }
        } else if (!otherSecurityProtection.equals(other.otherSecurityProtection)) {
            return false;
        }
        if (userSecurityProtection == null) {
            if (other.userSecurityProtection != null) {
                return false;
            }
        } else if (!userSecurityProtection.equals(other.userSecurityProtection)) {
            return false;
        }
        return true;
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (adminSecurityProtection == null ? 0 : adminSecurityProtection.hashCode());
        result = prime * result + (availability == null ? 0 : availability.hashCode());
        result = prime * result + (confidentiality == null ? 0 : confidentiality.hashCode());
        result = prime * result + (integrity == null ? 0 : integrity.hashCode());
        result = prime * result + (otherSecurityProtection == null ? 0 : otherSecurityProtection.hashCode());
        result = prime * result + (userSecurityProtection == null ? 0 : userSecurityProtection.hashCode());
        return result;
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