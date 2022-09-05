package com.example.csc2033_team19_stubank;

/* Author - Will Holmes
   This class is for setting up predictions  in the Predictions collection and values. */
public class Prediction {

    private Double predictedAmount;
    private Double timeDiff;

    public Prediction(Double predictedAmount, Double timeDiff){
        this.predictedAmount = predictedAmount;
        this.timeDiff = timeDiff;
    }

    // Getter and setter for predicted amount
    public Double getPredictedAmount() {
        return predictedAmount;
    }

    public void setPredictedAmount(Double balance) {
        this.predictedAmount = predictedAmount;
    }

    // Getter and setter for time difference
    public Double getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(Double timeDiff) {
        this.timeDiff = timeDiff;
    }

}