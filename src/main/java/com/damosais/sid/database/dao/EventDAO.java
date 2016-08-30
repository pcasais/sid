package com.damosais.sid.database.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.Event;

/**
 * This interface defines the actions that can be performed in the database with an Event
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Transactional
public interface EventDAO extends CrudRepository<Event, Long> {
    /**
     * Returns the events of an specific attack
     *
     * @param attack
     *            The attack being searched
     * @return a list with the events of that attack
     */
    public List<Event> findByAttack(Attack attack);
}