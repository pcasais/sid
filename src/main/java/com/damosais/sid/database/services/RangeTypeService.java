package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.RangeType;
import com.damosais.sid.database.dao.RangeTypeDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any range type in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class RangeTypeService {
    @Autowired
    private RangeTypeDAO rangeTypeDao;

    /**
     * Deletes a range type from the database
     *
     * @param rangeType
     *            The range type to be deleted
     */
    public void delete(RangeType rangeType) {
        rangeTypeDao.delete(rangeType);
    }

    /**
     * Returns the existing range types in the database
     *
     * @return The existing range types in the database
     */
    public List<RangeType> list() {
        final List<RangeType> rangeTypes = new ArrayList<>();
        rangeTypeDao.findAll().forEach(rangeTypes::add);
        return rangeTypes;
    }

    /**
     * Saves an existing range type to the database
     *
     * @param rangeType
     *            The existing range type
     */
    public void save(RangeType rangeType) {
        rangeTypeDao.save(rangeType);
    }
}