package com.damosais.sid.database.beans;

/**
 * This class represents the types of motivations in an incident
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum Motivation {
	THRILL      (1, "Thrill", "The attacker is moved by thrill, an increase of status or by challenge"),
	POLITICAL	(2, "Political", "The attacker expects to get a political gain"),
	FINANCIAL   (3, "Financial", "The attacker expects to get a financial gain"),
	DAMAGE		(4, "Damage", "The attacker just wants to cause damage to the victim"),	
	UNKNOWN		(5, "Unknown", "The motivation is nknown");
		
	private final int code;
	private final String name;
	private final String description;
	
	Motivation(int code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}
	
	public int getCode(){
		return code;
	}
	
	public String getName(){
	    return name;
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
		for (Motivation type: Motivation.values()){
			if(type.getCode() == code){
				match = type;
				break;
			}
		}
		return match;
	}
	
	/**
	 * Returns the Motivation by its name
	 * @param name the name to match
	 * @return The Motivation that has the name or null if none
	 */
	public static Motivation getByName(String name){
	    Motivation match = null;
	    for (Motivation type: Motivation.values()){
	        if(type.getName().equalsIgnoreCase(name)){
	            match = type;
	            break;
	        }
	    }
	    return match;
	}
}
