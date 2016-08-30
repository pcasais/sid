package com.damosais.sid.database.beans;

/**
 * This class represents the severity levels on a vulnerability
 * 
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum Severity {
    HIGH(1, "High"), MEDIUM(2, "Medium"), LOW(3, "Low");
    
    private final int code;
    private final String description;
    
    private Severity(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * Returns the Severity given its numeric representation
     *
     * @param code
     *            the numeric representation
     * @return the Severity that has this numeric representation or null if none
     */
    public static Severity getByCode(int code) {
        Severity match = null;
        for (final Severity severity : Severity.values()) {
            if (severity.code == code) {
                match = severity;
                break;
            }
        }
        return match;
    }
    
    /**
     * Returns the Severity given its description
     *
     * @param description
     *            the description of the severity
     * @return the Severity that has this description or null if none
     */
    public static Severity getByDescription(String description) {
        Severity match = null;
        for (final Severity severity : Severity.values()) {
            if (severity.description.equalsIgnoreCase(description)) {
                match = severity;
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
    
    @Override
    public String toString() {
        return description;
    }
}