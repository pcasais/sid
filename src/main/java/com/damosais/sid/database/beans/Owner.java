package com.damosais.sid.database.beans;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.neovisionaries.i18n.CountryCode;

/**
 * This class represents the owner of a target (also known as victim)
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Owners", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false)
    private CountryCode country;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sector", nullable = false)
    private Sector sector;
    
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<Target> targets;
    
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
        final Owner other = (Owner) obj;
        if (country != other.country) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (sector != other.sector) {
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

    public String getName() {
        return name;
    }
    
    public Sector getSector() {
        return sector;
    }
    
    public Set<Target> getTargets() {
        return targets;
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
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (sector == null ? 0 : sector.hashCode());
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

    public void setName(String name) {
        this.name = name;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public void setTargets(Set<Target> targets) {
        this.targets = targets;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
}