package com.damosais.sid.database.beans;

import java.util.Collections;
import java.util.Comparator;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * This class represents an incident in the Common Language which is defined as an attacker launching a set of attacks with a specific motivation
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Incidents", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(targetEntity = Attacker.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "IncidentAttackers", joinColumns = @JoinColumn(name = "incidentId"), inverseJoinColumns = @JoinColumn(name = "attackerId"))
    private Set<Attacker> attackers;
    
    @OneToMany(mappedBy = "incident", fetch = FetchType.EAGER)
    private Set<Attack> attacks;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "motivation")
    private Motivation motivation;
    
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
        final Incident other = (Incident) obj;
        if (attackers == null) {
            if (other.attackers != null) {
                return false;
            }
        } else if (!attackers.equals(other.attackers)) {
            return false;
        }
        if (motivation != other.motivation) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
    public Set<Attacker> getAttackers() {
        return attackers;
    }
    
    public Set<Attack> getAttacks() {
        return attacks;
    }
    
    public Date getCreated() {
        return created;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public Date getEnd() {
        Date maxDate = null;
        if (attacks != null && !attacks.isEmpty()) {
            maxDate = Collections.max(attacks, Comparator.comparing(c -> c.getEnd())).getEnd();
        }
        return maxDate;
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
        Date minDate = null;
        if (attacks != null && !attacks.isEmpty()) {
            minDate = Collections.min(attacks, Comparator.comparing(c -> c.getStart())).getStart();
        }
        return minDate;
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
        result = prime * result + (attackers == null ? 0 : attackers.hashCode());
        result = prime * result + (motivation == null ? 0 : motivation.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    public void setAttackers(Set<Attacker> attackers) {
        this.attackers = attackers;
    }

    public void setAttacks(Set<Attack> attacks) {
        this.attacks = attacks;
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

    public void setMotivation(Motivation motivation) {
        this.motivation = motivation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
}