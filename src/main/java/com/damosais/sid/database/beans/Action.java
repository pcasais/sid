package com.damosais.sid.database.beans;

/**
 * The types of action that can be performed on an Event
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum Action {
    //@formatter:off
	PROBE("Probe", "Accces a target in order to determine its characteristics"),
	SCAN("Scan", "Access a set of targets in order to identify which targets have a specific characteristic"),
	FLOOD("Flood", "Access a target repeatedly in order to overload the target's capacity"),
	AUTHENTICATE("Authenticate", "Present an identity of someone to a process and, if required, verify that identity, in order to access a target"),
	BYPASS("Bypass", "Avoid a process by using an alternative method to access a target"),
	SPOOF("Spoof", "Masquerade by assuming the appearence of a different entity in network communications"),
	READ("Read", "Obtain the content of data in a storage device or other data medium"),
	COPY("Copy", "Reproduce a target leaving the original target unchanged"),
	STEAL("Steal", "Take possesion of a target without leaving a copy in the original location"),
	MODIFY("Modify", "Change the content or characteristics of a target"),
	DELETE("Delete", "Remove a target or render it irretrievable");
    //@formatter:on
    
    private final String representation;
    private final String description;

    /**
     * The constructor requires the representation and its description
     * @param representation The representation of the action
     * @param description The description of the action
     */
    private Action(String representation, String description) {
        this.representation = representation;
        this.description = description;
    }
    
    /**
     * Returns the action that has the given representation
     *
     * @param representation
     *            The representation for which we are trying to find the action
     * @return The matching action or null if none matches
     */
    public static Action getActionByRepresentation(String representation) {
        Action matching = null;
        for (final Action action : Action.values()) {
            if (action.representation.equalsIgnoreCase(representation)) {
                matching = action;
                break;
            }
        }
        return matching;
    }

    public String getDescription() {
        return description;
    }

    public String getRepresentation() {
        return representation;
    }

    @Override
    public String toString() {
        return representation;
    }
}