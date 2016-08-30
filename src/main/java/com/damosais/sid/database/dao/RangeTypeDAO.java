package com.damosais.sid.database.dao;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.damosais.sid.database.beans.RangeType;

/**
 * This interface expresses the database operations that can be performed on an range type
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Transactional
public interface RangeTypeDAO extends CrudRepository<RangeType, Long> {

}