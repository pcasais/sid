package com.damosais.sid.database.beans;

/**
 * This enum represents the three type of variables than we can have in the system
 * 
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum VariableType {
    ECONOMIC("Economic"), POLITICAL("Political"), SOCIAL("Social");
    
    private final String name;
    
    private VariableType(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}