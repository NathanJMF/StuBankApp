package com.example.csc2033_team19_stubank;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
/*
Author Megan Jinks and Megan O'Doherty
Tests to check function working correctly in UserInputValidationClass
 */
public class UserInputValidationTesting {

    //Check returns false when all fields have an input in Add_to_pot page
    @Test
    public void testAddToPotFieldsFull(){
        String money = "15";
        String password = "password";
        assertFalse(UserInputValidation.checkAddtoPotFields(money, password));
    }

    //Check returns true when fields empty in Add_to_pot page
    @Test
    public void testAddToPotFieldsEmpty(){
        String money = "";
        String password = "";
        assertTrue(UserInputValidation.checkAddtoPotFields(money, password));
    }

    //check returns false when all fields have an input in Move_pot_money page
    @Test
    public void testTakePotFieldsFull(){
        String pot = "pot";
        String money = "10";
        String password = "password";
        assertFalse(UserInputValidation.checkTakePotFields(pot, money, password));
    }

    //check returns true when all fields empty in Move_pot_money page
    @Test
    public void testTakePotFieldsEmpty(){
        String pot = "";
        String money = "";
        String password = "";
        assertTrue(UserInputValidation.checkTakePotFields(pot, money, password));
    }

    //check returns false when all fields have an input in New_pot page
    @Test
    public void testNewPotFieldsFull(){
        String pot = "pot";
        String money = "10";
        String goal = "100";
        assertFalse(UserInputValidation.checkNewPotFields(pot, money, goal));
    }

    //check returns false when all fields empty in New_pot page
    @Test
    public void testNewPotFieldsEmpty(){
        String pot = "";
        String money = "";
        String goal = "";
        assertTrue(UserInputValidation.checkNewPotFields(pot, money, goal));
    }

    //check that when balance is larger user input it returns true
    @Test
    public void testMoneycheckTrue(){
        double balance = 10;
        double input = 5;
        assertTrue(UserInputValidation.moneyCheck(balance, input));
    }

    //check that when balance is smaller user input it returns false
    @Test
    public void testMoneycheckFalse(){
        double balance = 10;
        double input = 25;
        assertFalse(UserInputValidation.moneyCheck(balance, input));
    }

    // Checks that the validation method returns true when the transfer fields are empty.
    @Test
    public void testTransferFieldsEmpty(){
        double amount = 0;
        String sortCode = "";
        int accNumber = 0;
        String password = "";
        assertTrue(UserInputValidation.checkTransferFields(amount, sortCode, accNumber, password));
    }

    // Checks that the validation method returns false when the transfer fields contain data.
    @Test
    public void testTransferFieldsFull(){
        double amount = 100.00;
        String sortCode = "12-34-56";
        int accNumber = 12345678;
        String password = "Password1!";
        assertFalse(UserInputValidation.checkTransferFields(amount, sortCode, accNumber, password));
    }

    /* Checks that the sort code is in the correct format. If the sort code is not entered in
       the format XX-XX-XX, this should return true. */
    @Test
    public void testCheckSortCodeTrue(){
        String sortCode = "123456";
        assertTrue(UserInputValidation.checkSortCode(sortCode));
    }

    // Checks that the validation returns false when the sort code is in the correct format.
    @Test
    public void testCheckSortCodeFalse(){
        String sortCode = "12-34-56";
        assertFalse(UserInputValidation.checkSortCode(sortCode));
    }

    /* Tests whether the validation for account number does not accept numbers that are not
       8 digits in length. */
    @Test
    public void testCheckAccountNumberTrue(){
        int accNumberShort = 1234;
        int accNumberLong = 1234567890;
        assertTrue(UserInputValidation.checkAccountNumber(accNumberShort));
        assertTrue(UserInputValidation.checkAccountNumber(accNumberLong));
    }

    // Checks that the account number validation returns false (i.e. valid data) with an 8-digit number
    @Test
    public void testCheckAccountNumberFalse(){
        int accNumber = 12345678;
        assertFalse(UserInputValidation.checkAccountNumber(accNumber));
    }

    // Checks that the validation method returns true when the fields for changing password are empty.
    @Test
    public void testCheckChangePasswordTrue(){
        String oldPass = "";
        String newPass = "";
        String rePass = "";
        assertTrue(UserInputValidation.checkChangePassword(oldPass, newPass, rePass));
    }

    // Checks that the validation returns false when the fields for changing password contain data.
    @Test
    public void testCheckChangePasswordFalse(){
        String oldPass = "password";
        String newPass = "NewPassword123!";
        String rePass = "NewPassword123!";
        assertFalse(UserInputValidation.checkChangePassword(oldPass, newPass, rePass));
    }

    // Checks that the validation method returns true when the fields for resetting passwords are empty.
    @Test
    public void testCheckResetPasswordTrue(){
        String newPass = "";
        String rePass = "";
        assertTrue(UserInputValidation.checkResetPassword(newPass, rePass));
    }

    // Checks that the validation returns false when the fields for resetting passwords contain data.
    @Test
    public void testCheckResetPasswordFalse(){
        String newPass = "NewPass2?";
        String rePass = "NewPass2?";
        assertFalse(UserInputValidation.checkResetPassword(newPass, rePass));
    }

    // Tests whether the password repeat validation returns true when the inputted passwords do not match.
    @Test
    public void testCheckPasswordRepeatTrue(){
        String newPass = "Password1!";
        String rePass = "password1!";
        assertTrue(UserInputValidation.PasswordRepeatValidation(newPass, rePass));
    }

    // Tests that the password repeat validation returns false when the passwords match.
    @Test
    public void testCheckPasswordRepeatFalse(){
        String newPass = "NewPassword1!";
        String rePass = "NewPassword1!";
        assertFalse(UserInputValidation.PasswordRepeatValidation(newPass, rePass));
    }

    /* Checks the strict password validation on a number of invalid passwords, to ensure
       they are all rejected. */
    @Test
    public void testPasswordValidationTrue(){
        String invalidPass1 = "password";
        String invalidPass2 = "password1";
        String invalidPass3 = "password1!";
        assertTrue(UserInputValidation.PasswordValidation(invalidPass1));
        assertTrue(UserInputValidation.PasswordValidation(invalidPass2));
        assertTrue(UserInputValidation.PasswordValidation(invalidPass3));
    }

    // Checks that the password validation method accepts a valid password.
    @Test
    public void testPasswordValidationFalse(){
        String validPassword = "Password1!";
        assertFalse(UserInputValidation.PasswordValidation(validPassword));
    }

    //Check that returns true when number has 2 decimal places
    @Test
    public void testCheckTwoDecimalPlacesTrue(){
        String number = "12.50";
        assertTrue(UserInputValidation.checkTwoDecimalPlaces(number));
    }

    //Check that returns false when number doesn't has 2 decimal places
    @Test
    public void testCheckTwoDecimalPlacesFalse(){
        String number = "12.5";
        assertFalse(UserInputValidation.checkTwoDecimalPlaces(number));
    }

    //return true if either field is empty
    @Test
    public void testCheckSignInFieldsTrue(){
        String email = "";
        String password = "";
        assertTrue(UserInputValidation.CheckSignInFields(email, password));
    }

    //return false if both fields are full
    @Test
    public void testCheckSignInFieldsFalse(){
        String email = "test@email.com";
        String password = "password";
        assertFalse(UserInputValidation.CheckSignInFields(email, password));
    }

    //Check that correct messages output when field is empty or wrong format of email
    @Test
    public void testSignInValidation(){
        String emailCorrect = "test@newcastle.ac.uk";
        String emailWrong = "test@email.com";
        String passwordEmpty = "";
        String password = "password";
        assertEquals("Please populate every field!",UserInputValidation.SignInValidation(emailCorrect, passwordEmpty));
        assertEquals("Please enter a valid email address!", UserInputValidation.SignInValidation(emailWrong, password));
    }

}
