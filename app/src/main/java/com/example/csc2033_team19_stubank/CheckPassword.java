package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

//Author Megan Jinks
//Checks if user input correct password
//Generates and updates new card details from database
public class CheckPassword extends AppCompatActivity {

    private int width, height, newCVV;
    private Button submitButton;
    private EditText passwordCheck;
    private TextView resultText;
    private String newCardNumber;
    private boolean valid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_password_page);

        final String email = getIntent().getStringExtra("email");

        //Show page on % of screen
        DisplayMetrics displayM = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayM);
        width = displayM.widthPixels;
        height = displayM.heightPixels;
        getWindow().setLayout((int)(width*0.75), (int)(height*0.75));

        // Initialises the buttons.
        submitButton = (Button) findViewById(R.id.btnCheckPasswordSubmit);

        // Listens for when the button is clicked and appropriately directs the user to the next page.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialises an instance of the Cloud Firestore
                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("Students").document(email).get().addOnCompleteListener
                        (new OnCompleteListener<DocumentSnapshot>(){

                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    // Current snapshot of the data stored for the user
                                    DocumentSnapshot doc = task.getResult();

                                    //User input variables initialisation.
                                    passwordCheck = (EditText) findViewById(R.id.editTextCheckPassword);
                                    final String passwordCheckText = passwordCheck.getText().toString();

                                    //checking password is correct
                                    if(HashPassword.checkHashedPassword(passwordCheckText, doc)){

                                        //Checking if card number is valid and if not keeps generating until one is valid
                                        valid = false;
                                        while (valid == false){
                                            newCardNumber = GeneratingCardDetails.generateCardNumber();
                                            if(!(GeneratingCardDetails.checkCardValid(newCardNumber))){
                                                newCardNumber = GeneratingCardDetails.generateCardNumber();
                                            }else{
                                                valid = true;
                                            }
                                        }

                                        //generating CVV
                                        newCVV = GeneratingCardDetails.generateCVV();

                                        // Initialises an instance of the Cloud Firestore
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                        //Deletes old card number from card numbers collection
                                        db.collection("CardNumbers").document(doc.getString("cardNumber")).delete();

                                        //Adds new card number to card numbers collection
                                        Map<String, Object> cardNumData = new HashMap<>();
                                        cardNumData.put("email", email);
                                        db.collection("CardNumbers").document(newCardNumber).set(cardNumData);

                                        //updating files cvv and cardNumber with new details
                                        DocumentReference documentR = db.collection("Students").document(email);
                                        documentR.update("cvv", newCVV);
                                        documentR.update("cardNumber", newCardNumber);
                                        //move to CardDetails page
                                        Intent intent = new Intent(CheckPassword.this, CardDetails.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                    }
                                    else{
                                        //Display wrong password
                                        resultText = (TextView) findViewById(R.id.txtCheckPasswordErrorMessage);
                                        resultText.setText("Wrong password! Try again");
                                    }
                                }
                                else {
                                    Log.w("", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });
    }
}
