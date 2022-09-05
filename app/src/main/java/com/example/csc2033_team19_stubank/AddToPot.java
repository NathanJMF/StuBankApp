package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//Author Megan Jinks
//gets user to input balance and password and check it is correct and enough money in account
//updates new pot balance and account balance
public class AddToPot extends AppCompatActivity {
    private Button submitButton;
    private ImageButton backButton;
    private EditText amountEntered, passwordInput;
    private double moneyInt;
    private TextView alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_pot);

        final String email = getIntent().getStringExtra("email");
        final String pot = getIntent().getStringExtra("pot");

        //initialise buttons
        submitButton = (Button) findViewById(R.id.addMoneyPotButton);
        backButton = (ImageButton) findViewById(R.id.backButtonaddMoney);

        //moves user back to the pot page they were on
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddToPot.this, SavingPot.class);
                intent.putExtra("email", email);
                intent.putExtra("pot", pot);
                startActivity(intent);
            }
        });

        // Listens for when the button is clicked and appropriately directs the user to the next page.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialises an instance of the Cloud Firestore
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference docRef = db.collection("Students").document(email);
                final DocumentReference docRefPot = db.collection("Students").document(email)
                        .collection("Pots").document(pot);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // Current snapshot of the data stored for the user
                        final DocumentSnapshot doc = task.getResult();
                        // Gets the user's balance
                        final double balance = doc.getDouble("balance");

                        //initialising user inputs
                        amountEntered = (EditText) findViewById(R.id.amountAdd);
                        final String amount = amountEntered.getText().toString();
                        passwordInput = (EditText) findViewById(R.id.InputPasswordAddMoney);
                        final String password = passwordInput.getText().toString();
                        //check if user has filled fields
                        if(!(UserInputValidation.checkAddtoPotFields(amount, password))){
                            //check that user input for amount had 2 decimal places
                            if(UserInputValidation.checkTwoDecimalPlaces(amount)){
                                //check that amount is not empty then changing from string to double
                                moneyInt = 0;
                                if (!amount.equals("")) {
                                    moneyInt = Double.parseDouble(amount);
                                }
                                //Check user has enough money in account
                                if(UserInputValidation.moneyCheck(balance, moneyInt)){
                                    //Check user password is correct
                                    if(HashPassword.checkHashedPassword(password, doc)){
                                        final double finalMoneyInt = moneyInt;
                                        docRefPot.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                // Current snapshot of the data stored for the user
                                                final DocumentSnapshot doc2 = task.getResult();
                                                // Gets the user's balance
                                                final double balancePot = doc2.getDouble("balance");
                                                //update pot balance and account balance
                                                docRefPot.update("balance", balancePot + finalMoneyInt);
                                                docRef.update("balance", balance - finalMoneyInt);

                                                //move back to pot page
                                                Intent intent = new Intent(AddToPot.this, SavingPot.class);
                                                intent.putExtra("email", email);
                                                intent.putExtra("pot", pot);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                    else{
                                        alert = (TextView) findViewById(R.id.resultAddPot);
                                        alert.setText("Wrong password! Try again");
                                    }
                                }else{
                                    alert = (TextView) findViewById(R.id.resultAddPot);
                                    alert.setText("You do not have enough money in your account");
                                }
                            }else{
                                alert = (TextView) findViewById(R.id.resultAddPot);
                                alert.setText("The number needs to be 2 decimal places");
                            }
                        }
                        else{
                            alert = (TextView) findViewById(R.id.resultAddPot);
                            alert.setText("Please fill all fields");
                        }
                    }
                });
            }
        });
    }
}
