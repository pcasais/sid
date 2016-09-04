package com.damosais.sid.database.beans;

/**
 * This enum represents the possible roles in the system
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum UserRole {
    USER_ADMIN("User admin", "User administrator role can create and modify other user's account"),
    EDIT_DATA("Edit data", "Edit data role allows the alteration of the data in the system"),
    READ_ONLY("Read only", "Read only role only allows viewing data in the system");
    
    private final String name;
    private final String description;
    
    private UserRole(String name, String description){
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString(){
        return name;
    }
}