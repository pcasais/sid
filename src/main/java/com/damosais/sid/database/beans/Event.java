package com.damosais.sid.database.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents an event in the Common Language which is defined as an action performed on a target
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Events")
public class Event {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date")
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private Action action;

    @ManyToOne
    @JoinColumn(name = "targetId")
    private Target target;

    @ManyToOne
    @JoinColumn(name = "attackId")
    private Attack attack;

    public Action getAction() {
        return action;
    }

    public Attack getAttack() {
        return attack;
    }

    public Date getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public Target getTarget() {
        return target;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(action.getRepresentation());
        sb.append(" on ").append(target.toString());
        sb.append(" on the ").append(new SimpleDateFormat("yyyy-MM-dd").format(date));
        return sb.toString();
    }
}