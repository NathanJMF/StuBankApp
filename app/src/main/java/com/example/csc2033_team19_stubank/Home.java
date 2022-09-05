package com.example.csc2033_team19_stubank;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
/* Author - Megan O'Doherty
   This is the app's home page. Here, the user can see their balance, balance with pots and their
   available overdraft. The five most recent transactions are also displayed here for convenience,
   allowing users to quickly see their last payment as soon as they log in. */
public class Home extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // Gets the currently logged in user's email address
        final String email = getIntent().getStringExtra("email");

        // Initialises an instance of the Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // References to the user's data, their stored pots and transactions
        final DocumentReference documentReference = db.collection("Students")
                .document(email);
        final CollectionReference pot = documentReference.collection("Pots");
        final CollectionReference transactions = documentReference.collection("Transactions");

        // Gets the data from the database for the currently logged in user
        documentReference.get().addOnCompleteListener
                (new OnCompleteListener<DocumentSnapshot>() {
                     @SuppressLint("SetTextI18n")
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         if (task.isSuccessful()) {
                             // Current snapshot of the data stored for the user
                             DocumentSnapshot doc = task.getResult();
                             // Gets the user's current balance
                             final Double[] balance = {doc.getDouble("balance")};
                             Double overdraft = doc.getDouble("overdraft");

                             final TextView tv = findViewById(R.id.txtHomeBalance);
                             // Adjusts display depending on if the balance is negative or positive
                             if (balance[0] < 0) {
                                 //Formats the user's balance to 2 decimal places
                                 @SuppressLint("DefaultLocale") final String formattedBal = String.format("%.2f", (balance[0]*-1));
                                 // Displays the balance on the 'Balance:' TextView
                                 tv.setText("Balance: -£" + formattedBal);
                             } else {
                                 //Formats the user's balance to 2 decimal places
                                 @SuppressLint("DefaultLocale") final String formattedBal = String.format("%.2f", balance[0]);
                                 // Displays the balance on the 'Balance:' TextView
                                 tv.setText("Balance: £" + formattedBal);
                             }

                             @SuppressLint("DefaultLocale") final String formatOverdraft = String.format("%.2f", overdraft);
                             // Displays the balance on the 'Balance:' TextView
                             final TextView tv2 = findViewById(R.id.txtOverdraftHome);
                             tv2.append(formatOverdraft);

                             final TextView tv3 = findViewById(R.id.txtHomeBalWithPots);
                             pot.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                 @Override
                                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                     if (task.isSuccessful()) {
                                         for (QueryDocumentSnapshot document : task.getResult()) {
                                             // Gets the balance of each of the user's pots
                                             Double potBal = document.getDouble("balance");
                                             // Adds the balance of each pot to the current balance
                                             if(potBal > 0)
                                                balance[0] += potBal;
                                         }
                                         // Adjusts display depending on if the balance is negative or positive
                                         if (balance[0] < 0) {
                                             // Formats balance to 2 decimal places
                                             @SuppressLint("DefaultLocale") final String formatBal = String.format("%.2f", (balance[0]*-1));
                                             // Displays the balance on the 'Balance including pots:' TextView
                                             tv3.setText("Balance including pots: -£" + formatBal);
                                         } else {
                                             // Formats balance to 2 decimal places
                                             @SuppressLint("DefaultLocale") final String formatBal = String.format("%.2f", balance[0]);
                                             // Displays the balance on the 'Balance including pots:' TextView
                                             tv3.setText("Balance including pots: £" + formatBal);
                                         }
                                     }
                                 }
                             });

                             // Gets the user's 5 most recent transactions
                             transactions.orderBy("date", Query.Direction.DESCENDING).limit(5)
                                     .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                 @Override
                                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                     if (task.isSuccessful()) {
                                         for (QueryDocumentSnapshot document : task.getResult()) {
                                             // Gets each transaction
                                             Double amount = document.getDouble("amount");
                                             String category = document.getString("category");

                                             Date date = Objects.requireNonNull(document.getTimestamp("date")).toDate();
                                             @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                             String strDate = dateFormat.format(date);

                                             // Displays the user's most recent transactions
                                             final TextView textView = findViewById(R.id.txtTransactionList);

                                             String amountDisplay;
                                             String newBalanceDisplay;
                                             /* Adjusts display depending on whether the transaction
                                                amount is negative or positive. */
                                             if (amount < 0) {
                                                 @SuppressLint("DefaultLocale") String formatAmount = String.format("%.2f", (amount*-1));
                                                 amountDisplay = "-£"+formatAmount;
                                             } else {
                                                 @SuppressLint("DefaultLocale") String formatAmount = String.format("%.2f", amount);
                                                 amountDisplay ="£"+formatAmount;
                                             }
                                             // Adjusts display depending on if balance is negative or positive
                                             if (document.getDouble("newBalance") < 0) {
                                                 @SuppressLint("DefaultLocale") String newBalance =
                                                         String.format("%.2f", (document.getDouble("newBalance")*-1));
                                                 newBalanceDisplay = "-£"+newBalance;
                                             } else {
                                                 @SuppressLint("DefaultLocale") String newBalance =
                                                         String.format("%.2f", document.getDouble("newBalance"));
                                                 newBalanceDisplay = "£"+newBalance;
                                             }
                                             // Adds each transaction to the page
                                             textView.append(document.getString("name") + " (" + category +
                                                         ")\n " + amountDisplay + "    (New Balance: " + newBalanceDisplay + ")\n" +
                                                         strDate + "\n\n");
                                         }
                                     }
                                 }
                             });
                         }
                     }
                 });

        
        // Initialises all the buttons on this page.
        ImageButton transferButton = findViewById(R.id.transferButtonHome);
        ImageButton accountButton = findViewById(R.id.accountButtonHome);
        ImageButton potsButton = findViewById(R.id.potsButtonHome);
        ImageButton budgetButton = findViewById(R.id.budgetButtonHome);

        // Listens for when the buttons are clicked and appropriately directs the user to
        // the correct page.
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Transfer.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, AccountPage.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        potsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Pots.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Budget.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
}
