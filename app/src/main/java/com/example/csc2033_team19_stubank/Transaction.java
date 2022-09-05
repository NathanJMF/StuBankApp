package com.example.csc2033_team19_stubank;

import com.google.firebase.firestore.FieldValue;

/* Author - Megan O'Doherty
   This class is used to add transactions to the Firebase Firestore. */
public class Transaction {
    // Transaction details to be displayed on app
    private String name;
    private FieldValue date;
    private Double amount;
    private String category;
    private String reference;
    private Double previousBalance;
    private Double newBalance;
    private int timeDiff;
    private int dayOfWeek;
    private boolean includeBudget;

    // Getter and setter for transaction amount
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    // Getter and setter for category
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Getter and setter for date of transaction
    public FieldValue getDate() {
        return date;
    }

    public void setDate(FieldValue date) {
        this.date = date;
    }

    // Getter and setter for transaction name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for transaction reference
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    // Getter and setter for the account's balance before the transaction
    public Double getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(Double previousBalance) {
        this.previousBalance = previousBalance;
    }

    // Getter and setter for account's new balance
    public Double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(Double newBalance) {
        this.newBalance = newBalance;
    }

    // Getter and setter for transaction time difference
    public int getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(int timeDiff) {
        this.timeDiff = timeDiff;
    }

    // Getter and setter for day of week of transaction
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    // Getter and setter for whether to add transaction to user's budget
    public boolean getIncludeBudget() {
        return includeBudget;
    }

    public void setIncludeBudget(boolean includeBudget) {
        this.includeBudget = includeBudget;
    }

    // Empty constructor
    public Transaction(){

    }

    public Transaction(Double amount, String category, FieldValue date, String name, String reference,
                       Double previousBalance, Double newBalance, int timeDiff, int dayOfWeek, boolean includeBudget){
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.name = name;
        this.reference = reference;
        this.previousBalance = previousBalance;
        this.newBalance = newBalance;
        this.timeDiff = timeDiff;
        this.dayOfWeek = dayOfWeek;
        this.includeBudget = includeBudget;
    }
}
