package com.damosais.sid.database.beans;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * This class represents the mappings that a user has done before on a file with the given name
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "FileMappings", uniqueConstraints = @UniqueConstraint(columnNames = { "owner", "fileName", "sheetName" }))
public class FileMappings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @Column(name = "fileName", nullable = false)
    private String fileName;

    @Column(name = "sheetName", nullable = false)
    private String sheetName;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> columnMappings;

    public Map<String, String> getColumnMappings() {
        return columnMappings;
    }

    public String getFileName() {
        return fileName;
    }
    
    public Long getId() {
        return id;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public void setColumnMappings(Map<String, String> columnMappings) {
        this.columnMappings = columnMappings;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
