package com.damosais.sid.database.beans;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Represents a Common Vulnerability Exposition Definition
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "CVEs", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class CVEDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "published", nullable = false)
    private Date published;

    @Column(name = "modified")
    private Date modified;

    @Column(name = "cveDesc", nullable = false)
    private String cveDesc;

    @Column(name = "nvdDesc")
    private String nvdDesc;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity;

    @Column(name = "cvssBaseScore", nullable = false)
    private Float cvssBaseScore;

    @Column(name = "cvssExploitSubscore", nullable = false)
    private Float cvssExploitSubscore;

    @Column(name = "cvssImpactSubscore", nullable = false)
    private Float cvssImpactSubscore;

    @Enumerated(EnumType.STRING)
    @Column(name = "accessVector", nullable = false)
    private AccessVector accessVector;

    @Enumerated(EnumType.STRING)
    @Column(name = "accessComplexity", nullable = false)
    private AccessComplexity accessComplexity;

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication", nullable = false)
    private Authentication authentication;

    @Enumerated(EnumType.STRING)
    @Column(name = "confImpact", nullable = false)
    private Impact confImpact;

    @Enumerated(EnumType.STRING)
    @Column(name = "integImpact", nullable = false)
    private Impact integImpact;

    @Enumerated(EnumType.STRING)
    @Column(name = "availImpact", nullable = false)
    private Impact availImpact;

    @OneToOne(mappedBy = "definition", cascade = CascadeType.ALL)
    private LossType lossType;

    @OneToOne(mappedBy = "definition", cascade = CascadeType.ALL)
    private RangeType rangeType;

    @OneToOne(mappedBy = "definition", cascade = CascadeType.ALL)
    private Vulnerability vulnerability;

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
        final CVEDefinition other = (CVEDefinition) obj;
        if (accessComplexity != other.accessComplexity) {
            return false;
        }
        if (accessVector != other.accessVector) {
            return false;
        }
        if (authentication != other.authentication) {
            return false;
        }
        if (availImpact != other.availImpact) {
            return false;
        }
        if (confImpact != other.confImpact) {
            return false;
        }
        if (cveDesc == null) {
            if (other.cveDesc != null) {
                return false;
            }
        } else if (!cveDesc.equals(other.cveDesc)) {
            return false;
        }
        if (cvssBaseScore == null) {
            if (other.cvssBaseScore != null) {
                return false;
            }
        } else if (!cvssBaseScore.equals(other.cvssBaseScore)) {
            return false;
        }
        if (cvssExploitSubscore == null) {
            if (other.cvssExploitSubscore != null) {
                return false;
            }
        } else if (!cvssExploitSubscore.equals(other.cvssExploitSubscore)) {
            return false;
        }
        if (cvssImpactSubscore == null) {
            if (other.cvssImpactSubscore != null) {
                return false;
            }
        } else if (!cvssImpactSubscore.equals(other.cvssImpactSubscore)) {
            return false;
        }
        if (integImpact != other.integImpact) {
            return false;
        }
        if (lossType == null) {
            if (other.lossType != null) {
                return false;
            }
        } else if (!lossType.equals(other.lossType)) {
            return false;
        }
        if (modified == null) {
            if (other.modified != null) {
                return false;
            }
        } else if (!modified.equals(other.modified)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (nvdDesc == null) {
            if (other.nvdDesc != null) {
                return false;
            }
        } else if (!nvdDesc.equals(other.nvdDesc)) {
            return false;
        }
        if (published == null) {
            if (other.published != null) {
                return false;
            }
        } else if (!published.equals(other.published)) {
            return false;
        }
        if (rangeType == null) {
            if (other.rangeType != null) {
                return false;
            }
        } else if (!rangeType.equals(other.rangeType)) {
            return false;
        }
        if (severity != other.severity) {
            return false;
        }
        return true;
    }
    
    public AccessComplexity getAccessComplexity() {
        return accessComplexity;
    }

    public AccessVector getAccessVector() {
        return accessVector;
    }
    
    public Authentication getAuthentication() {
        return authentication;
    }
    
    public Impact getAvailImpact() {
        return availImpact;
    }
    
    public Impact getConfImpact() {
        return confImpact;
    }
    
    public Date getCreated() {
        return created;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public String getCveDesc() {
        return cveDesc;
    }
    
    public Float getCvssBaseScore() {
        return cvssBaseScore;
    }
    
    public Float getCvssExploitSubscore() {
        return cvssExploitSubscore;
    }

    public Float getCvssImpactSubscore() {
        return cvssImpactSubscore;
    }
    
    public Long getId() {
        return id;
    }

    public Impact getIntegImpact() {
        return integImpact;
    }

    public LossType getLossType() {
        return lossType;
    }

    public Date getModified() {
        return modified;
    }

    public String getName() {
        return name;
    }

    public String getNvdDesc() {
        return nvdDesc;
    }

    public Date getPublished() {
        return published;
    }

    public RangeType getRangeType() {
        return rangeType;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Date getUpdated() {
        return updated;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public Vulnerability getVulnerability() {
        return vulnerability;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (accessComplexity == null ? 0 : accessComplexity.hashCode());
        result = prime * result + (accessVector == null ? 0 : accessVector.hashCode());
        result = prime * result + (authentication == null ? 0 : authentication.hashCode());
        result = prime * result + (availImpact == null ? 0 : availImpact.hashCode());
        result = prime * result + (confImpact == null ? 0 : confImpact.hashCode());
        result = prime * result + (cveDesc == null ? 0 : cveDesc.hashCode());
        result = prime * result + (cvssBaseScore == null ? 0 : cvssBaseScore.hashCode());
        result = prime * result + (cvssExploitSubscore == null ? 0 : cvssExploitSubscore.hashCode());
        result = prime * result + (cvssImpactSubscore == null ? 0 : cvssImpactSubscore.hashCode());
        result = prime * result + (integImpact == null ? 0 : integImpact.hashCode());
        result = prime * result + (lossType == null ? 0 : lossType.hashCode());
        result = prime * result + (modified == null ? 0 : modified.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (nvdDesc == null ? 0 : nvdDesc.hashCode());
        result = prime * result + (published == null ? 0 : published.hashCode());
        result = prime * result + (rangeType == null ? 0 : rangeType.hashCode());
        result = prime * result + (severity == null ? 0 : severity.hashCode());
        return result;
    }

    public void setAccessComplexity(AccessComplexity accessComplexity) {
        this.accessComplexity = accessComplexity;
    }

    public void setAccessVector(AccessVector accessVector) {
        this.accessVector = accessVector;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public void setAvailImpact(Impact availImpact) {
        this.availImpact = availImpact;
    }

    public void setConfImpact(Impact confImpact) {
        this.confImpact = confImpact;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setCveDesc(String cveDesc) {
        this.cveDesc = cveDesc;
    }

    public void setCvssBaseScore(Float cvssBaseScore) {
        this.cvssBaseScore = cvssBaseScore;
    }

    public void setCvssExploitSubscore(Float cvssExploitSubscore) {
        this.cvssExploitSubscore = cvssExploitSubscore;
    }

    public void setCvssImpactSubscore(Float cvssImpactSubscore) {
        this.cvssImpactSubscore = cvssImpactSubscore;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIntegImpact(Impact integImpact) {
        this.integImpact = integImpact;
    }

    public void setLossType(LossType lossType) {
        this.lossType = lossType;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNvdDesc(String nvdDesc) {
        this.nvdDesc = nvdDesc;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public void setRangeType(RangeType rangeType) {
        this.rangeType = rangeType;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setVulnerability(Vulnerability vulnerability) {
        this.vulnerability = vulnerability;
    }

    @Override
    public String toString() {
        return name;
    }
}