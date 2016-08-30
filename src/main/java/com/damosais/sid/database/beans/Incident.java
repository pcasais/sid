package com.damosais.sid.database.beans;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * This class represents an incident in the Common Language which is defined as an attacker launching a set of attacks with a specific motivation
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Incidents")
public class Incident {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "start")
    private Date start;

    @Column(name = "end")
    private Date end;

    @Column(name = "name")
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "IncidentAttackers", joinColumns = @JoinColumn(name = "incidentId"), inverseJoinColumns = @JoinColumn(name = "attackerId"))
    private Set<Attacker> attackers;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "incident", fetch = FetchType.EAGER)
    @OrderBy("start asc")
    private Set<Attack> attacks;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "motivation")
    private Motivation motivation;

    public Set<Attacker> getAttackers() {
        return attackers;
    }

    public Set<Attack> getAttacks() {
        return attacks;
    }

    public Date getEnd() {
        return end;
    }

    public Long getId() {
        return id;
    }

    public Motivation getMotivation() {
        return motivation;
    }

    public String getName() {
        return name;
    }

    public Date getStart() {
        return start;
    }

    public void setAttackers(Set<Attacker> attackers) {
        this.attackers = attackers;
    }

    public void setAttacks(Set<Attack> attacks) {
        this.attacks = attacks;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMotivation(Motivation motivation) {
        this.motivation = motivation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStart(Date start) {
        this.start = start;
    }
}