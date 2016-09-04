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
 * This class represents the types of range in a CVE definition
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "RangeTypes")
public class RangeType {
    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "definition"))
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private CVEDefinition definition;

    @Column(name = "local", nullable = false)
    private Boolean local = false;
    
    @Column(name = "localNetwork", nullable = false)
    private Boolean localNetwork = false;
    
    @Column(name = "network", nullable = false)
    private Boolean network = false;
    
    @Column(name = "userInit", nullable = false)
    private Boolean userInit = false;
    
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
        final RangeType other = (RangeType) obj;
        if (local == null) {
            if (other.local != null) {
                return false;
            }
        } else if (!local.equals(other.local)) {
            return false;
        }
        if (localNetwork == null) {
            if (other.localNetwork != null) {
                return false;
            }
        } else if (!localNetwork.equals(other.localNetwork)) {
            return false;
        }
        if (network == null) {
            if (other.network != null) {
                return false;
            }
        } else if (!network.equals(other.network)) {
            return false;
        }
        if (userInit == null) {
            if (other.userInit != null) {
                return false;
            }
        } else if (!userInit.equals(other.userInit)) {
            return false;
        }
        return true;
    }
    
    public CVEDefinition getDefinition() {
        return definition;
    }
    
    public Long getId() {
        return id;
    }
    
    public Boolean getLocal() {
        return local;
    }
    
    public Boolean getLocalNetwork() {
        return localNetwork;
    }
    
    public Boolean getNetwork() {
        return network;
    }
    
    public Boolean getUserInit() {
        return userInit;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (local == null ? 0 : local.hashCode());
        result = prime * result + (localNetwork == null ? 0 : localNetwork.hashCode());
        result = prime * result + (network == null ? 0 : network.hashCode());
        result = prime * result + (userInit == null ? 0 : userInit.hashCode());
        return result;
    }
    
    public void setDefinition(CVEDefinition definition) {
        this.definition = definition;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setLocal(Boolean local) {
        this.local = local;
    }
    
    public void setLocalNetwork(Boolean localNetwork) {
        this.localNetwork = localNetwork;
    }
    
    public void setNetwork(Boolean network) {
        this.network = network;
    }
    
    public void setUserInit(Boolean userInit) {
        this.userInit = userInit;
    }
    
    @Override
    public String toString() {
        return "Local(" + local + ") LocalNetwork(" + localNetwork + ") Network(" + network + ") UserInit(" + userInit + ")";
    }
}