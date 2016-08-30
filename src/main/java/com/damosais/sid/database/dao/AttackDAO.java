package com.damosais.sid.database.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.Incident;

/**
 * This interface expresses the database operations that can be performed on an Attack
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Transactional
public interface AttackDAO extends CrudRepository<Attack, Long> {
    /**
     * Returns the attacks of an specific incident
     *
     * @param incident
     *            The incident being searched
     * @return a list with the attacks of that attack
     */
    public List<Attack> findByIncident(Incident incident);
}