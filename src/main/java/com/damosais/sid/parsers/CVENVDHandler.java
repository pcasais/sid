package com.damosais.sid.parsers;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.damosais.sid.database.beans.CVEDefinition;

/**
 * This interface defines the operations done by the SAX handler when parsing a NVD CVE file
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public interface CVENVDHandler {

    /**
     * Finishes the read of an entry node
     */
    public void endEntryNode();

    /**
     * Handles the end of the loss types node
     */
    public void endLossTypesNode();

    /**
     * Handles the end of a range node
     */
    public void endRangeNode();
    
    /**
     * Returns the data that has been parsed from the file
     *
     * @return A list with the definitions parsed from the file
     */
    public List<CVEDefinition> getParsedData();

    /**
     * Processes the content of a description field
     *
     * @param data
     *            the content of the description
     * @param attrs
     *            the attributes of the node
     */
    public void handleDescription(final String data, Attributes attrs);

    /**
     * Starts the handling of an availability loss type
     */
    public void startAvailLoss();
    
    /**
     * Starts the handling of a confidentiality loss
     */
    public void startConfLoss();

    /**
     * Starts the processing of a CVE definition
     *
     * @param attrs
     *            the attributes of the entry node
     * @throws SAXException
     *             If there is a problem reading the information from the attributes
     */
    public void startEntry(Attributes attrs) throws SAXException;
    
    /**
     * Starts the handling of an integrity loss
     */
    public void startIntLoss();

    /**
     * Starts the processing of a local range
     */
    public void startLocal();
    
    /**
     * Starts the processing of a local network range
     */
    public void startLocalNetwork();

    /**
     * Starts the processing of the loss types
     */
    public void startLossTypes();
    
    /**
     * Starts the processing of a network range
     */
    public void startNetwork();

    /**
     * Starts the NVD root node
     *
     * @param attrs
     *            the attributes of the node
     */
    public void startNvd(Attributes attrs);
    
    /**
     * Starts the processing of the ranges
     */
    public void startRange();

    /**
     * Starts the handling of the security protection loss
     *
     * @param attrs
     *            the attributes of the node
     */
    public void startSecProtLoss(Attributes attrs);
    
    /**
     * Starts the processing of a user initiated range
     */
    public void startUserInit();
    
}