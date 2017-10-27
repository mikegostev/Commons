/*
 * Created on 31.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.session;


import com.pri.log.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class UserProfile {

    private String username;
    private String passhash;
    // private Document profile;
    private int userID;


    public UserProfile(String username, int uid, String passhash) {
        this.username = username;
        this.passhash = passhash;
        userID = uid;

/*
  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  factory.setValidating(false);
  factory.setNamespaceAware(false);

  try
  {
   DocumentBuilder builder = factory.newDocumentBuilder();
   profile = builder.parse(new ByteArrayInputStream(profileXML.getBytes()));
  }
  catch (java.lang.Exception e)
  {
   throw new ProfileParseException(e.toString());
  }
*/
  /*
   * catch (ParserConfigurationException e) { e.printStackTrace(); } catch
   * (IOException e) { e.printStackTrace(); } catch (SAXException e) {
   * e.printStackTrace(); }
   */
    }


    /**
     * @return Returns the userID.
     */
    public int getUserID() {
        return userID;
    }

    public static String hashPassword(String pass) {
        return hashPasswordSHA1(pass);
    }

    public static String hashPasswordMD5(String pass) {
        String digestAlg = "MD5";
        StringBuffer passhash = null;
        try {
            MessageDigest md5d = MessageDigest.getInstance(digestAlg);

            byte[] digest = md5d.digest(pass.toString().getBytes());

            passhash = new StringBuffer(40);
            passhash.append(digestAlg).append('.');

            for (int i = 0; i < digest.length; i++) {
                passhash.append(digest[i] & 0xFF);
            }

        } catch (NoSuchAlgorithmException ex) {
            Log.error("Can't get MessageDigest for algorithm: " + digestAlg);
        }

        return passhash.toString();
    }

    public static String hashPasswordSHA1(String pass) {
        String digestAlg = "SHA1";
        StringBuffer passhash = null;
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlg);

            byte[] digest = md.digest(pass.toString().getBytes());

            passhash = new StringBuffer(40);
            passhash.append(digestAlg).append('.');

            for (int i = 0; i < digest.length; i++) {
                passhash.append(Integer.toHexString(digest[i] & 0xFF));
            }

        } catch (NoSuchAlgorithmException ex) {
            Log.error("Can't get MessageDigest for algorithm: " + digestAlg);
        }

        return passhash.toString();
    }


    public boolean checkPassword(String pass) {
        String hashed = null;

        if (passhash.startsWith("SHA1.")) {
            hashed = hashPasswordSHA1(pass);
        } else if (passhash.startsWith("MD5.")) {
            hashed = hashPasswordMD5(pass);
        } else {
            hashed = hashPasswordMD5(pass).substring(5);
        }

        return passhash.equals(hashed);
    }


    public String getUserName() {
        return username;
    }


}

