package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.CVEDefinition;
import com.damosais.sid.database.dao.CVEDefinitionDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any CVE Definition in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class CVEDefinitionService {
    @Autowired
    private CVEDefinitionDAO cveDefinitionDao;
    
    /**
     * Deletes a CVE definition from the database
     *
     * @param definition
     *            The CVE definition to be deleted
     */
    public void delete(CVEDefinition definition) {
        cveDefinitionDao.delete(definition);
    }
    
    /**
     * Returns the existing CVE definitions in the database
     *
     * @return The existing CVE definitions in the database
     */
    public List<CVEDefinition> list() {
        final List<CVEDefinition> definitions = new ArrayList<>();
        cveDefinitionDao.findAll().forEach(definitions::add);
        return definitions;
    }
    
    /**
     * Saves an existing CVE definition to the database
     *
     * @param definition
     *            The existing CVE definition
     */
    public void save(CVEDefinition definition) {
        cveDefinitionDao.save(definition);
    }
}