package com.example.csc2033_team19_stubank;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

//Author Megan Jinks
//Test for function of formatting cardNumber

public class DisplayingCardTesting {

    //Test whether card is being formatted correctly with space after every 4 numbers
    @Test
    public void formatCardNumberTest(){
        String number = "1234567890123456";
        assertEquals("1234 5678 9012 3456 ", CardDetails.formatCardNumber(number));
    }
}
