package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
/* Author - Nathan Fenwick and Karolis Zilius
   This class is for the sign in page of the app. */
public class SignIn extends AppCompatActivity {

    //User input variables declaration.
    private EditText EMail, PWord, PAuth;
    private String EMailS, PWordS, PAuthS;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //User input variables initialisation.
        EMail = findViewById(R.id.EMail);
        PWord = findViewById(R.id.PWord);
        PAuth = findViewById(R.id.AuthenticationCode);
        TextView forgottenPass = findViewById(R.id.txtForgottenPass);
        //Logic for login button press.
        Button signInBtn = findViewById(R.id.SignInBtn);
        ImageButton backBtn = findViewById(R.id.signInBackBtn);
        signInBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Getting user input.
                EMailS = EMail.getText().toString();
                PWordS = PWord.getText().toString();
                PAuthS = PAuth.getText().toString();
                // Check to see if
                if(!UserInputValidation.SignInValidation(EMailS, PWordS).isEmpty()){
                    Toast.makeText(SignIn.this, UserInputValidation.SignInValidation(EMailS, PWordS), Toast.LENGTH_LONG).show();
                }
                // Check to see if the entered details match log in credentials.

                else{

                    checkCredentials(EMailS,PWordS,PAuthS);

                }
            }
        });

        // Sends the user to the reset password page
        forgottenPass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, ResetPassword.class));
            }
        });

        // Back button - goes back to the main app page
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, MainActivity.class));
            }
        });
    }

    void checkCredentials(final String EMailS, final String PWordS, final String PAuthS){

        // Checks to see if data given matches document in fire store.
        db.collection("Students").document(EMailS).get().addOnCompleteListener
                (new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()) {
                                if (HashPassword.checkHashedPassword(PWordS, doc)) {
                                    if (PAuthS.equals(TwoFactorAuthentication.getTOTPCode(doc.getString("secretKey")))) {

                                        Intent intent = new Intent(SignIn.this, Home.class);
                                        intent.putExtra("email", EMailS);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(SignIn.this, "Authentication code incorrect.", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else {
                                    Toast.makeText(SignIn.this, "Please enter a valid set of credentials!", Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(SignIn.this, "Please enter a valid set of credentials!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(SignIn.this, "Failed to connect to database. Please try again.", Toast.LENGTH_LONG).show();
                            Log.w("", "Error getting document", task.getException());

                        }
                    }
                });
    }

}