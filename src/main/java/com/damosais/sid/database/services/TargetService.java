package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Owner;
import com.damosais.sid.database.beans.Target;
import com.damosais.sid.database.dao.TargetDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any target in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class TargetService {
    @Autowired
    private TargetDAO targetDao;

    /**
     * Deletes a target from the database
     *
     * @param target
     *            The target to be deleted
     */
    public void delete(Target target) {
        targetDao.delete(target);
    }

    /**
     * Returns the existing targets in the database
     *
     * @return The existing targets in the database
     */
    public List<Target> list() {
        final List<Target> targets = new ArrayList<>();
        targetDao.findAll().forEach(targets::add);
        return targets;
    }

    /**
     * Returns the targets of a owner
     *
     * @param owner
     *            The owner for which we are searching targets
     * @return A list with the targets of the owner
     */
    public List<Target> listByOwner(Owner owner) {
        return targetDao.findByOwner(owner);
    }

    /**
     * Saves an existing target to the database
     *
     * @param target
     *            The existing target
     */
    public void save(Target target) {
        targetDao.save(target);
    }
}