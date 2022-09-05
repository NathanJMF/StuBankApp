package com.example.csc2033_team19_stubank;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
/*
Author Megan Jinks
Tests for function which generate account and card details
 */
public class CardGeneratingTesting {

    //test whether a valid card returns true
    @Test
    public void checkCardValidTestTrue(){
        String card = "4138542208768406";
        assertTrue(GeneratingCardDetails.checkCardValid(card));
    }

    //test whether an invalid card returns false
    @Test
    public void checkCardValidTestFalse(){
        String card = "413854220876843411";
        assertFalse(GeneratingCardDetails.checkCardValid(card));
    }

    //test whether generateEightDigit returns 8 digits
    @Test
    public void checkGenerateEightDigit(){
        String card = "11111111";
        assertEquals(card.length(), GeneratingCardDetails.generateEightDigit().length());
    }

    //test whether generateNineDigit returns 9 digits
    @Test
    public void checkGenerateNineDigit(){
        String card = "111111111";
        assertEquals(card.length(), GeneratingCardDetails.generateNineDigit().length());
    }

    //test whether generateCVV returns 3 digits
    @Test
    public void checkGenerateCVV(){
        int card = 111;
        assertEquals(card > 99, GeneratingCardDetails.generateCVV() > 99);
    }

    //test whether generateAccountNumber produces 8 digit number
    @Test
    public void checkGenerateAccountNumber(){
        String number = "12345678";
        assertEquals(number.length(), GeneratingCardDetails.generateAccountNumber().length());
    }

    //test whether generateCardNumber produces 16 digit number
    @Test
    public void checkGenerateCardNumber(){
        String number = "1234567890123456";
        assertEquals(number.length(), GeneratingCardDetails.generateCardNumber().length());
    }






}
