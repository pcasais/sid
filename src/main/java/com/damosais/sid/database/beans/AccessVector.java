package com.damosais.sid.database.beans;

/**
 * The type of access vector needed to exploit a Vulnerability
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum AccessVector {
    LOCAL(1, "Local", "L"), ADJANCENT_NETWORK(2, "Adjancent network", "A"), NETWORK(3, "Network", "N");
    
    private final int code;
    private final String description;
    private final String xmlCode;
    
    AccessVector(int code, String description, String xmlCode) {
        this.code = code;
        this.description = description;
        this.xmlCode = xmlCode;
    }
    
    /**
     * Returns the AccessVector given its numeric representation
     *
     * @param code
     *            the numeric representation
     * @return the AccessVector that has this numeric representation or null if none
     */
    public AccessVector getByCode(int code) {
        AccessVector match = null;
        for (final AccessVector vector : AccessVector.values()) {
            if (vector.code == code) {
                match = vector;
                break;
            }
        }
        return match;
    }
    
    /**
     * Returns the AccessVector given its XML representation
     *
     * @param xmlCode
     *            the XML representation
     * @return the AccessVector that has this XML representation or null if none
     */
    public AccessVector getByXmlCode(String xmlCode) {
        AccessVector match = null;
        for (final AccessVector vector : AccessVector.values()) {
            if (vector.xmlCode.equalsIgnoreCase(xmlCode)) {
                match = vector;
                break;
            }
        }
        return match;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getXmlCode() {
        return xmlCode;
    }

    @Override
    public String toString() {
        return description;
    }
}