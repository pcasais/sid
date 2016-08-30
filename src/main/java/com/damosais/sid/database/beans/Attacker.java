package com.damosais.sid.database.beans;

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
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.neovisionaries.i18n.CountryCode;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents an attacker on an incident. It is characterised by its name, country and type
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Attackers")
public class Attacker {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @XStreamAlias("numeric")
    private CountryCode country;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AttackerType type;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "attackers", fetch = FetchType.LAZY)
    @OrderBy("start desc")
    private Set<Incident> incidents;

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
        final Attacker other = (Attacker) obj;
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
        if (type != other.type) {
            return false;
        }
        return true;
    }

    public CountryCode getCountry() {
        return country;
    }

    public Long getId() {
        return id;
    }

    public Set<Incident> getIncidents() {
        return incidents;
    }

    public String getName() {
        return name;
    }

    public AttackerType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (country == null ? 0 : country.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (type == null ? 0 : type.hashCode());
        return result;
    }

    public void setCountry(CountryCode country) {
        this.country = country;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIncidents(Set<Incident> incidents) {
        this.incidents = incidents;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(AttackerType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + (country != null ? " - " + country.getName() : "") + (type != null ? " - " + type.getDescription() : "");
    }
}