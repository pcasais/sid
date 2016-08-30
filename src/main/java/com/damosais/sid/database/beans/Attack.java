package com.damosais.sid.database.beans;

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
import javax.persistence.OrderBy;
import javax.persistence.Table;

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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "start")
    private Date start;
    
    @Column(name = "end")
    private Date end;
    
    @ManyToOne
    @JoinColumn(name = "toolId")
    private Tool tool;
    
    @ManyToOne
    @JoinColumn(name = "vulnerabilityId")
    private Vulnerability vulnerability;
    
    @OneToMany(mappedBy = "attack", fetch = FetchType.EAGER)
    @OrderBy("date asc")
    private Set<Event> events;
    
    @OneToMany
    @JoinColumn(name = "attackId")
    private Set<UnauthorizedResult> unauthorizedResults;
    
    @ManyToOne
    @JoinColumn(name = "incidentId")
    private Incident incident;
    
    public Date getEnd() {
        return end;
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
        return start;
    }
    
    public Tool getTool() {
        return tool;
    }
    
    public Set<UnauthorizedResult> getUnauthorizedResults() {
        return unauthorizedResults;
    }
    
    public Vulnerability getVulnerability() {
        return vulnerability;
    }
    
    public void setEnd(Date end) {
        this.end = end;
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
    
    public void setStart(Date start) {
        this.start = start;
    }
    
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    
    public void setUnauthorizedResults(Set<UnauthorizedResult> unauthorizedResults) {
        this.unauthorizedResults = unauthorizedResults;
    }
    
    public void setVulnerability(Vulnerability vulnerability) {
        this.vulnerability = vulnerability;
    }
    
}