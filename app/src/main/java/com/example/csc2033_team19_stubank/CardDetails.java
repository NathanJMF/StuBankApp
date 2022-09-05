package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

//Author Megan Jinks
//The class gets users card details from database and displays it on the page
//Buttons move the user back to Account_Page and to Check_Password to get new details
public class CardDetails extends AppCompatActivity {

    private String sortcode, cardNumberGet, formattedCardNumber, formattedSecurity, expiry, accountNumber, firstName, lastName, fullName;
    private TextView sortCode, sortCodeCard, cardNumberCard, CVV, CVVCard, expiryDate, accountNumberView, accountNumberCard, name;
    private Double securityNo;
    private Button changeDetailsButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_details_page);

        final String email = getIntent().getStringExtra("email");

        // Initialises an instance of the Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Students").document(email).get().addOnCompleteListener
                (new OnCompleteListener<DocumentSnapshot>(){

                    //@SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            // Current snapshot of the data stored for the user
                            DocumentSnapshot doc = task.getResult();
                            // Gets the user's sort code
                            sortcode = doc.getString("sortCode");
                            // Displays the sortcode
                            sortCode = (TextView) findViewById(R.id.txtSortCode);
                            sortCode.setText("Sort code: " + sortcode);
                            // Displays the sortcode on card
                            sortCodeCard = (TextView) findViewById(R.id.sortCode);
                            sortCodeCard.setText(sortcode);

                            // Gets the user's card number and formats it correctly
                            cardNumberGet = doc.getString("cardNumber");
                            formattedCardNumber = formatCardNumber(cardNumberGet);

                            // Displays the users card number on card
                            cardNumberCard = (TextView) findViewById(R.id.cardNo);
                            cardNumberCard.setText(formattedCardNumber);

                            // Gets the user's cvv
                            securityNo = doc.getDouble("cvv");
                            //Formats the user's cvv to 2 decimal places
                            formattedSecurity = String.format("%.0f", securityNo);
                            // Displays the users cvv
                            CVV = (TextView) findViewById(R.id.txtSecurityNo);
                            CVV.setText("Security number: " + formattedSecurity);
                            // Displays the users cvv on card
                            CVVCard = (TextView) findViewById(R.id.securityNo);
                            CVVCard.setText(formattedSecurity);

                            // Gets the user's expiry date
                            expiry = doc.getString("expiryDate");
                            // Displays the users expiry date card
                            expiryDate = (TextView) findViewById(R.id.expiryDate);
                            expiryDate.setText("Expiry date: " + expiry);

                            // Gets the user's account number
                            accountNumber = doc.getString("accountNumber");
                            // Displays the users account number
                            accountNumberView = (TextView) findViewById(R.id.txtAccNo);
                            accountNumberView.setText("Account number: " + accountNumber);
                            // Displays the users account number card
                            accountNumberCard = (TextView) findViewById(R.id.accountNo);
                            accountNumberCard.setText(accountNumber);

                            //gets the user's first name and last name
                            firstName = doc.getString("firstName");
                            lastName = doc.getString("lastName");
                            fullName = (firstName + " " + lastName);
                            // Displays the users full name
                            name = (TextView) findViewById(R.id.userName);
                            name.setText(fullName);

                        }
                        else {
                            Log.w("", "Error getting documents.", task.getException());
                        }
                    }
                });


        //Initialising button and making action to move to check_Password class when pressed
        changeDetailsButton = findViewById(R.id.btnGetNewCardDetails);
        changeDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CardDetails.this, CheckPassword.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        //Initalising button and making action to move to account_Page when pressed
        backButton = findViewById(R.id.cardDetailsBackBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CardDetails.this, AccountPage.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

    }
    //function to put card number into correct format
    public static String formatCardNumber(String card){
        //making empty list
        List<String> splitCardNumber = new ArrayList<String>();
        //adding every 4 numbers to the list
        int i = 0;
        while(i < card.length()){
            splitCardNumber.add(card.substring(i, Math.min(i + 4, card.length())));
            i += 4;
        }
        //adding space after every 4 numbers
        int j = 0;
        String formattedCardNumber = "";
        for(j=0; j < splitCardNumber.size(); j++){
            formattedCardNumber += splitCardNumber.get(j) + " ";
        }

        return formattedCardNumber;
    }
}
