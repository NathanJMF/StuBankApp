package com.example.csc2033_team19_stubank;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//Author Megan Jinks
//Test to check function in PredictFutureSpending when predictions zero
public class PredictFutureSpendingTesting {

    //Check that returns true when input 0
    @Test
    public void checkIfPredictZeroTestTrue(){
        Double example= 0.0;
        assertTrue(PredictFutureSpending.checkIfPredictZero(example));
    }

    //Check that returns false when input not 0
    @Test
    public void checkIfPredictZeroTestFalse(){
        Double example= 5.50;
        assertFalse(PredictFutureSpending.checkIfPredictZero(example));
    }
}
