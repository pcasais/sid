package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Incident;
import com.damosais.sid.database.dao.IncidentDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any incident in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class IncidentService {
    @Autowired
    private IncidentDAO incidentDao;
    
    /**
     * Deletes an incident from the database
     *
     * @param incident
     *            The incident to be deleted
     */
    public void delete(Incident incident) {
        incidentDao.delete(incident);
    }
    
    /**
     * Returns the existing incidents in the database
     *
     * @return The existing incidents in the database
     */
    public List<Incident> list() {
        final List<Incident> incidents = new ArrayList<>();
        incidentDao.findAll().forEach(incidents::add);
        return incidents;
    }
    
    /**
     * Saves an existing incident to the database
     *
     * @param incident
     *            The existing incident
     */
    public void save(Incident incident) {
        incidentDao.save(incident);
    }
}