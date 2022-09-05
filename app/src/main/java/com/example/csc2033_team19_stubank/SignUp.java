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

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/* Author - Will Holmes
   This class is for the sign up page of the app. */
public class SignUp extends AppCompatActivity {
    //Variable declaration
    private EditText inputFirstName, inputLastName, inputEmail, inputPassword, inputRepeatPass;
    private String firstName, lastName, email, password, repeatPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inputFirstName = findViewById(R.id.editTextSignUpFirstName);
        inputLastName = findViewById(R.id.editTextSignUpSurname);
        inputEmail = findViewById(R.id.editTextSignUpEmailAddress);
        inputPassword = findViewById(R.id.editTextSignUpPassword);
        inputRepeatPass = findViewById(R.id.editTextSignUpRepeatPassword);

        // Buttons on the page
        Button signUpButton = findViewById(R.id.btnSignUp);
        ImageButton backButton = findViewById(R.id.signUpBackBtn);

        // When Sign Up button is pressed
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user inputs
                firstName = inputFirstName.getText().toString();
                lastName = inputLastName.getText().toString();
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                repeatPass = inputRepeatPass.getText().toString();
                // Input validation
                String validationResult = UserInputValidation.SignUpValidation(firstName, lastName, email, password, repeatPass);
                if (!(validationResult).isEmpty()) {
                    // Shows appropriate input validation message if anything is invalid
                    Toast.makeText(SignUp.this, validationResult, Toast.LENGTH_LONG).show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference docRef = db.collection("Students").document(email);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                //Checks if email is already registered to an account and moves to next stage of account creation if it is not
                                DocumentSnapshot doc = task.getResult();
                                if (!doc.exists()) {
                                    createAccount(firstName, lastName, email, password);
                                } else {
                                    Toast.makeText(SignUp.this, "This email is already registered to an account!", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(SignUp.this, "Cannot connect to database. Check your connection.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        // Back button - goes back to the main app page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, MainActivity.class));
            }
        });
    }

    //Sends new user info to next step of account creation process.
    public void createAccount(String firstName, String lastName, String email, String password){
        try {
            String [] hashSalt = HashPassword.hashPassword(password);

            // Pass all the data to 2FA activity to create an account after the 2FA has been set up
            Intent intent = new Intent(getBaseContext(), EmailVerification.class);
            intent.putExtra("email", email);
            intent.putExtra("firstName", nameCapital(firstName));
            intent.putExtra("lastName", nameCapital(lastName));
            intent.putExtra("password", hashSalt[0]);
            intent.putExtra("salt", hashSalt[1]);

            startActivity(intent);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //Generates expiry date 4 years after sign up.
    public static String generateExpiryDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        LocalDateTime date = LocalDateTime.now();
        date = date.plusYears(4);
        return formatter.format(date);
    }

    //Makes first letter of name a capital letter.
    public static String nameCapital(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
