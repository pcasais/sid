package com.damosais.sid.database.beans;

/**
 * This class represents the different levels of complexity on access for a Vulnerability
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum AccessComplexity {
    HIGH(1, "High", "H"), MEDIUM(2, "Medium", "M"), LOW(3, "Low", "L");
    
    private final int code;
    private final String description;
    private final String xmlCode;
    
    AccessComplexity(int code, String description, String xmlCode) {
        this.code = code;
        this.description = description;
        this.xmlCode = xmlCode;
    }
    
    /**
     * Returns the AccessComplexity given its numeric representation
     *
     * @param code
     *            The numeric representation
     * @return the AccessComplexity that has this numeric representation or null if none
     */
    public AccessComplexity getByCode(int code) {
        AccessComplexity match = null;
        for (final AccessComplexity complexity : AccessComplexity.values()) {
            if (complexity.code == code) {
                match = complexity;
                break;
            }
        }
        return match;
    }
    
    /**
     * Returns the AccessComplexity by its XML representation
     *
     * @param xmlCode
     *            The XML representation
     * @return the AccessComplexity that has this XML representation or null if none
     */
    public AccessComplexity getByXmlCode(String xmlCode) {
        AccessComplexity match = null;
        for (final AccessComplexity complexity : AccessComplexity.values()) {
            if (complexity.xmlCode.equalsIgnoreCase(xmlCode)) {
                match = complexity;
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