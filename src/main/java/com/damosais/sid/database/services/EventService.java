package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Attack;
import com.damosais.sid.database.beans.Event;
import com.damosais.sid.database.dao.EventDAO;

/**
 * This class is responsible of creating, listing, deleting and updating events
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class EventService {
    @Autowired
    private EventDAO eventDao;
    
    /**
     * Deletes an event from the database
     *
     * @param event
     *            The event being deleted
     */
    public void delete(Event event) {
        eventDao.delete(event);
    }
    
    /**
     * Returns the existing events in the database
     *
     * @return The existing events in the database
     */
    public List<Event> list() {
        final List<Event> events = new ArrayList<>();
        eventDao.findAll().forEach(events::add);
        return events;
    }
    
    /**
     * Returns the events that are part of an attack
     * 
     * @param attack
     *            the attack for which we are looking events
     * @return a list with the events that are part of the attack
     */
    public List<Event> listByAttack(Attack attack) {
        return eventDao.findByAttack(attack);
    }

    /**
     * Saves an existing event to the database
     *
     * @param event
     *            The existing event
     */
    public void save(Event event) {
        eventDao.save(event);
    }
}