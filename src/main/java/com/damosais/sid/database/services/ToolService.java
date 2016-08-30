package com.damosais.sid.database.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.damosais.sid.database.beans.Tool;
import com.damosais.sid.database.dao.ToolDAO;

/**
 * This service is responsible of retrieving, creating, deleting and updating any tool in the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class ToolService {
    @Autowired
    private ToolDAO toolDao;

    /**
     * Deletes a tool from the database
     *
     * @param tool
     *            The tool to be deleted
     */
    public void delete(Tool tool) {
        toolDao.delete(tool);
    }

    /**
     * Returns the existing tools in the database
     *
     * @return The existing tools in the database
     */
    public List<Tool> list() {
        final List<Tool> tools = new ArrayList<>();
        toolDao.findAll().forEach(tools::add);
        return tools;
    }

    /**
     * Saves an existing tool to the database
     *
     * @param tool
     *            The existing tool
     */
    public void save(Tool tool) {
        toolDao.save(tool);
    }
}