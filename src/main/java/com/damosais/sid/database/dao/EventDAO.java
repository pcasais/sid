package com.damosais.sid.database.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.Event;
import com.neovisionaries.i18n.CountryCode;

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

    /**
     * Returns events that are between two defined dates
     *
     * @param start
     *            The start date of the range
     * @param end
     *            The end date of the range
     * @param country
     *            The country of the target
     * @return a list with the events that match
     */
    public List<Event> findByDateBetweenAndTargetCountry(Date start, Date end, CountryCode country);
}