package com.example.csc2033_team19_stubank;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/* Author - Will Holmes
   This class is for viewing transactions. It shows the user all of their past transactions in descending time
    order (most recent to oldest). The user can tap on any transaction to be able to edit the transaction
    details (transaction name, category and budget inclusion status). */

public class ViewTransactions extends AppCompatActivity {

    private ImageButton backButton;
    private ListView transactionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_transactions);

        // Gets email of user
        final String email = getIntent().getStringExtra("email");

        final List<String> transactionsList = new ArrayList<>();
        final List<String> transactionsIDList = new ArrayList<>();

        backButton = (ImageButton) findViewById(R.id.viewTransactionsBackButton);
        transactionsListView = findViewById(R.id.viewTransactionsListView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference documentReference = db.collection("Students").document(email);
        final CollectionReference transactions = documentReference.collection("Transactions");

        //Gets all records of user transactions in descending order
        transactions.orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Gets each transaction
                        Double amount = document.getDouble("amount");
                        String category = document.getString("category");
                        String id = document.getId();

                        //Formats date to correct format
                        Date date = Objects.requireNonNull(document.getTimestamp("date")).toDate();
                        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                        String strDate = dateFormat.format(date);

                        //Formats money amounts depending on negative or positive
                        String amountDisplay;
                        String newBalanceDisplay;
                        if (amount < 0) {
                            @SuppressLint("DefaultLocale") String formatAmount = String.format("%.2f", (amount*-1));
                            amountDisplay = "-£"+formatAmount;
                        } else {
                            @SuppressLint("DefaultLocale") String formatAmount = String.format("%.2f", amount);
                            amountDisplay ="£"+formatAmount;
                        }
                        if (document.getDouble("newBalance") < 0) {
                            @SuppressLint("DefaultLocale") String newBalance =
                                    String.format("%.2f", (document.getDouble("newBalance")*-1));
                            newBalanceDisplay = "-£"+newBalance;
                        } else {
                            @SuppressLint("DefaultLocale") String newBalance =
                                    String.format("%.2f", document.getDouble("newBalance"));
                            newBalanceDisplay = "£"+newBalance;
                        }
                        //Adds formatted transaction details to list of transactions and adds transaction id to the same position in another list
                        transactionsList.add(document.getString("name") + " (" + category +
                                ")\n " + amountDisplay + "    (New Balance: " + newBalanceDisplay + ")\n" +
                                strDate);
                        transactionsIDList.add(id);
                    }
                    //Sets UI list view to show full list of transactions
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ViewTransactions.this, android.R.layout.simple_list_item_1, transactionsList);
                    transactionsListView.setAdapter(arrayAdapter);
                }
            }
        });

        //When transaction is clicked, open page to edit details of that transaction
        transactionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewTransactions.this, EditTransaction.class);
                intent.putExtra("email", email);
                intent.putExtra("id",  transactionsIDList.get(position));
                startActivity(intent);
            }
        });

        //Takes the user back to Account page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTransactions.this, AccountPage.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
}