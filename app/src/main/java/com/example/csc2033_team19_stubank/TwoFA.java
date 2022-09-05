package com.example.csc2033_team19_stubank;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;

import java.util.HashMap;
import java.util.Map;

/* Author - Karolis Zilius
        This class is for the 2FA page. Once 2FA is set up properly,
        the user's new account is created and added to the Firestore database*/

public class TwoFA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twofa);

        ImageView QRCode = findViewById(R.id.QRImage);
        TextView secretKeyText = findViewById(R.id.QRCode);
        ImageButton copyToClipboardButton = findViewById(R.id.copyToClipboard);
        final EditText authenticationCodeView = findViewById(R.id.enterAuthenticationCode);
        Button confirmAuthenticationButton = findViewById(R.id.confirmAuthenticationCode);

        // Get the account email from the intent
        final String email = getIntent().getStringExtra("email");

        // Generate a secret authentication key for the user
        final String key = TwoFactorAuthentication.generateSecretKey();

        // Generate data for the barcode
        String barCodeData = TwoFactorAuthentication.getGoogleAuthenticatorBarCode(key, email, "StuBank");

        // Create QR code image in the specified path
        try {

            // Print the QR code in the page
            Bitmap bitmap = TwoFactorAuthentication.createQRCode(barCodeData, 600, 600);
            QRCode.setImageBitmap(bitmap);
            // Print secret key on the page
            secretKeyText.setText(key);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        copyToClipboardButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Copy the secret key to the user's clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("SecretKey", key);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(TwoFA.this, "Key copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });

        confirmAuthenticationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String authCode = authenticationCodeView.getText().toString();

                /* Checks if the entered authentication code matches the Google Authenticator
                   generated code */
                if (authCode.equals(TwoFactorAuthentication.getTOTPCode(key))){

                    // Initialises Firebase Firestore
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Intent intent = new Intent(getBaseContext(), Home.class);

                    // Generating new account, card and savings pot details for the user
                    Account account = new Account(GeneratingCardDetails.generateAccountNumber(), "11-22-33",
                            0.0, 1000.0, FieldValue.serverTimestamp(), false);
                    Card card = new Card(GeneratingCardDetails.generateCardNumber(),
                            GeneratingCardDetails.generateCVV(), SignUp.generateExpiryDate(), false);
                    Pot pot = new Pot("Savings", 0.0, 0.0);
                    Prediction prediction = new Prediction(0.0,0.0);

                    // Adding each field to the Students, AccountNumbers, CardNumbers and Predictions collections
                    final Map<String, Object> docData = new HashMap<>();
                    docData.put("firstName", getIntent().getStringExtra("firstName"));
                    docData.put("lastName", getIntent().getStringExtra("lastName"));
                    docData.put("accountNumber", account.getAccountNumber());
                    docData.put("sortCode",  account.getSortCode());
                    docData.put("creationDate", account.getCreationDate());
                    docData.put("currentDate", account.getCreationDate());
                    docData.put("cardNumber", card.getCardNumber());
                    docData.put("cvv",  card.getCvv());
                    docData.put("expiryDate",  card.getExpiryDate());
                    docData.put("balance", account.getBalance());
                    docData.put("overdraft", account.getOverdraft());
                    docData.put("frozen",  card.isFrozen());
                    docData.put("roundUp", account.isRoundUp());
                    docData.put("allBudget", 0.0);
                    docData.put("groceriesBudget", 0.0);
                    docData.put("shoppingBudget", 0.0);
                    docData.put("billsBudget", 0.0);
                    docData.put("entertainmentBudget", 0.0);
                    docData.put("eatingOutBudget", 0.0);
                    docData.put("universityBudget", 0.0);
                    docData.put("transportBudget", 0.0);
                    docData.put("otherBudget", 0.0);
                    docData.put("password", getIntent().getStringExtra("password"));
                    docData.put("salt", getIntent().getStringExtra("salt"));
                    docData.put("secretKey", key);

                    final Map<String, Object> accNumData = new HashMap<>();
                    accNumData.put("email", email);

                    final Map<String, Object> cardNumData = new HashMap<>();
                    cardNumData.put("email", email);
                    
                    /* References to the user's details, to be stored in their respective collections
                       in Firebase */
                    final DocumentReference student = db.collection("Students").document(email);
                    final DocumentReference accNos = db.collection("AccountNumbers").document(account.getAccountNumber());
                    final DocumentReference cardNos = db.collection("CardNumbers").document(card.getCardNumber());

                    // Adds the student's data, savings pot and predictions to the database
                    student.set(docData);
                    student.collection("Pots").document("Savings").set(pot);
                    student.collection("Predictions").document("Groceries").set(prediction);
                    student.collection("Predictions").document("Shopping").set(prediction);
                    student.collection("Predictions").document("Bills").set(prediction);
                    student.collection("Predictions").document("Entertainment").set(prediction);
                    student.collection("Predictions").document("Eating Out").set(prediction);
                    student.collection("Predictions").document("University").set(prediction);
                    student.collection("Predictions").document("Transport").set(prediction);

                    /* Ensures the user has a unique account number by checking if the randomly
                       generated number already exists. If it exists, a new account number is generated
                       and used for the user to avoid two accounts having the same account number. */
                    accNos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                // Snapshot of the currently stored data in Firebase
                                DocumentSnapshot document = task.getResult();
                                assert document != null;
                                if (document.exists()) {
                                    /* Generates a new account number and updates the user's
                                       details with the new number */
                                    Account account1 = new Account();
                                    account1.setAccountNumber(GeneratingCardDetails.generateAccountNumber());
                                    student.update("accountNumber", account1.getAccountNumber());
                                    // Creates a new document in the AccountNumbers collection
                                    db.collection("AccountNumbers").
                                            document(account1.getAccountNumber()).set(accNumData);
                                }
                                else
                                    accNos.set(accNumData);
                            }
                        }
                    });

                    /* Ensures the user has a unique card number by checking if the randomly
                       generated number already exists in the database, and generating a new
                       one if it does exist. */
                    cardNos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                // Snapshot of the currently stored data
                                DocumentSnapshot document = task.getResult();
                                assert document != null;
                                if (document.exists()) {
                                    /* Generates a new card number and updates the user's
                                       details with the new number */
                                    Card card1 = new Card();
                                    card1.setCardNumber(GeneratingCardDetails.generateCardNumber());
                                    student.update("cardNumber", card1.getCardNumber());
                                    // Creates a new document in the CardNumbers collection
                                    db.collection("CardNumbers").
                                            document(card1.getCardNumber()).set(cardNumData);
                                }
                                else
                                    cardNos.set(cardNumData);
                            }
                        }
                    });

                    // Sends the user's email to the home page to log them in
                    intent.putExtra("email", email);
                    startActivity(intent);
                    // Finishes the current context to prevent users from being able to sign up again
                    finish();

                }
                else{
                    // Error message displays if the wrong 2FA code has been entered
                    Toast.makeText(TwoFA.this, "Enter the correct 2FA code", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}