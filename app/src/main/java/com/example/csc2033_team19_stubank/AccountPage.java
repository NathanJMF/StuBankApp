package com.example.csc2033_team19_stubank;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
/*
Author Megan Jinks and Megan O'Doherty
Displays user's balance and overdraft
Buttons to take to 5 main pages, statements, freeze account, view transactions, predict spending
and card details.
Dialog boxes for freezing card
 */
public class AccountPage extends AppCompatActivity {
    private Double balance, overdraft;
    private TextView balanceText, overdraftText;
    private ImageButton transferButton, homeButton, potsButton, settingButton, budgetButton, accountDetailsButton,
            freezeButton, statementButton, viewTransactionsButton, predictSpendingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page);

        final String email = getIntent().getStringExtra("email");

        // Initialises the buttons on the bottom of the page.
        transferButton = findViewById(R.id.transferButtonAccountPage);
        homeButton = findViewById(R.id.homeButtonAccountPage);
        potsButton =  findViewById(R.id.potButtonAccountPage);
        settingButton = findViewById(R.id.settingButton);
        budgetButton =  findViewById(R.id.budgetButtonAccountPage);
        accountDetailsButton =  findViewById(R.id.accountDetailsButton2);
        freezeButton = findViewById(R.id.freezeButton);
        statementButton = findViewById(R.id.statementButton);
        viewTransactionsButton = findViewById(R.id.viewTransactionsButton);
        predictSpendingButton = findViewById(R.id.predictSpendingButton);

        // Initialises an instance of the Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference documentReference = db.collection("Students").document(email);
        documentReference.get().addOnCompleteListener
                (new OnCompleteListener<DocumentSnapshot>() {

                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         if (task.isSuccessful()) {
                            // Current snapshot of the data stored for the user
                             DocumentSnapshot doc = task.getResult();
                             // Gets the user's balance
                             balance = doc.getDouble("balance");
                             balanceText = (TextView) findViewById(R.id.balanceAccountPage);
                             //Formats the user's balance to 2 decimal places and display it
                             if (balance < 0) {
                                 @SuppressLint("DefaultLocale") String formattedBal = String.format("%.2f", (balance*-1));
                                 balanceText.setText("Balance: -£" + formattedBal);
                             } else {
                                 @SuppressLint("DefaultLocale") String formattedBal = String.format("%.2f", balance);
                                 balanceText.setText("Balance: £" + formattedBal);
                             }

                             // Gets the user's overdraft
                             overdraft = doc.getDouble("overdraft");
                             overdraftText = (TextView) findViewById(R.id.overdraftAccountPage);
                             //Formats the user's overdraft to 2 decimal places and display it
                             @SuppressLint("DefaultLocale") String formattedOverdraft = String.format("%.2f", overdraft);
                             overdraftText.setText("Remaining Overdraft: £" + formattedOverdraft);

                         }
                     }
                 });

        // Listens for when the buttons are clicked and directs the user to the correct page.
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountPage.this, Transfer.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountPage.this, Home.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        potsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountPage.this, Pots.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountPage.this, Budget.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        accountDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountPage.this, CardDetails.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        freezeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference.get().addOnCompleteListener
                        (new OnCompleteListener<DocumentSnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Current snapshot of the data stored for the user
                                    DocumentSnapshot doc = task.getResult();
                                    // Gets the user's card frozen status
                                    boolean frozen = doc.getBoolean("frozen");
                                    // Creates a dialog box
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AccountPage.this);
                                    if(!frozen){
                                        // Asks the user if they want to freeze their card for confirmation
                                        builder.setMessage("Are you sure you would like to freeze your card?");
                                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // If user clicks 'yes' - freezes the card
                                                documentReference.update("frozen", true);
                                                Toast.makeText(AccountPage.this, "Your " +
                                                        "card has been frozen.", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(AccountPage.this, AccountPage.class);
                                                intent.putExtra("email", email);
                                                startActivity(intent);
                                            }
                                        });
                                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // If user clicks 'no' - refreshes the account page
                                                Intent intent = new Intent(AccountPage.this, AccountPage.class);
                                                intent.putExtra("email", email);
                                                startActivity(intent);
                                            }
                                        });
                                        builder.create().show();
                                    }
                                    else if(frozen){
                                        // Confirms that the user would like to unfreeze their card
                                        builder.setMessage("Are you sure you would like to unfreeze your card?");
                                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // If user clicks 'yes' - unfreezes the card
                                                documentReference.update("frozen", false);
                                                Toast.makeText(AccountPage.this, "Your " +
                                                        "card has been unfrozen.", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(AccountPage.this, AccountPage.class);
                                                intent.putExtra("email", email);
                                                startActivity(intent);
                                            }
                                        });
                                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // If user clicks 'no' - refreshes the account page
                                                Intent intent = new Intent(AccountPage.this, AccountPage.class);
                                                intent.putExtra("email", email);
                                                startActivity(intent);
                                            }
                                        });
                                        // Shows the dialog box
                                        builder.create().show();
                                    }
                                }
                            }
                        });
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AccountPage.this, Settings.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        statementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AccountPage.this, Statement.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        viewTransactionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AccountPage.this, ViewTransactions.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        predictSpendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AccountPage.this, PredictFutureSpending.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
}