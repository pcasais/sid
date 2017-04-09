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
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "date", nullable = false)
    private Date date;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private Action action;
    
    @ManyToOne
    @JoinColumn(name = "targetId", nullable = false)
    private Target target;
    
    @ManyToOne
    @JoinColumn(name = "attackId")
    private Attack attack;
    
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

    @Transient
    private int rowNumber;

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
        final Event other = (Event) obj;
        if (action != other.action) {
            return false;
        }
        if (date == null) {
            if (other.date != null) {
                return false;
            }
        } else if (!date.equals(other.date)) {
            return false;
        }
        if (target == null) {
            if (other.target != null) {
                return false;
            }
        } else if (!target.equals(other.target)) {
            return false;
        }
        return true;
    }

    public Action getAction() {
        return action;
    }

    public Attack getAttack() {
        return attack;
    }

    public Date getCreated() {
        return created;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Date getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public Target getTarget() {
        return target;
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
        result = prime * result + (action == null ? 0 : action.hashCode());
        result = prime * result + (date == null ? 0 : date.hashCode());
        result = prime * result + (target == null ? 0 : target.hashCode());
        return result;
    }
    
    public void setAction(Action action) {
        this.action = action;
    }
    
    public void setAttack(Attack attack) {
        this.attack = attack;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
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