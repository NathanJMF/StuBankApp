package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.*;
import java.net.*;
import java.lang.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/*
Author Nathan Fenwick and Megan Jinks
Displays predictions from database and connects to server
 */
public class PredictFutureSpending extends AppCompatActivity {
    private ImageButton backButton;
    private TextView predictAmountBillsText, predictAmountEatingOutText, predictAmountEntertainmentText,
    predictAmountGroceriesText, predictAmountShoppingText, predictAmountTransportText, predictAmountUniversityText,
    predictTimeBillsText, predictTimeEatingOutText, predictTimeEntertainmentText, predictTimeGroceriesText,
    predictTimeShoppingText, predictTimeTransportText, predictTimeUniversityText;
    private Double billsTime, billsAmount, eatingOutAmount, eatingOutTime, entertainmentAmount,
    entertainmentTime, groceriesAmount, groceriesTime, shoppingAmount, shoppingTime, transportAmount,
    transportTime, universityAmount, universityTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.predict_future_spending);

        final String email = getIntent().getStringExtra("email");
        new ConnAsyncTask().execute(email);

        // Initialises the back button
        backButton = findViewById(R.id.backButtonFutureSpending);
        predictAmountBillsText = findViewById(R.id.predictedAmountBills);
        predictAmountEatingOutText = findViewById(R.id.predictedAmountEatingOut);
        predictAmountEntertainmentText = findViewById(R.id.predictedAmountEntertainment);
        predictAmountGroceriesText = findViewById(R.id.predictedAmountGroceries);
        predictAmountShoppingText = findViewById(R.id.predictedAmountShopping);
        predictAmountTransportText = findViewById(R.id.predictedAmountTranpsort);
        predictAmountUniversityText = findViewById(R.id.predictedAmountUniversity);
        predictTimeBillsText = findViewById(R.id.predictedTimeBills);
        predictTimeEatingOutText = findViewById(R.id.predictedTimeEatingOut);
        predictTimeEntertainmentText = findViewById(R.id.predictedTimeEntertainment);
        predictTimeGroceriesText = findViewById(R.id.predictedTimeGroceries);
        predictTimeShoppingText = findViewById(R.id.predictedTimeShopping);
        predictTimeTransportText = findViewById(R.id.predictedTimeTransport);
        predictTimeUniversityText = findViewById(R.id.predictedTimeUniversity);


        // Initialises an instance of the Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRefBills = db.collection("Students").document(email).
                collection("Predictions").document("Bills");
        DocumentReference docRefEatingOut = db.collection("Students").document(email).
                collection("Predictions").document("Eating Out");
        DocumentReference docRefEntertainment = db.collection("Students").document(email).
                collection("Predictions").document("Entertainment");
        DocumentReference docRefGroceries = db.collection("Students").document(email).
                collection("Predictions").document("Groceries");
        DocumentReference docRefShopping = db.collection("Students").document(email).
                collection("Predictions").document("Shopping");
        DocumentReference docRefTransport = db.collection("Students").document(email).
                collection("Predictions").document("Transport");
        DocumentReference docRefUniversity = db.collection("Students").document(email).
                collection("Predictions").document("University");

        //Get from bills document
        docRefBills.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Current snapshot of the data stored for the user
                DocumentSnapshot doc = task.getResult();
                // Gets the user's amount predicted and time diff
                billsAmount = doc.getDouble("predictedAmount");
                billsTime = doc.getDouble("timeDiff");
                //check if prediction is zero for amount and time
                if(checkIfPredictZero(billsAmount)&&checkIfPredictZero(billsTime)){
                    predictAmountBillsText.setText("Not enough data");
                    predictTimeBillsText.setText("");
                }else{
                    predictAmountBillsText.setText("£" + billsAmount);
                    predictTimeBillsText.setText((int)Math.round(billsTime) + " days");
                }
            }
        });
        //Get from eating out document
        docRefEatingOut.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Current snapshot of the data stored for the user
                DocumentSnapshot doc = task.getResult();
                // Gets the user's amount predicted and time diff
                eatingOutAmount = doc.getDouble("predictedAmount");
                eatingOutTime = doc.getDouble("timeDiff");
                //check if prediction is zero for amount and time
                if(checkIfPredictZero(eatingOutAmount)&&checkIfPredictZero(eatingOutTime)){
                    predictAmountEatingOutText.setText("Not enough data");
                    predictTimeEatingOutText.setText("");
                }else{
                    predictAmountEatingOutText.setText("£" + eatingOutAmount);
                    predictTimeEatingOutText.setText((int)Math.round(eatingOutTime) + " days");
                }
            }
        });
        //Get from entertainment document
        docRefEntertainment.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Current snapshot of the data stored for the user
                DocumentSnapshot doc = task.getResult();
                // Gets the user's amount predicted
                entertainmentAmount = doc.getDouble("predictedAmount");
                //Gets the user's timeDiff
                entertainmentTime = doc.getDouble("timeDiff");
                //check if prediction is zero for amount and time
                if(checkIfPredictZero(entertainmentAmount)&&checkIfPredictZero(entertainmentTime)){
                    predictAmountEntertainmentText.setText("Not enough data");
                    predictTimeEntertainmentText.setText("");
                }else{
                    predictAmountEntertainmentText.setText("£" + entertainmentAmount);
                    predictTimeEntertainmentText.setText((int)Math.round(entertainmentTime) + " days");
                }

            }
        });
        //Get from groceries document
        docRefGroceries.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Current snapshot of the data stored for the user
                DocumentSnapshot doc = task.getResult();
                // Gets the user's amount predicted and time diff
                groceriesAmount = doc.getDouble("predictedAmount");
                groceriesTime = doc.getDouble("timeDiff");
                //check if prediction is zero for amount and time
                if(checkIfPredictZero(groceriesAmount)&&checkIfPredictZero(groceriesTime)){
                    predictAmountGroceriesText.setText("Not enough data");
                    predictTimeGroceriesText.setText("");
                }else{
                    predictAmountGroceriesText.setText("£" + groceriesAmount);
                    predictTimeGroceriesText.setText((int)Math.round(groceriesTime) + " days");
                }
            }
        });
        //Get from shopping document
        docRefShopping.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Current snapshot of the data stored for the user
                DocumentSnapshot doc = task.getResult();
                // Gets the user's amount predicted and time diff
                shoppingAmount = doc.getDouble("predictedAmount");
                shoppingTime = doc.getDouble("timeDiff");
                //check if prediction is zero for amount and time
                if(checkIfPredictZero(shoppingAmount)&&checkIfPredictZero(shoppingTime)){
                    predictAmountShoppingText.setText("Not enough data");
                    predictTimeShoppingText.setText("");
                }else{
                    predictAmountShoppingText.setText("£" + shoppingAmount);
                    predictTimeShoppingText.setText((int)Math.round(shoppingTime) + " days");
                }
            }
        });
        //Get from transport document
        docRefTransport.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Current snapshot of the data stored for the user
                DocumentSnapshot doc = task.getResult();
                // Gets the user's amount predicted and timeDiff
                transportAmount = doc.getDouble("predictedAmount");
                transportTime = doc.getDouble("timeDiff");
                //check if prediction is zero for amount and time
                if(checkIfPredictZero(transportAmount)&&checkIfPredictZero(transportTime)){
                    predictAmountTransportText.setText("Not enough data");
                    predictTimeTransportText.setText("");
                }else{
                    predictAmountTransportText.setText("£" + transportAmount);
                    predictTimeTransportText.setText((int)Math.round(transportTime) + " days");
                }
            }
        });
        //Get from University document
        docRefUniversity.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Current snapshot of the data stored for the user
                DocumentSnapshot doc = task.getResult();
                // Gets the user's amount predicted and time diff
                universityAmount = doc.getDouble("predictedAmount");
                universityTime = doc.getDouble("timeDiff");
                //check if prediction is zero for amount and time
                if(checkIfPredictZero(universityAmount)&&checkIfPredictZero(universityTime)){
                    predictAmountUniversityText.setText("Not enough data");
                    predictTimeUniversityText.setText("");
                }else{
                    predictAmountUniversityText.setText("£" + universityAmount);
                    predictTimeUniversityText.setText((int)Math.round(universityTime) + " days");
                }

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PredictFutureSpending.this, AccountPage.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
    //Checking is input from database is zero
    public static boolean checkIfPredictZero(Double input){
        if(input == 0){
            return true;
        }else{
            return false;
        }
    }

    // Client code for interacting with python server
    class ConnAsyncTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String...userEmail){
            try{
                // Please change ip address before running
                Socket socket=new Socket("192.168.55.10",2004);
                DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
                DataInputStream din=new DataInputStream(socket.getInputStream());
                // Sends userEmail as a message to the python server
                dout.writeUTF(userEmail[0]);
                dout.flush();
                // After server has finished running predictions message is sent back to let the
                // client know it has finished
                String str = din.readUTF();
                dout.close();
                din.close();
                socket.close();
            }

            catch(Exception e){
                // If a connection cant be established then carry on and print error in run terminal
                System.out.println("ERROR!");
            }
            return "";
        }
    }

}

