package com.damosais.sid.database.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.damosais.sid.database.beans.User;

/**
 * This is the interface that defines the methods that can be performed with Users in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Transactional
public interface UserDAO extends CrudRepository<User, Long> {

    /**
     * Given a name returns the matching user
     * 
     * @param name
     *            The user's name
     * @return The matching user or null if none
     */
    public User findByName(String name);

}