package com.damosais.sid.database.beans;

import java.util.Date;
import java.util.Set;

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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.neovisionaries.i18n.CountryCode;

/**
 * This class represents a military conflict.
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Conflicts", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class Conflict {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "start", nullable = false)
    private Date start;
    
    @Column(name = "end", nullable = true)
    private Date end;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "location", nullable = false)
    private CountryCode location;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<CountryCode> partiesInvolved;
    
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
    
    public Date getCreated() {
        return created;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Date getEnd() {
        return end;
    }

    public Long getId() {
        return id;
    }

    public CountryCode getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public Set<CountryCode> getPartiesInvolved() {
        return partiesInvolved;
    }

    public Date getStart() {
        return start;
    }

    public Date getUpdated() {
        return updated;
    }
    
    public User getUpdatedBy() {
        return updatedBy;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public void setEnd(Date end) {
        this.end = end;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setLocation(CountryCode location) {
        this.location = location;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPartiesInvolved(Set<CountryCode> partiesInvolved) {
        this.partiesInvolved = partiesInvolved;
    }
    
    public void setStart(Date start) {
        this.start = start;
    }
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return name;
    }
}