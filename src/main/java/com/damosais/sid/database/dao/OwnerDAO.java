package com.damosais.sid.database.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.damosais.sid.database.beans.Owner;

/**
 * This is the interface that defines the actions that can be performed in the database with Owner data
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Transactional
public interface OwnerDAO extends CrudRepository<Owner, Long> {
    
}