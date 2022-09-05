package com.example.csc2033_team19_stubank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;

/* Author - Megan O'Doherty
   This class is the app's reset password page, which is used if the user has forgotten their
   password.
   As additional security, the user must enter their verification code from Google Authenticator
   before being able to access the fields to reset their password. Once their password has been
   reset, users can now log in with their new password. */
public class ResetPassword extends AppCompatActivity {

    // Variable initialisation
    private EditText email, authCode, newPass, rePass;
    private String emailStr, authCodeStr, newPassStr, rePassStr;
    private TextView txtNewPass, txtRePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        // Initialises instance of Firebase Firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialising the values for text fields
        email = findViewById(R.id.editTextRPEmail);
        authCode = findViewById(R.id.editTextRP2FA);
        newPass = findViewById(R.id.editTextRPNewPassword);
        rePass = findViewById(R.id.editTextRPReEnterPass);
        txtNewPass = findViewById(R.id.txtRPNewPassword);
        txtRePass = findViewById(R.id.txtRPReEnterPassword);
        final Button resetPassword = findViewById(R.id.btnRPResetPass);

        // Back button which takes the user back to the sign in page
        ImageButton back = findViewById(R.id.resetPasswordBackBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPassword.this, SignIn.class));
            }
        });

        // Submit button
        final Button submit = findViewById(R.id.btnRPSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the user's inputted email and authorisation code
                emailStr = email.getText().toString();
                authCodeStr = authCode.getText().toString();

                if (!UserInputValidation.rpCheck(emailStr, authCodeStr).isEmpty())
                {
                    // Displays an error message to alert the user they have entered invalid data
                    Toast.makeText(ResetPassword.this, UserInputValidation.rpCheck
                                    (emailStr, authCodeStr), Toast.LENGTH_LONG).show();
                }

                // Only checks database if there is data in the email field to prevent NullPointerExceptions
                if(emailStr.length() > 0) {
                    // Gets the data from the corresponding email address's account
                    db.collection("Students").document(emailStr).get().addOnCompleteListener
                            (new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot doc = task.getResult();
                                        // Ensures all data entered is valid
                                        assert doc != null;
                                        if(!doc.exists() && UserInputValidation.rpCheck(emailStr, authCodeStr).isEmpty()){
                                            Toast.makeText(ResetPassword.this, "There is no registered " +
                                                            "account using your entered email address.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            // Ensures the 2FA authentication code is correct
                                            if(doc.exists() && !TwoFactorAuthentication.getTOTPCode
                                                    (doc.getString("secretKey")).equals(authCodeStr)){
                                                Toast.makeText(ResetPassword.this, "The verification " +
                                                                "code you have entered is incorrect.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            else if(doc.exists() && TwoFactorAuthentication.getTOTPCode
                                                    (doc.getString("secretKey")).equals(authCodeStr)){
                                                /*Makes the reset password fields visible
                                                 once the user has been verified */
                                                submit.setVisibility(View.INVISIBLE);
                                                txtNewPass.setVisibility(View.VISIBLE);
                                                txtRePass.setVisibility(View.VISIBLE);
                                                newPass.setVisibility(View.VISIBLE);
                                                rePass.setVisibility(View.VISIBLE);
                                                resetPassword.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
        });

        // Button used to reset the user's password
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the user's inputted new password (and repeated new password)
                newPassStr = newPass.getText().toString();
                rePassStr = rePass.getText().toString();

                // User input validation for the new password
                if (!UserInputValidation.resetPasswordValidation(newPassStr, rePassStr).isEmpty())
                {
                    // Displays an error message to alert the user their data is invalid
                    Toast.makeText(ResetPassword.this, UserInputValidation.resetPasswordValidation
                            (newPassStr, rePassStr), Toast.LENGTH_LONG).show();
                }
                else{
                    // Reference to the user's account
                    final DocumentReference docRef = db.collection("Students").document(emailStr);
                    // Dialog box to confirm password reset - in case the button is accidentally pressed
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                    builder.setMessage("You are about to reset your password. Is this OK?");
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // If user clicks 'yes' - this resets the user's password
                            try {
                                // Hashes and salts the user's password, and stores in the database
                                String [] hashSalt = HashPassword.hashPassword(newPassStr);
                                docRef.update("password", hashSalt[0]);
                                docRef.update("salt", hashSalt[1]);
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                            // Sends the user back to the sign in page so they can sign in with their new password
                            startActivity(new Intent(ResetPassword.this, SignIn.class));
                            Toast.makeText(ResetPassword.this,
                                    "Your password has been reset.", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // If user clicks 'no' - refreshes the page
                            startActivity(new Intent(ResetPassword.this, ResetPassword.class));
                        }
                    });
                    // Shows the alert box
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}