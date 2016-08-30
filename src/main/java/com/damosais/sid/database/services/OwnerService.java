package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Owner;
import com.damosais.sid.database.dao.OwnerDAO;

/**
 * This class is responsible of creating, listing, deleting and updating onwers and its related data
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class OwnerService {
    @Autowired
    private OwnerDAO ownerDao;
    
    /**
     * Deletes an owner from the database
     *
     * @param owner
     *            The owner being deleted
     */
    public void delete(Owner owner) {
        ownerDao.delete(owner);
    }

    /**
     * Returns the existing events in the database
     *
     * @return The existing events in the database
     */
    public List<Owner> list() {
        final List<Owner> owners = new ArrayList<>();
        ownerDao.findAll().forEach(owners::add);
        return owners;
    }

    /**
     * Saves an existing owner to the database
     *
     * @param owner
     *            The existing owner
     */
    public void save(Owner owner) {
        ownerDao.save(owner);
    }
}