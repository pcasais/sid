package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.CVEDefinition;
import com.damosais.sid.database.beans.User;
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
    private static final Logger LOGGER = Logger.getLogger(CVEDefinitionService.class);

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
    
    /**
     * Updates existing CVE definitions and inserts new found ones
     *
     * @param parsedDefinitions
     *            A list with the most up to date data
     * @param user
     *            The user which is doing the update
     * @return A list with the errors found during the processing
     */
    public List<String> update(List<CVEDefinition> parsedDefinitions, User user) {
        // 1st) We map the existing CVEs
        final Map<String, CVEDefinition> cvesByName = new HashMap<>();
        cveDefinitionDao.findAll().forEach(definition -> cvesByName.put(definition.getName(), definition));
        // 2nd) We create a list for the errors we may found
        final List<String> errors = new ArrayList<>();
        // 3rd) We now go through each parsed item finding if it is an update or an insert
        for (final CVEDefinition definition : parsedDefinitions) {
            final CVEDefinition existingOne = cvesByName.get(definition.getName());
            if (existingOne != null) {
                definition.setId(existingOne.getId());
                definition.setCreated(existingOne.getCreated());
                definition.setCreatedBy(existingOne.getCreatedBy());
                definition.setUpdatedBy(user);
                definition.getLossType().setId(existingOne.getLossType().getId());
                definition.getRangeType().setId(existingOne.getRangeType().getId());
            } else {
                definition.setCreatedBy(user);
            }
            try {
                cveDefinitionDao.save(definition);
            } catch (final Exception e) {
                LOGGER.error("Problem inserting parsed CVE definition '" + definition.getName() + "'", e);
                Throwable cause = e;
                while (cause.getCause() != null) {
                    cause = cause.getCause();
                }
                errors.add(definition.getName() + ": " + cause.getLocalizedMessage());
            }
        }
        return errors;
    }
}