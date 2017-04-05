package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Conflict;
import com.damosais.sid.database.dao.ConflictDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any conflict in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class ConflictService {
    @Autowired
    private ConflictDAO conflictDao;

    /**
     * Deletes a conflict from the database
     *
     * @param conflict
     *            The conflict to be deleted
     */
    public void delete(Conflict conflict) {
        conflictDao.delete(conflict);
    }

    /**
     * Returns the existing conflicts in the database
     *
     * @return The existing conflicts in the database
     */
    public List<Conflict> list() {
        final List<Conflict> conflicts = new ArrayList<>();
        conflictDao.findAll().forEach(conflicts::add);
        return conflicts;
    }

    /**
     * Saves an existing conflict to the database
     *
     * @param conflict
     *            The existing conflict
     */
    public void save(Conflict conflict) {
        conflictDao.save(conflict);
    }
}