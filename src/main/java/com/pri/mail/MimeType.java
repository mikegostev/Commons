package com.pri.mail;

import java.io.Externalizable;

/**
 * A Multipurpose Internet Mail Extension (MIME) type, as defined in RFC 2045 and 2046.
 */
public class MimeType extends MimeString implements Externalizable {

    private String primaryType;
    private String subType;


    /**
     * Default constructor.
     */
    public MimeType() {
        super();
        primaryType = "application";
        subType = "*";
    }

    /**
     * Constructor that builds a MimeType from a String.
     *
     * @param rawdata the MIME type string
     */
    public MimeType(String rawdata) throws MimeTypeParseException {
        parse(rawdata);
    }

    /**
     * Constructor that builds a MimeType with the given primary and sub type but has an empty parameter list.
     *
     * @param primary the primary MIME type
     * @param sub the MIME sub-type
     * @throws MimeTypeParseException if the primary type or subtype is not a valid token
     */
    public MimeType(String primary, String sub) throws MimeTypeParseException {
        super();
        //    check to see if primary is valid
        if (isValidToken(primary)) {
            primaryType = primary.toLowerCase();
        } else {
            throw new MimeTypeParseException("Primary type is invalid.");
        }

        //    check to see if sub is valid
        if (isValidToken(sub)) {
            subType = sub.toLowerCase();
        } else {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
    }

    /**
     * A routine for parsing the MIME type out of a String.
     */
    protected void parse(String rawdata) throws MimeTypeParseException {
        super.parse(rawdata);

        int slashIndex = primaryValue.indexOf('/');

        if ((slashIndex < 0)) {
            throw new MimeTypeParseException("Unable to find a sub type.");
        } else {
            primaryType = primaryValue.substring(0, slashIndex).trim().toLowerCase();
            subType = primaryValue.substring(slashIndex + 1).trim().toLowerCase();
        }

        //    check to see if sub is valid
        if (!isValidToken(subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
    }

    /**
     * Retrieve the primary type of this object.
     *
     * @return the primary MIME type
     */
    public String getPrimaryType() {
        return primaryType;
    }

    /**
     * Set the primary type for this object to the given String.
     *
     * @param primary the primary MIME type
     * @throws MimeTypeParseException if the primary type is not a valid token
     */
    public void setPrimaryType(String primary) throws MimeTypeParseException {
        //    check to see if primary is valid
        if (!isValidToken(primary)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        primaryType = primary.toLowerCase();
    }

    /**
     * Retrieve the subtype of this object.
     *
     * @return the MIME subtype
     */
    public String getSubType() {
        return subType;
    }

    /**
     * Set the subtype for this object to the given String.
     *
     * @param sub the MIME subtype
     * @throws MimeTypeParseException if the subtype is not a valid token
     */
    public void setSubType(String sub) throws MimeTypeParseException {
        //    check to see if sub is valid
        if (!isValidToken(sub)) {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
        subType = sub.toLowerCase();
    }

    /**
     * Return the String representation of this object.
     */
    public String toString() {
        return getBaseType() + getParameters().toString();
    }

    /**
     * Return a String representation of this object without the parameter list.
     *
     * @return the MIME type and sub-type
     */
    public String getBaseType() {
        return primaryType + "/" + subType;
    }

    /**
     * Determine if the primary and sub type of this object is the same as what is in the given type.
     *
     * @param type the MimeType object to compare with
     * @return true if they match
     */
    public boolean match(MimeType type) {
        return primaryType.equals(type.getPrimaryType()) && (subType.equals("*") || type.getSubType().equals("*")
                || (subType.equals(type.getSubType())));
    }

    /**
     * Determine if the primary and sub type of this object is the same as the content type described in rawdata.
     *
     * @param rawdata the MIME type string to compare with
     * @return true if they match
     */
    public boolean match(String rawdata) throws MimeTypeParseException {
        return match(new MimeType(rawdata));
    }

    /**
     * Determine whether or not a given character belongs to a legal token.
     */
    protected static boolean isTokenChar(char c) {
        return MimeString.isTokenChar(c) && c != '/';
    }

}