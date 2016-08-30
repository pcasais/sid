package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.Incident;
import com.damosais.sid.database.dao.AttackDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any attack in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class AttackService {
    @Autowired
    private AttackDAO attackDao;

    /**
     * Deletes an attack from the database
     *
     * @param attack
     *            The attack to be deleted
     */
    public void delete(Attack attack) {
        attackDao.delete(attack);
    }

    /**
     * Returns the existing attacks in the database
     *
     * @return The existing attacks in the database
     */
    public List<Attack> list() {
        final List<Attack> attacks = new ArrayList<>();
        attackDao.findAll().forEach(attacks::add);
        return attacks;
    }

    /**
     * Returns the attacks that are part of an incident
     *
     * @param incident
     *            the incident for which we are looking attacks
     * @return a list with the attacks that are part of the incident
     */
    public List<Attack> listByIncident(Incident incident) {
        return attackDao.findByIncident(incident);
    }

    /**
     * Saves an existing attack to the database
     *
     * @param attack
     *            The existing attack
     */
    public void save(Attack attack) {
        attackDao.save(attack);
    }
}