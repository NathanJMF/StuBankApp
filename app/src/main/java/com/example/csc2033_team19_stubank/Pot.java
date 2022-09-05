package com.example.csc2033_team19_stubank;

/* Author - Megan O'Doherty
   This class is used to add pots to the Firebase Firestore. */
public class Pot {

    private String name;
    private Double balance;
    private Double goal;

    public Pot(String name, Double balance, Double goal){
        this.name = name;
        this.balance = balance;
        this.goal = goal;
    }

    // Getter and setter for pot name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for balance of the pot
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    // Getter and setter for the pot's saving goal
    public Double getGoal() {
        return goal;
    }

    public void setGoal(Double goal) {
        this.goal = goal;
    }

}
