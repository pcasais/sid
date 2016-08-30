package com.damosais.sid.database.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.damosais.sid.database.beans.Owner;
import com.damosais.sid.database.beans.Target;

/**
 * This interface defines the actions that can be performed on the database with a Target
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Transactional
public interface TargetDAO extends CrudRepository<Target, Long> {
    
    /**
     * Returns the targets of an specific owner
     *
     * @param owner
     *            The owner being searched
     * @return a list with the targets of that owner
     */
    public List<Target> findByOwner(Owner owner);
}
