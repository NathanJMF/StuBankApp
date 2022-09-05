package com.example.csc2033_team19_stubank;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
/*
Author Megan Jinks
User inputs new goal for pot and is updated in database
 */
public class EditGoal extends AppCompatActivity {
    private int width, height;
    private Button submitButton;
    private ImageButton backButton;
    private EditText newGoal;
    private TextView resultText;
    private Double doubleGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_goal_page);

        final String email = getIntent().getStringExtra("email");
        final String pot = getIntent().getStringExtra("pot");

        //getting page to show % of screen
        DisplayMetrics displayM = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayM);
        width = displayM.widthPixels;
        height = displayM.heightPixels;
        getWindow().setLayout((int)(width*0.75), (int)(height*0.75));

        // Initialises the buttons.
        submitButton = (Button) findViewById(R.id.btnChangeGoal);
        backButton = (ImageButton) findViewById(R.id.newGoalBackBtn);

        // Listens for when either of the buttons are clicked and appropriately directs the user to the next page.
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditGoal.this, SavingPot.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialises an instance of the Cloud Firestore
                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("Students").document(email).get().addOnCompleteListener
                        (new OnCompleteListener<DocumentSnapshot>() {

                             @SuppressLint("SetTextI18n")
                             @Override
                             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                 if (task.isSuccessful()) {
                                     // Current snapshot of the data stored for the user
                                     DocumentSnapshot doc = task.getResult();

                                     //User input variables initialisation.
                                     newGoal = (EditText) findViewById(R.id.editTextNewGoal);
                                     final String goal = newGoal.getText().toString();
                                     //if goal empty tells user to enter goal
                                     if(goal.equals("")){
                                         resultText = (TextView) findViewById(R.id.txtNewGoalErrorMessage);
                                         resultText.setText("You need to enter a new goal");
                                     }
                                     else{
                                         //check goal entered has 2 decimal places
                                         if(UserInputValidation.checkTwoDecimalPlaces(goal)){
                                             //change from string to double
                                             doubleGoal = Double.parseDouble(goal);

                                             DocumentReference documentR = db.collection("Students").document(email).collection("Pots").document(pot);
                                             //update goal in database
                                             documentR.update("goal", doubleGoal);
                                             //move back to saving pot
                                             Intent intent = new Intent(EditGoal.this, SavingPot.class);
                                             intent.putExtra("email", email);
                                             intent.putExtra("pot", pot);
                                             startActivity(intent);
                                         }else{
                                             resultText = (TextView) findViewById(R.id.txtNewGoalErrorMessage);
                                             resultText.setText("Goal needs to have 2 decimal places");
                                         }

                                     }
                                 }
                             }
                         });
            }
        });
    }
}
