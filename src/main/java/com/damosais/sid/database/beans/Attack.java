package com.damosais.sid.database.beans;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * This class represents an attack in the Common Language which is defined as the use of a tool to exploit a vulnerability producing a set of events which lead
 * to an unauthorised result
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Attacks")
public class Attack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "toolId")
    private Tool tool;
    
    @ManyToOne
    @JoinColumn(name = "vulnerabilityId")
    private Vulnerability vulnerability;
    
    @OneToMany(mappedBy = "attack", fetch = FetchType.EAGER)
    @OrderBy("date asc")
    private Set<Event> events;

    @OneToOne(fetch = FetchType.EAGER)
    private UnauthorizedResult unauthorizedResults;

    @ManyToOne
    @JoinColumn(name = "incidentId")
    private Incident incident;

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
        Date maxDate = null;
        if (events != null && !events.isEmpty()) {
            maxDate = Collections.max(events, Comparator.comparing(c -> c.getDate())).getDate();
        }
        return maxDate;
    }
    
    public Set<Event> getEvents() {
        return events;
    }
    
    public Long getId() {
        return id;
    }

    public Incident getIncident() {
        return incident;
    }

    public Date getStart() {
        Date minDate = null;
        if (events != null && !events.isEmpty()) {
            minDate = Collections.min(events, Comparator.comparing(c -> c.getDate())).getDate();
        }
        return minDate;
    }

    public Tool getTool() {
        return tool;
    }
    
    public UnauthorizedResult getUnauthorizedResults() {
        return unauthorizedResults;
    }
    
    public Date getUpdated() {
        return updated;
    }
    
    public User getUpdatedBy() {
        return updatedBy;
    }
    
    public Vulnerability getVulnerability() {
        return vulnerability;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public void setEvents(Set<Event> events) {
        this.events = events;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setIncident(Incident incident) {
        this.incident = incident;
    }
    
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    
    public void setUnauthorizedResults(UnauthorizedResult unauthorizedResults) {
        this.unauthorizedResults = unauthorizedResults;
    }
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public void setVulnerability(Vulnerability vulnerability) {
        this.vulnerability = vulnerability;
    }
    
}