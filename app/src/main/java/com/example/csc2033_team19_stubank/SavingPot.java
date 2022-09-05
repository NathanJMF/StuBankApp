package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
//shows pot details with buttons to change the pot
public class SavingPot extends AppCompatActivity{
    private ImageButton backButton;
    private Button editGoalButton, addMoneyButton, moveMoneyButton;
    private Double balance, goal;
    private TextView balanceShown, goalShown, title;
    private String formattedBalance, potName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saving_pot);

        final String email = getIntent().getStringExtra("email");
        final String pot = getIntent().getStringExtra("pot");

        // Initialises the buttons
        backButton = (ImageButton) findViewById(R.id.savingsPotBackBtn);
        final Button deleteButton = (Button) findViewById(R.id.btnSavingsPotDelete);
        editGoalButton = (Button) findViewById(R.id.btnSavingsPotEditGoal);
        addMoneyButton = (Button) findViewById(R.id.btnAddtoPot);
        moveMoneyButton = (Button) findViewById(R.id.btnTransferPotMoney);

        // Listens for when either of the buttons are clicked and appropriately directs the user to the next page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavingPot.this, Pots.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        // Listens for when either of the buttons are clicked and appropriately directs the user to the next page
        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavingPot.this, AddToPot.class);
                intent.putExtra("email", email);
                intent.putExtra("pot", pot);
                startActivity(intent);
            }
        });
        // Listens for when either of the buttons are clicked and appropriately directs the user to the next page
        moveMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavingPot.this, MovePotMoney.class);
                intent.putExtra("email", email);
                intent.putExtra("pot", pot);
                startActivity(intent);
            }
        });

        // Initialises an instance of the Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference documentReferenceBalance = db.collection("Students")
                .document(email);
        final DocumentReference documentReference = db.collection("Students")
                .document(email).collection("Pots").document(pot);

        documentReference.get().addOnCompleteListener
                (new OnCompleteListener<DocumentSnapshot>(){

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Current snapshot of the data stored for the user
                            DocumentSnapshot doc = task.getResult();
                            // Gets the user's balance
                            balance = doc.getDouble("balance");
                            // Displays the balance
                            balanceShown = (TextView) findViewById(R.id.txtSavingsPotAmount);
                            //Formats the user's balance to 2 decimal places
                            formattedBalance = String.format("%.2f", balance);
                            balanceShown.setText("£" + formattedBalance);

                            // Gets the user's goal
                            goal = doc.getDouble("goal");
                            // Displays the goal
                            goalShown = (TextView) findViewById(R.id.txtSavingsPotGoal);
                            //Formats the user's goal to 2 decimal places
                            final String formattedGoal = String.format("%.2f", goal);
                            //if above 0 show the user their goal
                            if(goal > 0){
                                goalShown.setText("Goal: £" + formattedGoal);
                            }

                            //get pot name
                            potName = doc.getString("name");
                            title = (TextView) findViewById(R.id.txtSavingsPot);
                            //set title with pot name
                            title.setText(potName + " Pot");
                            //set all pots not named Savings to show delete button
                            if(!potName.equals("Savings")){
                                deleteButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        //move to editGoal class
        editGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavingPot.this, EditGoal.class);
                intent.putExtra("email", email);
                intent.putExtra("pot", pot);
                startActivity(intent);
            }
        });
        //move to check_delete
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavingPot.this, CheckDelete.class);
                intent.putExtra("email", email);
                intent.putExtra("pot", pot);
                startActivity(intent);
                finish();
            }
        });

    }
}
