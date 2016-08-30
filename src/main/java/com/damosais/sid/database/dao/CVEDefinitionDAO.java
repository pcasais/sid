package com.damosais.sid.database.dao;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.damosais.sid.database.beans.CVEDefinition;

/**
 * This interface expresses the database operations that can be performed on a CVE definition
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Transactional
public interface CVEDefinitionDAO extends CrudRepository<CVEDefinition, Long> {

}