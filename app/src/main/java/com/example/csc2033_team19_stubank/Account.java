package com.example.csc2033_team19_stubank;

import com.google.firebase.firestore.FieldValue;

/* Author - Megan O'Doherty
   This class is used to add user account details to the Firebase Firestore. */
public class Account {

    // Account information
    private String accountNumber;
    private String sortCode;
    private Double balance;
    private Double overdraft;
    private FieldValue creationDate;
    private boolean roundUp;

    public Account(String accountNumber, String sortCode, Double balance, Double overdraft, FieldValue creationDate, boolean roundUp){
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.balance = balance;
        this.overdraft = overdraft;
        this.creationDate = creationDate;
        this.roundUp = roundUp;
    }

    // Empty constructor
    public Account() {

    }

    // Getter and setter for account number
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    // Getter and setter for bank sort code
    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    // Getter and setter for current account balance
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    // Getter and setter for current account overdraft
    public Double getOverdraft() {
        return overdraft;
    }

    public void setOverdraft(Double overdraft) {
        this.overdraft = overdraft;
    }

    // Getter and setter for account creation date
    public FieldValue getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(FieldValue creationDate) {
        this.creationDate = creationDate;
    }

    // Getter and setter for rounding up transactions
    public boolean isRoundUp() { return roundUp; }

    public void setRoundUp(boolean roundUp) { this.roundUp = roundUp; }
}
