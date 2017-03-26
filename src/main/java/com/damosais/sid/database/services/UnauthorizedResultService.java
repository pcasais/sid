package com.damosais.sid.database.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.UnauthorizedResult;
import com.damosais.sid.database.dao.UnauthorizedResultDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any unauthorized result in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class UnauthorizedResultService {
    @Autowired
    private UnauthorizedResultDAO unauthorizedResultDAO;

    /**
     * Deletes an unauthorized result from the database
     *
     * @param unauthorizedResult
     *            The unauthorized result to be deleted
     */
    public void delete(UnauthorizedResult unauthorizedResult) {
        unauthorizedResultDAO.delete(unauthorizedResult);
    }

    /**
     * Saves an existing unauthorized result to the database
     *
     * @param unauthorizedResult
     *            The existing unauthorized result
     */
    public void save(UnauthorizedResult unauthorizedResult) {
        unauthorizedResultDAO.save(unauthorizedResult);
    }
}