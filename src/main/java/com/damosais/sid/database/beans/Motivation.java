package com.damosais.sid.database.beans;

/**
 * This class represents the types of motivations in an incident
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum Motivation {
	THRILL      (1, "Thrill, status or challenge"),
	POLITICAL	(2, "Political gain"),
	FINANCIAL   (3, "Financial gain"),
	DAMAGE		(4, "Damage"),	
	UNKNOWN		(5, "Unknown");
		
	private final int code;
	private final String description;
	
	Motivation(int code, String description) {
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
     * Returns the Motivation by its numeric code
     * @param code the numeric code
     * @return The Motivation that has that numeric code or null if none
     */
	public static Motivation getByCode(int code){
		Motivation match = null;
		for(Motivation type: Motivation.values()){
			if(type.code == code){
				match = type;
				break;
			}
		}
		return match;
	}
}
