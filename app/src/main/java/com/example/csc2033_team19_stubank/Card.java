package com.example.csc2033_team19_stubank;

/* Author - Megan O'Doherty
   This class is used to add user card details to the Firebase Firestore. */
public class Card {

    // Card details
    private String cardNumber;
    private int cvv;
    private String expiryDate;
    private boolean frozen;

    public Card(String cardNumber, int cvv, String expiryDate, boolean frozen){
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
        this.frozen = frozen;
    }

    // Empty constructor
    public Card() {

    }

    // Getter and setter for card number
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    // Getter and setter for card CVV
    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    // Getter and setter for card expiry date
    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    // Getter and setter for card's frozen status
    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

}
