package com.damosais.sid.database.beans;

/**
 * This class represents the different types of attackers
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum AttackerType {
	HACKER      (1, "Hacker"),
	HACKTIVIST	(2, "Hacktivist"),
	VANDALS     (3, "Vandals"),
	CRIMINALS	(4, "Criminals"),
	STATE		(5, "State"),	
	OTHER		(6, "Other"),
	UNKNOWN     (7, "Unknown");
		
	private final int code;
	private final String description;
	
	AttackerType(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public int getCode(){
		return code;
	}
	
	public String getDescription(){
		return description;
	}
	
	/**
	 * Returns the AttackerType by its numeric code
	 * @param code the numeric code
	 * @return The AttackerType that has that numeric code or null if none
	 */
	public static AttackerType getByCode(int code){
		AttackerType match = null;
		for(AttackerType type: AttackerType.values()){
			if(type.code == code){
				match = type;
				break;
			}
		}
		return match;
	}
	
	/**
     * Returns the AttackerType by its description
     * @param description The description
     * @return The AttackerType that has that description or null if none
     */
    public static AttackerType getByDescription(String description){
        AttackerType match = null;
        for(AttackerType type: AttackerType.values()){
            if(type.getDescription().equalsIgnoreCase(description)){
                match = type;
                break;
            }
        }
        return match;
    }
	
	@Override
	public String toString(){
	    return description;
	}
}