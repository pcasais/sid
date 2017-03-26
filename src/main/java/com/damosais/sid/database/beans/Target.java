package com.damosais.sid.database.beans;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.neovisionaries.i18n.CountryCode;

/**
 * This class represents a target of an event which is defined as a site owned by an owner (victim) which has a set of public IPs and sits in a country
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Targets", uniqueConstraints = @UniqueConstraint(columnNames = { "siteName" }))
public class Target {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "ownerId", nullable = false)
    private Owner owner;
    
    @Column(name = "siteName", nullable = false)
    private String siteName;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "TargetIps", joinColumns = @JoinColumn(name = "targetId"))
    @Column(name = "targetIpsId")
    private List<String> ips;

    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false)
    private CountryCode country;

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
        final Target other = (Target) obj;
        if (country != other.country) {
            return false;
        }
        if (ips == null) {
            if (other.ips != null) {
                return false;
            }
        } else if (!StringUtils.join(ips, ",").equals(StringUtils.join(other.ips, ","))) {
            return false;
        }
        if (siteName == null) {
            if (other.siteName != null) {
                return false;
            }
        } else if (!siteName.equals(other.siteName)) {
            return false;
        }
        return true;
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

    public List<String> getIps() {
        return ips;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getSiteName() {
        return siteName;
    }

    public Date getUpdated() {
        return updated;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (country == null ? 0 : country.hashCode());
        result = prime * result + (ips == null ? 0 : StringUtils.join(ips, ",").hashCode());
        result = prime * result + (siteName == null ? 0 : siteName.hashCode());
        return result;
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

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
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