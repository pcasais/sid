package com.damosais.sid.database.dao;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.damosais.sid.database.beans.FileMappings;
import com.damosais.sid.database.beans.User;

/**
 * This interface expresses the database operations that can be performed on the file mappings
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Transactional
public interface FileMappingsDAO extends CrudRepository<FileMappings, Long> {
    
    /**
     * This method retrieves the file mappings for a specific user, file name and sheet name
     *
     * @param owner
     *            The user who owns the mapping
     * @param fileName
     *            The name of the file
     * @param sheetName
     *            The name of the sheet in the file
     * @return The FileMappings that match or null
     */
    public FileMappings findByOwnerAndFileNameAndSheetName(User owner, String fileName, String sheetName);
}