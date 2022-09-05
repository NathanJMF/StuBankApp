package com.example.csc2033_team19_stubank;

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

//Author Megan Jinks
//checks user input and balance and moves money from pot into another pot or account
public class MovePotMoney extends AppCompatActivity {
    private Button submitButton;
    private ImageButton backButton;
    private EditText passwordText, amount;
    private Double moneyInt, balanceInputPot;
    private TextView alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_from_pot);

        final String email = getIntent().getStringExtra("email");
        final String pot = getIntent().getStringExtra("pot");

        //initialising buttons
        submitButton = (Button) findViewById(R.id.btnTakeFromPot);
        backButton = (ImageButton) findViewById(R.id.takeFromPotBackBtn);

        //move back to pot page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovePotMoney.this, SavingPot.class);
                intent.putExtra("email", email);
                intent.putExtra("pot", pot);
                startActivity(intent);
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialises an instance of the Cloud Firestore
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference docRef = db.collection("Students").document(email);
                final DocumentReference docRefPot = db.collection("Students").document(email)
                        .collection("Pots").document(pot);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            // Current snapshot of the data stored for the user
                            DocumentSnapshot doc = task.getResult();
                            //get account balance
                            final Double accountBalance = doc.getDouble("balance");
                            passwordText = (EditText) findViewById(R.id.takeFromPotPassword);
                            //get user input of password
                            final String password = passwordText.getText().toString();

                            //Check is password is correct
                            if(HashPassword.checkHashedPassword(password, doc)){

                                //get user input of money
                                amount = (EditText) findViewById(R.id.moneyTake);
                                final String money = amount.getText().toString();

                                //get user input of where to send money
                                final EditText potOrAccountText = (EditText) findViewById(R.id.accountOrPot);
                                final String potOrAccount = potOrAccountText.getText().toString();

                                //check fields are full
                                if(!(UserInputValidation.checkTakePotFields(potOrAccount, money ,password))){
                                    //Check money inputted is 2 decimal places
                                    if(UserInputValidation.checkTwoDecimalPlaces(money)){
                                        final DocumentReference docRefUserInput = db.collection("Students").document(email)
                                                .collection("Pots").document(potOrAccount);
                                        docRefUserInput.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    // Current snapshot of the data stored for the user
                                                    final DocumentSnapshot doc2 = task.getResult();

                                                    docRefPot.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                            // Current snapshot of the data stored for the user
                                                            final DocumentSnapshot doc3 = task.getResult();
                                                            //check money isn't empty and change to double
                                                            moneyInt = 0.0;
                                                            if (!money.equals("")) {
                                                                moneyInt = Double.parseDouble(money);
                                                            }

                                                            //Check is document exists
                                                            if(doc2.exists()){
                                                                // Gets the user's pot balance
                                                                final Double balancePot = doc3.getDouble("balance");
                                                                balanceInputPot = doc2.getDouble("balance");
                                                                //Check user has enough money in pot
                                                                if(UserInputValidation.moneyCheck(balancePot, moneyInt)){
                                                                    //update balance of pot and account
                                                                    docRefPot.update("balance", balancePot - moneyInt);
                                                                    docRefUserInput.update("balance", balanceInputPot + moneyInt);
                                                                    //move back to pot page
                                                                    Intent intent = new Intent(MovePotMoney.this, SavingPot.class);
                                                                    intent.putExtra("email", email);
                                                                    intent.putExtra("pot", pot);
                                                                    startActivity(intent);
                                                                }else{
                                                                    alert = (TextView) findViewById(R.id.txtResultTakePot);
                                                                    alert.setText("You do not have enough money in your pot");
                                                                }
                                                            }
                                                            //check if input == account
                                                            else if(potOrAccount.equals("account")){
                                                                //get balance
                                                                final Double balancePot = doc3.getDouble("balance");
                                                                //check user has enough balance in pot
                                                                if(UserInputValidation.moneyCheck(balancePot, moneyInt)){
                                                                    //updates balance of pot and account
                                                                    docRef.update("balance",accountBalance + moneyInt);
                                                                    docRefPot.update("balance", balancePot - moneyInt);
                                                                    //move to pot page
                                                                    Intent intent = new Intent(MovePotMoney.this, SavingPot.class);
                                                                    intent.putExtra("email", email);
                                                                    intent.putExtra("pot", pot);
                                                                    startActivity(intent);
                                                                }else{
                                                                    alert = (TextView) findViewById(R.id.txtResultTakePot);
                                                                    alert.setText("You do not have enough money in your pot");
                                                                }
                                                            }
                                                            else{
                                                                alert = (TextView) findViewById(R.id.txtResultTakePot);
                                                                alert.setText("Please enter a valid pot or write 'account");
                                                            }
                                                        }
                                                    });

                                                }
                                            }
                                        });
                                    }else{
                                        alert = (TextView) findViewById(R.id.txtResultTakePot);
                                        alert.setText("Money needs to have 2 decimal places");
                                    }
                                }
                                else{
                                    alert = (TextView) findViewById(R.id.txtResultTakePot);
                                    alert.setText("You need to fill all fields");
                                }
                            }else{
                                alert = (TextView) findViewById(R.id.txtResultTakePot);
                                alert.setText("Wrong password! Try again");
                            }

                        }
                    }
                });
            }
        });
    }
}
