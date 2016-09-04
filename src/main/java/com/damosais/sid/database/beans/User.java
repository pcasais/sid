package com.damosais.sid.database.beans;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * This class represents the entity User which is used to store the credentials of an user in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Users", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "salt", nullable = false)
    private String salt;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "UserRoles", joinColumns = @JoinColumn(name = "userId"))
    @Column(name = "rolesId")
    private Set<UserRole> roles;
    
    @Column(name = "failedLogins", nullable = false)
    private Integer failedLogins = 0;
    
    @Column(name = "suspended", nullable = false)
    private Boolean suspended = false;

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
        final User other = (User) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (roles == null) {
            if (other.roles != null) {
                return false;
            }
        } else if (!roles.equals(other.roles)) {
            return false;
        }
        if (salt == null) {
            if (other.salt != null) {
                return false;
            }
        } else if (!salt.equals(other.salt)) {
            return false;
        }
        if (suspended == null) {
            if (other.suspended != null) {
                return false;
            }
        } else if (!suspended.equals(other.suspended)) {
            return false;
        }
        return true;
    }

    public Integer getFailedLogins() {
        return failedLogins;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getPassword() {
        return password;
    }
    
    public Set<UserRole> getRoles() {
        return roles;
    }
    
    public String getSalt() {
        return salt;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (password == null ? 0 : password.hashCode());
        result = prime * result + (roles == null ? 0 : roles.hashCode());
        result = prime * result + (salt == null ? 0 : salt.hashCode());
        result = prime * result + (suspended == null ? 0 : suspended.hashCode());
        return result;
    }
    
    public Boolean isSuspended() {
        return suspended;
    }
    
    public void setFailedLogins(Integer failedLogins) {
        this.failedLogins = failedLogins;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }
    
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }
}