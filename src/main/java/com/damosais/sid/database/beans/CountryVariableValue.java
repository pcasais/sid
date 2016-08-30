package com.damosais.sid.database.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.neovisionaries.i18n.CountryCode;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents the value of a variable we are studying for a country
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "CountryVariableValues")
public class CountryVariableValue {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "variable", nullable = false)
    @XStreamAlias("numeric")
    private SocioeconomicVariable variable;

    @Column(name = "country", nullable = false)
    @XStreamAlias("numeric")
    private CountryCode country;

    @Column(name = "date", nullable = false)
    private Date date;
    
    @Column(name = "value", nullable = false)
    private Double value;
    
    public CountryCode getCountry() {
        return country;
    }

    public Date getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public Double getValue() {
        return value;
    }

    public SocioeconomicVariable getVariable() {
        return variable;
    }

    public void setCountry(CountryCode country) {
        this.country = country;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setVariable(SocioeconomicVariable variable) {
        this.variable = variable;
    }
}