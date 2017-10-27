package com.pri.mail;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A Multipurpose Internet Mail Extension (MIME) type, as defined in RFC 2045 and 2046.
 */
public class MimeString implements Externalizable {

    protected String primaryValue;
    private MimeTypeParameterList parameters;

    /**
     * A string that holds all the special chars.
     */
    private static final String TSPECIALS = "()<>@,;:[]?=\\\"";

    /**
     * Default constructor.
     */
    public MimeString() {
        parameters = new MimeTypeParameterList();
    }

    /**
     * Constructor that builds a MimeType from a String.
     *
     * @param rawdata the MIME type string
     */
    public MimeString(String rawdata) throws MimeTypeParseException {
        parse(rawdata);
    }


    /**
     * A routine for parsing the MIME type out of a String.
     */
    protected void parse(String rawdata) throws MimeTypeParseException {
//  int slashIndex = rawdata.indexOf('/');
        int semIndex = rawdata.indexOf(';');

        if (semIndex < 0) {
            primaryValue = rawdata.trim().toLowerCase();
            parameters = new MimeTypeParameterList();
        } else {
            //    we have all three items in the proper sequence
            primaryValue = rawdata.substring(0, semIndex).trim().toLowerCase();
            parameters = new MimeTypeParameterList(rawdata.substring(semIndex));
        }

        //    now validate the primary and sub types

        //    check to see if primary is valid
        if (!isValidToken(primaryValue)) {
            throw new MimeTypeParseException("Primary value is invalid.");
        }

    }


    /**
     * Set the primary type for this object to the given String.
     *
     * @param primary the primary MIME type
     * @throws MimeTypeParseException if the primary type is not a valid token
     */
    public void setBaseType(String primary) throws MimeTypeParseException {
        //    check to see if primary is valid
        if (!isValidToken(primary)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        primaryValue = primary.toLowerCase();
    }

    /**
     * Retrieve this object's parameter list.
     *
     * @return a MimeTypeParameterList object representing the parameters
     */
    public MimeTypeParameterList getParameters() {
        return parameters;
    }

    /**
     * Retrieve the value associated with the given name, or null if there is no current association.
     *
     * @param name the parameter name
     * @return the paramter's value
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Set the value to be associated with the given name, replacing any previous association.
     *
     * @param name the parameter name
     * @param value the paramter's value
     */
    public void setParameter(String name, String value) {
        parameters.set(name, value);
    }

    /**
     * Remove any value associated with the given name.
     *
     * @param name the parameter name
     */
    public void removeParameter(String name) {
        parameters.remove(name);
    }

    /**
     * Return the String representation of this object.
     */
    public String toString() {
        return getBaseType() + parameters.toString();
    }

    /**
     * Return a String representation of this object without the parameter list.
     *
     * @return the MIME type and sub-type
     */
    public String getBaseType() {
        return primaryValue;
    }

    /**
     * Determine if the primary and sub type of this object is the same as what is in the given type.
     *
     * @param type the MimeType object to compare with
     * @return true if they match
     */
    public boolean match(MimeString type) {
        return primaryValue.equals(type.getBaseType());
    }

    /**
     * Determine if the primary and sub type of this object is the same as the content type described in rawdata.
     *
     * @param rawdata the MIME type string to compare with
     * @return true if they match
     */
    public boolean match(String rawdata) throws MimeTypeParseException {
        return match(new MimeString(rawdata));
    }

    /**
     * The object implements the writeExternal method to save its contents by calling the methods of DataOutput for its
     * primitive values or calling the writeObject method of ObjectOutput for objects, strings and arrays.
     *
     * @param out the ObjectOutput object to write to
     * @throws IOException Includes any I/O exceptions that may occur
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(toString());
        out.flush();
    }

    /**
     * The object implements the readExternal method to restore its contents by calling the methods of DataInput for
     * primitive types and readObject for objects, strings and arrays.  The readExternal method must read the values in
     * the same sequence and with the same types as were written by writeExternal.
     *
     * @param in the ObjectInput object to read from
     * @throws ClassNotFoundException If the class for an object being restored cannot be found.
     */
    public void readExternal(ObjectInput in) throws IOException//, ClassNotFoundException
    {
        try {
            parse(in.readUTF());
        } catch (MimeTypeParseException e) {
            throw new IOException(e.toString());
        }
    }

    //    below here be scary parsing related things

    /**
     * Determine whether or not a given character belongs to a legal token.
     */
    protected static boolean isTokenChar(char c) {
        return ((c > 040) && (c < 0177)) && (TSPECIALS.indexOf(c) < 0);
    }

    /**
     * Determine whether or not a given string is a legal token.
     */
    protected boolean isValidToken(String s) {
        int len = s.length();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                char c = s.charAt(i);
                if (!isTokenChar(c)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * A simple parser test,
     * for debugging...
     *
     public static void main(String[] args)
     throws MimeTypeParseException, IOException {
     for (int i = 0; i < args.length; ++i) {
     System.out.println("Original: " + args[i]);

     MimeType type = new MimeType(args[i]);

     System.out.println("Short:    " + type.getBaseType());
     System.out.println("Parsed:   " + type.toString());
     System.out.println();
     }
     }
     */
}