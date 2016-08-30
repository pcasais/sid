package com.damosais.sid.database.beans;

/**
 * Represents the type of authentication needed to exploit a vulnerability
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum Authentication {
    NONE(1, "None required", "N"), SINGLE(2, "Required single instance", "S"), MULTIPLE(3, "Required multiple instances", "M");

    private final int code;
    private final String description;
    private final String xmlCode;

    Authentication(int code, String description, String xmlCode) {
        this.code = code;
        this.description = description;
        this.xmlCode = xmlCode;
    }

    /**
     * Returns the Authentication given its numeric representation
     *
     * @param code
     *            the numeric representation
     * @return the Authentication that has this numeric representation or null if none
     */
    public Authentication getByCode(int code) {
        Authentication match = null;
        for (final Authentication authentication : Authentication.values()) {
            if (authentication.code == code) {
                match = authentication;
                break;
            }
        }
        return match;
    }

    /**
     * Returns the Authentication given its XML representation
     *
     * @param xmlCode
     *            the XML representation
     * @return the Authentication that has this XML representation or null if none
     */
    public Authentication getByXmlCode(String xmlCode) {
        Authentication match = null;
        for (final Authentication authentication : Authentication.values()) {
            if (authentication.xmlCode.equalsIgnoreCase(xmlCode)) {
                match = authentication;
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

    public String getXmlCode() {
        return xmlCode;
    }

    @Override
    public String toString() {
        return description;
    }
}