package com.example.csc2033_team19_stubank;

import java.security.SecureRandom;
/*
Author Megan Jinks and Will Holmes
Functions to generate account and card details
 */
public class GeneratingCardDetails {
    //generate 16 digit card number
    public static String generateCardNumber(){
        //add two numbers to make 16 character string
        String firstHalfCardNo = "4138542";
        String cardNumber = String.format("%s%s", firstHalfCardNo, generateNineDigit());

        return cardNumber;
    }
    //generate random 8 digit accountNumber
    public static String generateAccountNumber(){
        String accountNumber = generateEightDigit();
        return accountNumber;
    }

    //Checking card valid with Luhns algorithm
    public static boolean checkCardValid(String card){

        //covert string to array
        int[] numbers = new int[card.length()];
        for(int i=0; i< card.length(); i++){
            numbers[i] = Integer.parseInt(card.substring(i , i+1));
        }
        //doubling every other digit and if above 9 mod and add 1
        for(int i = numbers.length -2; i >= 0; i = i -2){
            int digit = numbers[i];
            digit = digit *2;
            if(digit >9){
                digit = digit % 10 + 1;
            }
            numbers[i] = digit;
        }
        //adding up digits
        int sum = 0;
        for (int j = 0; j < numbers.length; j ++){
            sum = sum + numbers[j];
        }
        //if multiple 10 return true
        return sum % 10 == 0;
    }
    //Generating random 8 digit number
    public static String generateEightDigit(){
        SecureRandom random = new SecureRandom();
        //checking it is an 8 digit number
        boolean tooSmall = false;
        int maxValue = 99999999;
        int randomValue = random.nextInt(maxValue);
        if (randomValue < 10000000){
            tooSmall = true;
        }
        while (tooSmall == true){
            randomValue = random.nextInt(maxValue);
            if(randomValue > 10000000){
                tooSmall = false;
            }
        }
        return String.valueOf(randomValue);
    }
    //generating random 9 digit number
    public static String generateNineDigit(){
        SecureRandom random = new SecureRandom();
        //checking it is a 9 digit number
        boolean tooSmall = false;
        int maxValue = 999999999;
        int randomValue = random.nextInt(maxValue);
        if (randomValue < 100000000){
            tooSmall = true;
        }
        while (tooSmall == true){
            randomValue = random.nextInt(maxValue);
            if(randomValue > 100000000){
                tooSmall = false;
            }
        }
        return String.valueOf(randomValue);
    }
    //generating random 3 digit number
    public static int generateCVV(){
        SecureRandom random = new SecureRandom();
        //checking it is 3 digit number
        boolean tooSmallCVV = false;
        int maxValueCVV = 999;
        int newCVV = random.nextInt(maxValueCVV);

        //checking 3 digits long
        if (newCVV < 100){
            tooSmallCVV = true;
        }
        while (tooSmallCVV == true){
            newCVV = random.nextInt(maxValueCVV);
            if(newCVV > 100){
                tooSmallCVV = false;
            }
        }
        return newCVV;
    }
}
