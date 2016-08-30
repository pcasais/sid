package com.damosais.sid.database.beans;

import java.util.List;

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

import org.apache.commons.lang3.StringUtils;

import com.neovisionaries.i18n.CountryCode;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents a target of an event which is defined as a site owned by an owner (victim) which has a set of public IPs and sits in a country
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Targets")
public class Target {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "ownerId")
    private Owner owner;
    
    @Column(name = "siteName")
    private String siteName;
    
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private List<String> ips;
    
    @XStreamAlias("numeric")
    private CountryCode country;
    
    public CountryCode getCountry() {
        return country;
    }
    
    public Long getId() {
        return id;
    }
    
    public List<String> getIps() {
        return ips;
    }
    
    public Owner getOwner() {
        return owner;
    }
    
    public String getSiteName() {
        return siteName;
    }
    
    public void setCountry(CountryCode country) {
        this.country = country;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setIps(List<String> ips) {
        this.ips = ips;
    }
    
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(siteName);
        if (country != null) {
            sb.append(" (").append(country.getName()).append(")");
        }
        if (ips != null && !ips.isEmpty()) {
            sb.append(" [").append(StringUtils.join(ips, ",")).append("]");
        }
        return sb.toString();
    }
}