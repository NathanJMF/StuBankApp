/**
 * @author Karolis
 */

package com.example.csc2033_team19_stubank;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class TwoFactorAuthenticationTesting {

    // Check if the generated keys are unique
    @Test
    public void checkGenerateSecretKey(){

        String keyOne = TwoFactorAuthentication.generateSecretKey();
        String keyTwo = TwoFactorAuthentication.generateSecretKey();

        assertNotSame(keyOne, keyTwo);
    }

    // Check if the bar code generation returns a string that is not null
    @Test
    public void checkGetGoogleAuthenticatorBarCode(){

        String BarCode = TwoFactorAuthentication.getGoogleAuthenticatorBarCode(TwoFactorAuthentication.generateSecretKey(), "test@email.com", "StuBank");

        assertSame(BarCode.getClass(), String.class);
        assertNotNull(BarCode);

    }

    // Check if a new TOTP code gets generated every 30 seconds
    @Test
    public void checkGetTOTPCode() {

        String secretKey = TwoFactorAuthentication.generateSecretKey();
        String code = TwoFactorAuthentication.getTOTPCode(secretKey);
        String lastCode = code;
        boolean check = true;

        while (check) {
            code = TwoFactorAuthentication.getTOTPCode(secretKey);
            if (!code.equals(lastCode)) {
                lastCode = code;
                code = TwoFactorAuthentication.getTOTPCode(secretKey);
                check = false;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }

        assertNotSame(code, lastCode);

    }
}