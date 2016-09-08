package com.damosais.sid.database.beans;

/**
 * This class represents the type of impact that can be caused by the exploitation of a vulnerability
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum Impact {
    COMPLETE(3, "Complete", "C"), PARTIAL(2, "Partial", "P"), NONE(1, "None", "N");
    
    private final int code;
    private final String description;
    private final String xmlCode;
    
    Impact(int code, String description, String xmlCode) {
        this.code = code;
        this.description = description;
        this.xmlCode = xmlCode;
    }
    
    /**
     * Returns the Impact given its numeric representation
     *
     * @param code
     *            the numeric representation
     * @return the Impact that has this numeric representation or null if none
     */
    public static Impact getByCode(int code) {
        Impact match = null;
        for (final Impact impact : Impact.values()) {
            if (impact.code == code) {
                match = impact;
                break;
            }
        }
        return match;
    }
    
    /**
     * Returns the Impact given its XML representation
     *
     * @param xmlCode
     *            the XML representation
     * @return the Impact that has this XML representation or null if none
     */
    public static Impact getByXmlCode(String xmlCode) {
        Impact match = null;
        for (final Impact impact : Impact.values()) {
            if (impact.xmlCode.equalsIgnoreCase(xmlCode)) {
                match = impact;
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