/**
 * This package provides utility functionality for the ofCourse system.
 */
package de.ofCourse.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides the services of hashing a given plaintext password concatenated with
 * a salt by using a SHA hash algorithm.
 * 
 * @author Tobias Fuchs
 *
 */
public class PasswordHash {

    /**
     * Returns the encrypted form of a given plaintext password.<br>
     * The password is encrypted by concatenating it with the given salt and
     * using a one way SHA hash algorithm.
     * 
     * @param password
     *            password you want to encrypt
     * @param salt
     *            salt you use for encryption
     *
     * @return encrypted password
     * 
     */
    public static String hash(String password, String salt) {
    	String passwordHash = null;
    	try {
    		MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i=0; i< bytes.length ;i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).
                		substring(1));
            }
            passwordHash = sb.toString();
            return passwordHash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	return null;
    }
}
