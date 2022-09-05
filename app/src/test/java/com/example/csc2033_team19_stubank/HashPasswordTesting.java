/**
 * @author Karolis
 */

package com.example.csc2033_team19_stubank;

import org.junit.Test;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class HashPasswordTesting {

    // Checks if a different 16 bit salt is being generated every time
    @Test
    public void checkGetSalt() throws NoSuchAlgorithmException {

        byte[] saltOne = HashPassword.getSalt();
        byte[] saltTwo = HashPassword.getSalt();

        assertEquals(16, saltOne.length);
        assertEquals(16, saltTwo.length);
        assertNotSame(saltOne, saltTwo);

    }

    // Checks if hashPassword functions return a hashed password with 128 char length
    @Test
    public void checkGet_SHA_512_SecurePassword() throws NoSuchAlgorithmException {

        String password = "Password123!";

        String hashedPassword = HashPassword.get_SHA_512_SecurePassword(password, HashPassword.getSalt());

        assertNotSame(hashedPassword, password);
        assertEquals(128, hashedPassword.length());

    }

    // Check if hashPassword joins get_SHA_512_SecurePassword() and getSalt() together and turns
    // salt into a string for storing in the firebase
    @Test
    public void checkHashPassword() throws NoSuchAlgorithmException {

        String password = "Password123!";

        String [] hashedPassword = HashPassword.hashPassword(password);

        assertNotSame(hashedPassword[0], password);
        assertEquals(128, hashedPassword[0].length());
        assertSame(hashedPassword[1].getClass(), String.class);
        assertEquals(16, hashedPassword[1].length());

    }
}
