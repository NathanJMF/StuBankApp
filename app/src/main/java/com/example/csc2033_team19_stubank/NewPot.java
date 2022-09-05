package com.example.csc2033_team19_stubank;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Author Megan Jinks
//Creates new pot
public class NewPot extends AppCompatActivity {
    private ImageButton backButton;
    private Button submitButton;
    private EditText namePotInput, moneyInput, goalInput, passwordInput;
    private Double moneyInt, goalInt;
    private TextView alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_pot_page);

        final String email = getIntent().getStringExtra("email");

        // Initialises the buttons
        backButton = (ImageButton) findViewById(R.id.newPotBackBtn);

        // Listens for when the button is clicked and appropriately directs the user to the next page.
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPot.this, Pots.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        // Initialises the buttons.
        submitButton = (Button) findViewById(R.id.btnCreateNewPot);

        // Listens for when the button is clicked and appropriately directs the user to the next page.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialises an instance of the Cloud Firestore
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference docRef = db.collection("Students").document(email);

                docRef.get().addOnCompleteListener
                        (new OnCompleteListener<DocumentSnapshot>() {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Current snapshot of the data stored for the user
                                    final DocumentSnapshot doc = task.getResult();

                                    //initialising user inputs
                                    namePotInput = (EditText) findViewById(R.id.editTextNewPotName);
                                    final String newPot = namePotInput.getText().toString();

                                    moneyInput = (EditText) findViewById(R.id.editTextNewPotAmount);
                                    final String money = moneyInput.getText().toString();
                                    //if money not empty turns to Double
                                    moneyInt = 0.0;
                                    if (!money.equals("")) {
                                        moneyInt = Double.parseDouble(money);
                                    }

                                    goalInput = (EditText) findViewById(R.id.editTextNewPotGoal);
                                    final String goal = goalInput.getText().toString();
                                    //if goal not empty turns to Double
                                    goalInt = 0.0;
                                    if (!goal.equals("")) {
                                        goalInt = Double.parseDouble(goal);
                                    }

                                    //create hashmap for new pot with user inputs
                                    final Map<String, Object> pot = new HashMap<>();
                                    pot.put("name", newPot);
                                    pot.put("balance", moneyInt);
                                    pot.put("goal", goalInt);

                                    //get users balance
                                    final Double balance = doc.getDouble("balance");
                                    //check user has enough money in account
                                    if (UserInputValidation.moneyCheck(balance, moneyInt)) {
                                        //check fields aren't empty
                                        if(!(UserInputValidation.checkNewPotFields(newPot, money, goal))){
                                            //check money and goal inputs have 2 decimal places
                                            if(UserInputValidation.checkTwoDecimalPlaces(money) && UserInputValidation.checkTwoDecimalPlaces(goal)){
                                                final DocumentReference dbRef = db.collection("Students").document(email)
                                                        .collection("Pots").document(newPot);
                                                final double finalMoneyInt = moneyInt;
                                                dbRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            // Current snapshot of the data stored for the user
                                                            DocumentSnapshot doc2 = task.getResult();
                                                            //Check to see if pot doesn't already exist
                                                            if(!doc2.exists()) {
                                                                db.collection("Students").document(email)
                                                                        .collection("Pots").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            //creates list of pots from database
                                                                            List<DocumentSnapshot> listPots = task.getResult().getDocuments();
                                                                            //check list pots smaller than 11
                                                                            if(listPots.size() < 11){
                                                                                passwordInput = (EditText) findViewById(R.id.editTextNewPotPassword);
                                                                                final String passwordText = passwordInput.getText().toString();
                                                                                //check password is correct
                                                                                if (HashPassword.checkHashedPassword(passwordText, doc)) {
                                                                                    //add pot to database and update new account balance
                                                                                    dbRef.set(pot);
                                                                                    docRef.update("balance", balance - finalMoneyInt);
                                                                                    //move to pot page
                                                                                    Intent intent = new Intent(NewPot.this, Pots.class);
                                                                                    intent.putExtra("email", email);
                                                                                    startActivity(intent);

                                                                                } else {
                                                                                    alert = (TextView) findViewById(R.id.txtNewPotErrorMessage);
                                                                                    alert.setText("Wrong password! Try again");
                                                                                }
                                                                            }
                                                                            else{
                                                                                alert = (TextView) findViewById(R.id.txtNewPotErrorMessage);
                                                                                alert.setText("You already have the maximum amount of pots");
                                                                            }

                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            else{
                                                                alert = (TextView) findViewById(R.id.txtNewPotErrorMessage);
                                                                alert.setText("This pot already exists");
                                                            }
                                                        }
                                                    }
                                                });
                                            }else{
                                                alert = (TextView) findViewById(R.id.txtNewPotErrorMessage);
                                                alert.setText("money and goal need to have 2 decimal places");
                                            }

                                                }
                                        else{
                                            alert = (TextView) findViewById(R.id.txtNewPotErrorMessage);
                                            alert.setText("You need to fill all fields");
                                        }
                                        }
                                    else {
                                        alert = (TextView) findViewById(R.id.txtNewPotErrorMessage);
                                        alert.setText("You do not have enough money in your account!");
                                    }
                                    }
                                }

                                });
                            }
                        });
            }

        }


