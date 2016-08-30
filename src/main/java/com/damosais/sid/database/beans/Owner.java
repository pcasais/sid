package com.damosais.sid.database.beans;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.neovisionaries.i18n.CountryCode;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents the owner of a target (also known as victim)
 * 
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Owners")
public class Owner {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @XStreamAlias("numeric")
    private CountryCode country;

    @Enumerated(EnumType.STRING)
    @Column(name = "sector")
    private Sector sector;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<Target> targets;

    public CountryCode getCountry() {
        return country;
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

    public void setCountry(CountryCode country) {
        this.country = country;
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
}