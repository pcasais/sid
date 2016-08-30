package com.damosais.sid.database.beans;

/**
 * Returns the confidentiality level of the information
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum ConfidencialityLevel {
	TOP_SECRET		(1, "Top Secret"),
	SECRET			(2, "Secret"),
	CONFIDENTIAL	(3, "Confidential"),
	RESTRICTED		(4, "Restricted"),
	UNCLASSIFIED	(5, "Unclassified");
	
	private final int code;
	private final String description;
	
	ConfidencialityLevel(int code, String description) {
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
     * Returns the ConfidencialityLevel by its numeric code
     * @param code the numeric code
     * @return The ConfidencialityLevel that has that numeric code or null if none
     */
	public static ConfidencialityLevel getByCode(int code){
		ConfidencialityLevel match = null;
		for(ConfidencialityLevel type: ConfidencialityLevel.values()){
			if(type.code == code){
				match = type;
				break;
			}
		}
		return match;
	}
}