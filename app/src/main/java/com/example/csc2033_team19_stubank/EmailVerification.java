package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/* Author - Will Holmes
   This class is for verifying the email entered on the Sign Up page. Input validation on the Sign
    Up page makes sure the email is a university email address (.ac.uk) in a valid format. This page
    makes sure that the email is an existing email address that the new user can access by sending a
    verification email containing a link to the email inbox which must be clicked for account creation to continue*/

public class EmailVerification extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // Sets variables
        Button resendEmailButton = findViewById(R.id.resendEmail);
        Button confirmVerificationButton = findViewById(R.id.confirmVerificaton);

        final String email = getIntent().getStringExtra("email");
        final String firstName = getIntent().getStringExtra("firstName");
        final String lastName = getIntent().getStringExtra("lastName");
        final String password = getIntent().getStringExtra("password");
        final String salt = getIntent().getStringExtra("salt");
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        //Attempts to send user a verification email to the provided email address
        sendVerification(email);

        //Resends verification email on user request
        resendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerification(email);
            }
        });


        //Checks whether email is verified to continue sign up process when button is pressed
        confirmVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();
                user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        FirebaseUser user = auth.getCurrentUser();
                        //If email is verified then continue
                        if (user.isEmailVerified()) {
                            Intent intent = new Intent(getBaseContext(), TwoFA.class);
                            intent.putExtra("email", email);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("lastName", lastName);
                            intent.putExtra("password", password);
                            intent.putExtra("salt", salt);

                            startActivity(intent);
                        //If email is not verified, prompt user to verify their email first
                        } else {
                            Toast.makeText(EmailVerification.this, "Please verify your email address", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }

    public void sendVerification(final String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //Signs out of any authorisation account in case already logged in
        auth.signOut();
        //Attempts to log in to account registered with entered email in case a verification attempt has been made before
        auth.signInWithEmailAndPassword(email, "defaultpassword")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Deletes account if one exists
                            deleteAccount(email);
                        } else {
                            //Creates account for sending verification email
                            createAccount(email);
                        }
                    }
                });
    }

    //Sends verification email to given email address
    public void sendEmail(final String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Displays success message if email sent successfully
                            Toast.makeText(EmailVerification.this, ("Verification email sent to "+email), Toast.LENGTH_LONG).show();
                        } else {
                            //Displays error message if not successful
                            Toast.makeText(EmailVerification.this, "Error sending verification email. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Creates account for sending verification email
    public void createAccount(final String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //Creates account for sending verification email
        auth.createUserWithEmailAndPassword(email, "defaultpassword")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sends the user a verification email
                            sendEmail(email);
                        } else {
                            //Displays error message if not successful
                            Toast.makeText(EmailVerification.this, "Error sending verification email. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //If authorisation account exists with this email due to a past verification attempt, delete it and create a new one
    public void deleteAccount(final String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Creates account for sending verification email
                            createAccount(email);
                        } else {
                            //Displays error message if not successful
                            Toast.makeText(EmailVerification.this, "Error sending verification email. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

}