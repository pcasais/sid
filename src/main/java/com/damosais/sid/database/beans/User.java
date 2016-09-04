package com.damosais.sid.database.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rolesId")
    private UserRole role;

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
        if (role != other.role) {
            return false;
        }
        if (salt == null) {
            if (other.salt != null) {
                return false;
            }
        } else if (!salt.equals(other.salt)) {
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

    public UserRole getRole() {
        return role;
    }

    public String getSalt() {
        return salt;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (password == null ? 0 : password.hashCode());
        result = prime * result + (role == null ? 0 : role.hashCode());
        result = prime * result + (salt == null ? 0 : salt.hashCode());
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

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }
}