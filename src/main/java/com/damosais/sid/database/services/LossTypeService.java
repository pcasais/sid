package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.LossType;
import com.damosais.sid.database.dao.LossTypeDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any loss type in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class LossTypeService {
    @Autowired
    private LossTypeDAO lossTypeDao;
    
    /**
     * Deletes a loss type from the database
     *
     * @param lossType
     *            The loss type to be deleted
     */
    public void delete(LossType lossType) {
        lossTypeDao.delete(lossType);
    }
    
    /**
     * Returns the existing loss types in the database
     *
     * @return The existing loss types in the database
     */
    public List<LossType> list() {
        final List<LossType> lossTypes = new ArrayList<>();
        lossTypeDao.findAll().forEach(lossTypes::add);
        return lossTypes;
    }
    
    /**
     * Saves an existing loss type to the database
     *
     * @param lossType
     *            The existing loss type
     */
    public void save(LossType lossType) {
        lossTypeDao.save(lossType);
    }
}