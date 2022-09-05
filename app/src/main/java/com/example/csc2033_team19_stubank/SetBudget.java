package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/* Author - Will Holmes
   This class is for the setting of new monthly budget goals for each category. All values must be to two decimal
    places and if no new budget is entered the existing budget value is kept*/

public class SetBudget extends AppCompatActivity {

    private ImageButton backButton;
    private Button submitButton;
    private EditText groceriesInput, shoppingInput, billsInput, entertainmentInput, eatingOutInput,
            universityInput, transportInput, otherInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_budget_page);

        final String email = getIntent().getStringExtra("email");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference documentReference = db.collection("Students").document(email);

        //Finds relevant UI elements
        backButton = (ImageButton) findViewById(R.id.setBudgetBackButton);
        submitButton = (Button) findViewById(R.id.setBudgetSubmitButton);
        groceriesInput = (EditText) findViewById(R.id.setBudgetGroceriesEditText);
        shoppingInput = (EditText) findViewById(R.id.setBudgetShoppingEditText);
        billsInput = (EditText) findViewById(R.id.setBudgetBillsEditText);
        entertainmentInput = (EditText) findViewById(R.id.setBudgetEntertainmentEditText);
        eatingOutInput = (EditText) findViewById(R.id.setBudgetEatingOutEditText);
        universityInput = (EditText) findViewById(R.id.setBudgetUniversityEditText);
        transportInput = (EditText) findViewById(R.id.setBudgetTransportEditText);
        otherInput = (EditText) findViewById(R.id.setBudgetOtherEditText);

        // Takes the user back to Budget page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetBudget.this, Budget.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        // When the user submits new budgets, attempt to update them
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String groceries = groceriesInput.getText().toString();
                final String shopping = shoppingInput.getText().toString();
                final String bills = billsInput.getText().toString();
                final String entertainment = entertainmentInput.getText().toString();
                final String eatingOut = eatingOutInput.getText().toString();
                final String university = universityInput.getText().toString();
                final String transport = transportInput.getText().toString();
                final String other = otherInput.getText().toString();

                // Checks that all entered values are either empty or set to 2 deciaml places
                if (UserInputValidation.checkSetBudgets(groceries, shopping, bills, entertainment,
                        eatingOut, university, transport, other)) {

                    final Double groceriesSet = makeNumber(groceries);
                    final Double shoppingSet = makeNumber(shopping);
                    final Double billsSet = makeNumber(bills);
                    final Double entertainmentSet = makeNumber(entertainment);
                    final Double eatingOutSet = makeNumber(eatingOut);
                    final Double universitySet = makeNumber(university);
                    final Double transportSet = makeNumber(transport);
                    final Double otherSet = makeNumber(other);

                    documentReference.get().addOnCompleteListener
                            (new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // If new budget values are not empty, updates budget values with new values
                                        if (groceries.length() > 0) {
                                            documentReference.update("groceriesBudget", groceriesSet);
                                        }
                                        if (shopping.length() > 0) {
                                            documentReference.update("shoppingBudget", shoppingSet);
                                        }
                                        if (bills.length() > 0) {
                                        documentReference.update("billsBudget", billsSet);
                                        }
                                        if (entertainment.length() > 0) {
                                        documentReference.update("entertainmentBudget", entertainmentSet);
                                        }
                                        if (eatingOut.length() > 0) {
                                        documentReference.update("eatingOutBudget", eatingOutSet);
                                        }
                                        if (university.length() > 0) {
                                        documentReference.update("universityBudget", universitySet);
                                        }
                                        if (transport.length() > 0) {
                                        documentReference.update("transportBudget", transportSet);
                                        }
                                        if (other.length() > 0) {
                                        documentReference.update("otherBudget", otherSet);
                                        }
                                        //Generate new total budget (the total of all categories combined)
                                        generateNewBudgetTotal(email);

                                        // Take the user back to the Budget page
                                        Intent intent = new Intent(SetBudget.this, Budget.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                    //Show relevant messages if update doesn't go through (input validation or connection issues)
                                    } else {
                                        Toast.makeText(SetBudget.this, "Unable to connect to database! Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SetBudget.this, "Please set new budgets to 2 decimal places (x.xx).", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Takes string and turns it into a double
    public static Double makeNumber(String amount){
        Double amountDouble;
        if ((amount.length() == 0)) {
            amountDouble = (Double) 0.0;
        } else {
            amountDouble = Double.parseDouble(amount);
        }
        return amountDouble;
    }

    //Adds up all new budgets to create new budget total for all categories combined
    public static void generateNewBudgetTotal(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference documentReference = db.collection("Students").document(email);
        documentReference.get().addOnCompleteListener
                (new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Double groceriesBudget = doc.getDouble("groceriesBudget");
                            Double shoppingBudget = doc.getDouble("shoppingBudget");
                            Double billsBudget = doc.getDouble("billsBudget");
                            Double entertainmentBudget = doc.getDouble("entertainmentBudget");
                            Double eatingOutBudget = doc.getDouble("eatingOutBudget");
                            Double universityBudget = doc.getDouble("universityBudget");
                            Double transportBudget = doc.getDouble("transportBudget");
                            Double otherBudget = doc.getDouble("otherBudget");
                            documentReference.update("allBudget", groceriesBudget + shoppingBudget +
                                    billsBudget + entertainmentBudget + eatingOutBudget + universityBudget + transportBudget + otherBudget);
                        }
                    }
                });
    }

}