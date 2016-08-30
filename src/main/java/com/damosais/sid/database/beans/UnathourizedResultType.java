package com.damosais.sid.database.beans;

/**
 * This class represents the types of unauthorised results that are produced during an attack
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum UnathourizedResultType {
	INCREASED_ACCESS			(1, "Increased access"),
	DISCLOSURE_OF_INFORMATION	(2, "Disclosure of information"),
	CORRUPTION_OF_INFORMATION	(3, "Corruption of information"),
	DENIAL_OF_SERVICE			(4, "Denial of service"),
	THEFT_OF_RESOURCES			(5, "Theft of resources");
	
	private final int code;
	private final String description;
	
	UnathourizedResultType(int code, String description) {
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
     * Returns the UnathourizedResultType given its numeric representation
     *
     * @param code
     *            the numeric representation
     * @return the UnathourizedResultType that has this numeric representation or null if none
     */
	public static UnathourizedResultType getByCode(int code){
		UnathourizedResultType match = null;
		for(UnathourizedResultType type: UnathourizedResultType.values()){
			if(type.code == code){
				match = type;
				break;
			}
		}
		return match;
	}
}