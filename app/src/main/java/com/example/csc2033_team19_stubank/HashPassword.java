/**
 * @author Karolis
 */

package com.example.csc2033_team19_stubank;

import com.google.firebase.firestore.DocumentSnapshot;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/* Author - Karolis Zilius
        This class takes a password and hashes it using a SHA 512 bit hash. It returns the hashed password and the salt.*/

public class HashPassword {

    private static byte[] saltBytes;
    private static String hashedPassword;

    // Takes and argument password and hashes it. Returns the salt used for hashing and the hashed password
    public static String [] hashPassword(String passwordToHash) throws NoSuchAlgorithmException {

        // Array for returning the hashed password and salt
        String [] result = new String [2];

        // Generate salt
        byte[] salt = getSalt();

        //Hash password using generated salt
        String securePassword = get_SHA_512_SecurePassword(passwordToHash, salt);

        // Turn byte [] into a String for easy storage using "ISO-8859-1" encoding
        String saltString = new String(salt, StandardCharsets.ISO_8859_1);

        // Store hashed password and salt in an array
        result [0] = securePassword;
        result[1] = saltString;

        return result;
    }

    // Enter the user input password and account email address to check if the has of the password
    // would match the one in the database
    public static boolean checkHashedPassword (String passwordInput, final DocumentSnapshot doc){

        // Retrieves hashed password and salt from firebase
        hashedPassword = doc.getString("password");
        String salt = doc.getString("salt");

        // Convert the String into a byte [] array to generate the hashed password
        assert salt != null;
        saltBytes = salt.getBytes(StandardCharsets.ISO_8859_1);

        // Generate a hash using the retrieved salt and user input password
        String securePassword = get_SHA_512_SecurePassword(passwordInput, saltBytes);

       // Compares the hashed password stored in the db to the one generated above
        return hashedPassword.equals(securePassword);
    }

    // Generates a SHA 512bit hash
    public static String get_SHA_512_SecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    // Generate a salt
    public static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}