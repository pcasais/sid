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
    @GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "definition"))
    @Id
    @GeneratedValue(generator = "generator")
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private CVEDefinition definition;
    
    @Column(name = "local")
    private Boolean local;
    
    @Column(name = "localNetwork")
    private Boolean localNetwork;
    
    @Column(name = "network")
    private Boolean network;
    
    @Column(name = "userInit")
    private Boolean userInit;
    
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