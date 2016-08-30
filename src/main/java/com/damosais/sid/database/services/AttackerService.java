package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Attacker;
import com.damosais.sid.database.dao.AttackerDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any attacker in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class AttackerService {
    @Autowired
    private AttackerDAO attackerDao;
    
    /**
     * Deletes an attacker from the database
     *
     * @param attacker
     *            The attacker to be deleted
     */
    public void delete(Attacker attacker) {
        attackerDao.delete(attacker);
    }
    
    /**
     * Returns the existing attackers in the database
     *
     * @return The existing attackers in the database
     */
    public List<Attacker> list() {
        final List<Attacker> attackers = new ArrayList<>();
        attackerDao.findAll().forEach(attackers::add);
        return attackers;
    }
    
    /**
     * Saves an existing attacker to the database
     *
     * @param attacker
     *            The existing attacker
     */
    public void save(Attacker attacker) {
        attackerDao.save(attacker);
    }
}