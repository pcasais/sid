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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Represents a Common Vulnerability Exposition Definition
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "CVEs")
public class CVEDefinition {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "published")
    private Date published;
    
    @Column(name = "modified")
    private Date modified;
    
    @Column(name = "cveDesc")
    private String cveDesc;
    
    @Column(name = "nvdDesc")
    private String nvdDesc;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private Severity severity;
    
    @Column(name = "cvssBaseScore")
    private Float cvssBaseScore;
    
    @Column(name = "cvssExploitSubscore")
    private Float cvssExploitSubscore;
    
    @Column(name = "cvssImpactSubscore")
    private Float cvssImpactSubscore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "accessVector")
    private AccessVector accessVector;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "accessComplexity")
    private AccessComplexity accessComplexity;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication")
    private Authentication authentication;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "confImpact")
    private Impact confImpact;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "integImpact")
    private Impact integImpact;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "availImpact")
    private Impact availImpact;
    
    @OneToOne(mappedBy = "definition", cascade = CascadeType.ALL)
    private LossType lossType;
    
    @OneToOne(mappedBy = "definition", cascade = CascadeType.ALL)
    private RangeType rangeType;
    
    @OneToMany(mappedBy = "definition", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Vulnerability> vulnerabilities;
    
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
    
    public Set<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
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
    
    public void setVulnerabilities(Set<Vulnerability> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }
    
    @Override
    public String toString() {
        return name;
    }
}