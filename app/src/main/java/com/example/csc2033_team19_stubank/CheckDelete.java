package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//Author Megan Jinks
//Checks that user wants to delete pot by user entering password and checking if correct
//Updates account balance and deletes pot from database
public class CheckDelete extends AppCompatActivity {
    private EditText passwordInput;
    private String password;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_delete_page);

        //Display page on % of screen
        DisplayMetrics displayM = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayM);
        int width = displayM.widthPixels;
        int height = displayM.heightPixels;
        getWindow().setLayout((int)(width*0.75), (int)(height*0.75));

        final String email = getIntent().getStringExtra("email");
        final String pot = getIntent().getStringExtra("pot");

        //initialise button
        final Button deleteButton = (Button) findViewById(R.id.btnConfirmDelete);

        // Initialises an instance of the Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference documentReferenceBalance = db.collection("Students")
                .document(email);
        final DocumentReference documentReferencePot = db.collection("Students")
                .document(email).collection("Pots").document(pot);

        // Listens for when the button is clicked and appropriately directs the user to the next page
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    documentReferenceBalance.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                // Current snapshot of the data stored for the user
                                DocumentSnapshot doc = task.getResult();
                                //get user's balance of account
                                final Double balance = doc.getDouble("balance");
                                //getting user inputs
                                passwordInput = (EditText) findViewById(R.id.editTextPasswordInput);
                                password = passwordInput.getText().toString();
                                //Check password is correct
                                if(HashPassword.checkHashedPassword(password, doc)){
                                    documentReferencePot.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc2 = task.getResult();
                                                //getting balance of pot
                                                final Double balancePot = doc2.getDouble("balance");
                                                //updating account balance with money from pot
                                                documentReferenceBalance.update("balance", balance + balancePot);
                                                //deleting pot from database
                                                documentReferencePot.delete();

                                                //moving to pot home page
                                                Intent intent = new Intent(CheckDelete.this, Pots.class);
                                                intent.putExtra("email", email);
                                                intent.putExtra("pot", pot);
                                                startActivity(intent);
                                                // Finishes the current context
                                                finish();
                                            }
                                        }
                                    });
                                }
                                else{
                                    //Display wrong password
                                    resultText = (TextView) findViewById(R.id.txtDeletePotErrorMessage);
                                    resultText.setText("Wrong password! Try again");
                                }
                            }
                        }
                    });
            }
        });

    }

}
