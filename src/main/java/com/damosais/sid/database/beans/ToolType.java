package com.damosais.sid.database.beans;

/**
 * This class represents the types of tools that can be used in attacks
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum ToolType {
	PHYSICAL_ATTACK		(1, "Physical Attack", "An attack that involves dealing damage to a target."),
	EXPLOIT				(2, "Exploit", "A piece of software, a chunk of data or a sequence of command that takes advantage of a bug."),
	MALWARE				(3, "Malware", "A malicious program that performs unwanted actions on a target."),
	DISTRIBUTED_TOOL	(4, "Distributed tool", "A program designed to be executed by multiple hosts to act on a target."),
	DATA_TAP			(5, "Data tap", "A device that provides access to data as it flows through a network or system.");
	
	private final int code;
	private final String name;
	private final String description;

	private ToolType(int code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	public int getCode() {
		return code;
	}
	
	public String getName(){
	    return name;
	}

	public String getDescription() {
		return description;
	}
	
	/**
     * Returns the ToolType given its numeric representation
     *
     * @param code
     *            the numeric representation
     * @return the ToolType that has this numeric representation or null if none
     */
	public static ToolType getByCode(int code){
		ToolType match = null;
		for(ToolType type: ToolType.values()){
			if(type.code == code){
				match = type;
				break;
			}
		}
		return match;
	}
	
	/**
     * Returns the ToolType given its character representation
     *
     * @param name
     *            the name of the type
     * @return the ToolType that has this name or null if none
     */
    public static ToolType getByName(String name){
        ToolType match = null;
        for(ToolType type: ToolType.values()){
            if(type.getName().equalsIgnoreCase(name)){
                match = type;
                break;
            }
        }
        return match;
    }
	
	@Override
	public String toString(){
	    return name;
	}
}