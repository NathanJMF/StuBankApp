package com.example.csc2033_team19_stubank;

import com.google.firebase.firestore.DocumentSnapshot;
/*
Author Megan O'Doherty, Will Holmes, Nathan Fenwick, Megan Jinks
Functions to check user inputs
 */
public class UserInputValidation {

    //Validation for SignIn class.
    public static String SignInValidation(String email, String password) {
        if (CheckSignInFields(email, password)) {
            return "Please populate every field!";
        } else if (EmailValidation(email)) {
            return "Please enter a valid email address!";
        }
        return "";
    }

    //Check if all sign in fields are filled.
    public static boolean CheckSignInFields(String email, String password) {
        return email.isEmpty() || password.isEmpty();
    }

    //Validation for SignUp class
    public static String SignUpValidation(String firstName, String lastName, String email, String password, String repeatPass) {
        if (CheckSignUpFields(firstName, lastName, email, password, repeatPass)) {
            return "Please populate every field!";
        } else if (NameValidation(firstName)) {
            return "Names must be at least 2 characters long and no more than 25 characters.";
        } else if (NameValidation(lastName)) {
            return "Names must be at least 2 characters long and no more than 25 characters.";
        } else if (EmailValidation(email)) {
            return "Please enter a valid university email address! (.ac.uk)";
        } else if (PasswordRepeatValidation(password, repeatPass)) {
            return "These passwords do not match!";
        } else if (PasswordValidation(password)) {
            return "Please enter a strong password! Password must be between 8 and 20 characters and include " +
                    "at least one lowercase and uppercase character, one number and one special character.";
        }
        return "";
    }

    //Check if all sign in fields are filled.
    public static boolean CheckSignUpFields(String firstName, String lastName, String email, String password, String repeatPass) {
        return firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPass.isEmpty();
    }

    //Check if user has entered a valid name.
    public static boolean NameValidation(String name) {
        return !name.matches("^\\S{2,25}$");
    }

    //Check if user has entered a valid university email address (example@example.ac.uk).
    public static boolean EmailValidation(String email) {
        return !email.matches("^[a-zA-Z0-9./!?#~*\\-_']+@[a-zA-Z0-9.\\-]+\\.ac\\.uk$");
    }

    //Check if password and repeat password match.
    public static boolean PasswordRepeatValidation(String password, String repeatPass) {
        return !password.equals(repeatPass);
    }

    //Check if user has entered a valid password.
    public static boolean PasswordValidation(String password) {
        return !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!?]).{8,20}$");
    }

    //Checks to see if the string is an actual integer.
    public static boolean checkCNumber(String CNumber) {
        try {
            double d = Double.parseDouble(CNumber);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    // Validation for making money transfers
    public static String transferValidation(String amountStr, Double amount, String sortCode, int accNumber,
                                            String password, DocumentSnapshot doc) {
        if(!checkTwoDecimalPlaces(amountStr)){
            return ("Please enter an amount that contains two decimal places.");
        } else if (checkTransferFields(amount, sortCode, accNumber, password)) {
            return ("Please enter a value into each field.");
        } else if (checkSortCode(sortCode)) {
            return ("Please enter the payee's sort code in the format XX-XX-XX.");
        } else if (checkAccountNumber(accNumber)) {
            return ("Please enter an 8-digit account number for the payee.");
        } else if (!HashPassword.checkHashedPassword(password, doc)) {
            return ("The password you have entered does not match your account password.");
        }
        return "";
    }

    // Checks if all fields are filled in the transfer page
    public static boolean checkTransferFields(Double amount, String sortCode, Integer accNumber, String password) {
        return amount == 0.0 || sortCode.equals("") || accNumber == 0 || password.equals("");
    }

    // Checks that the inputted sort code matches the format XX-XX-XX (where X is a digit)
    public static boolean checkSortCode(String sortCode) {
        return !sortCode.matches("\\d{2}-\\d{2}-\\d{2}");
    }

    // Checks that the inputted account number is an 8 digit number
    public static boolean checkAccountNumber(int accNumber) {
        String accNo = String.valueOf(accNumber);
        return accNo.length() != 8;
    }

    // Validation for the change password page
    public static String changePasswordValidation(String enteredPass, String newPass, String rePass, DocumentSnapshot doc) {
        if (checkChangePassword(enteredPass, newPass, rePass)) {
            return ("Please enter a value in each field.");
        } else if (!HashPassword.checkHashedPassword(enteredPass, doc)) {
            return ("The password you have entered does not match your account password.");
        } else if (PasswordRepeatValidation(newPass, rePass)) {
            return ("The new passwords you have entered do not match.");
        }
        else if (PasswordValidation(newPass)) {
            return "Your password must be between 8 and 20 characters and include " +
                    "at least one lowercase and uppercase character, one number and one special character.";
        }
        return "";
    }

    // Checks that all fields are filled in the change password page
    public static boolean checkChangePassword(String oldPass, String newPass, String rePass) {
        return oldPass.equals("") || newPass.equals("") || rePass.equals("");
    }

    // Validation for the reset password page (new and repeated new passwords)
    public static String resetPasswordValidation(String newPass, String rePass) {
        if (checkResetPassword(newPass, rePass)) {
            return ("Please enter a value in each field.");
        }
        else if (PasswordRepeatValidation(newPass, rePass)) {
            return ("The new passwords you have entered do not match.");
        }
        else if (PasswordValidation(newPass)) {
            return "Your password must be between 8 and 20 characters and include " +
                    "at least one lowercase and uppercase character, one number and one special character.";
        }
        return "";
    }

    // Validation for the first part of the reset password page (email and authorisation code)
    public static String rpCheck(String email, String authCode){
        /* checkResetPassword method is used here as it is a presence check for two fields.
           It removes the need to create another method which would have the same functionality. */
        if(checkResetPassword(email, authCode)){
            return ("Please enter a value in each field.");
        }
        else if(EmailValidation(email)){
            return ("Please enter a valid email address (ending in .ac.uk).");
        }
        return "";
    }

    // Checks that all fields are filled in the reset password page
    public static boolean checkResetPassword(String newPass, String rePass) {
        return newPass.equals("") || rePass.equals("");
    }

    //Cheks that balance is higher or equal to money inputted
    public static boolean moneyCheck(double balance, double moneyInput){
        return balance >= moneyInput;
    }

    //Check fields for user input in NewPot class are empty
    public static boolean checkNewPotFields(String newPot, String money, String goal){
        return newPot.equals("") || money.equals("") || goal.equals("");
    }

    //Check fields for user input in MovePotMoney class are empty
    public static boolean checkTakePotFields(String namePot, String money, String password){
        return namePot.equals("") || money.equals("") || password.equals("");
    }

    //Check fields for user input in AddToPot class are empty
    public static boolean checkAddtoPotFields(String money, String password){
        return money.equals("") || password.equals("");
    }

    // Checks that the entered data is a number with two decimal places
    public static boolean checkTwoDecimalPlaces(String number){
        return (number.matches("\\d+.\\d{2}"));
    }

    // Checks that entered data is either empty or two decimal places
    public static boolean checkNewBudget(String number){
        if (number.length() > 0){
            if (checkTwoDecimalPlaces(number)){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    //Checks all budgets are either empty or two decimal places
    public static boolean checkSetBudgets(String groceries, String shopping, String bills, String entertainment,
                                          String eatingOut, String university, String transport,String other) {
        return checkNewBudget(groceries) && checkNewBudget(shopping) && checkNewBudget(bills) && checkNewBudget(entertainment)
                && checkNewBudget(eatingOut) && checkNewBudget(university) && checkNewBudget(transport) && checkNewBudget(other);
    }

}