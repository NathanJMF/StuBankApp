package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/* Author - Will Holmes
   This class is for editing the name, category and budget inclusion status of past transactions. */
public class EditTransaction extends AppCompatActivity {

    private ImageButton backButton;
    private Button submitButton;
    private EditText name;
    private Spinner category, includeBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_transaction);

        // Gets email of user and id of transaction
        final String email = getIntent().getStringExtra("email");
        final String transactionID = getIntent().getStringExtra("id");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference documentReference = db.collection("Students")
                .document(email).collection("Transactions").document(transactionID);

        //Defines relevant UI elements for the page
        backButton = (ImageButton) findViewById(R.id.editTransactionBackButton);
        submitButton = (Button) findViewById(R.id.editTransactionButton);
        name = (EditText) findViewById(R.id.editTransactionNameInput);
        category = findViewById(R.id.editTransactionCategoryInput);
        includeBudget = findViewById(R.id.editTransactionIncludeInput);

        //When user clicks button to submit changes
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user inputs
                final String setName = name.getText().toString();
                final String setCategory = category.getSelectedItem().toString();
                String setIncludeStr = includeBudget.getSelectedItem().toString();
                final boolean setInclude;
                if (setIncludeStr.equals("Yes")){
                    setInclude = true;
                } else {
                    setInclude = false;
                }
                //Input validation for transaction name length
                if (setName.length() < 51 ) {
                    if (setName.length() > 2) {
                        documentReference.get().addOnCompleteListener(
                                (new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        //Updates transaction info if successful
                                        if (task.isSuccessful()) {
                                            documentReference.update("name", setName);
                                            documentReference.update("category", setCategory);
                                            documentReference.update("includeBudget", setInclude);

                                            Intent intent = new Intent(EditTransaction.this, ViewTransactions.class);
                                            intent.putExtra("email", email);
                                            startActivity(intent);
                                        }
                                    }
                                }));
                        //Shows relevant messages to user if new transaction name is too long or short
                    } else {
                        Toast.makeText(EditTransaction.this, "Name too short! Names must be at least 3 characters.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(EditTransaction.this, "Name too long! Names must be no longer than 50 characters.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Takes the user back to View Transactions page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTransaction.this, ViewTransactions.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
}