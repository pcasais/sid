package com.damosais.sid.database.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.FileMappings;
import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.dao.FileMappingsDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any tool in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class FileMappigsService {
    @Autowired
    private FileMappingsDAO fileMappingsDAO;

    /**
     * Deletes a file mapping from the database
     *
     * @param fileMapping
     *            The file mapping to be deleted
     */
    public void delete(FileMappings fileMappings) {
        fileMappingsDAO.delete(fileMappings);
    }

    /**
     * Returns the existing mappings for the user, the file name and sheet name
     *
     * @param owner
     *            The user who owns the mapping
     * @param fileName
     *            The name of the file being uploaded
     * @param sheetName
     *            The name of the sheet
     * @return The existing file mappings for the user, file name and sheet name or null if none
     */
    public FileMappings get(User owner, String fileName, String sheetName) {
        return fileMappingsDAO.findByOwnerAndFileNameAndSheetName(owner, fileName, sheetName);
    }

    /**
     * Saves an existing file mapping to the database
     *
     * @param file
     *            mapping The existing file mapping
     */
    public void save(FileMappings fileMappings) {
        fileMappingsDAO.save(fileMappings);
    }
}