package com.example.csc2033_team19_stubank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;

/* Author - Megan O'Doherty
   This class is the app's change password page.
   This can be used if the user is already logged in but needs to change their password for
   any reason. Due to this, they are asked for their old password before being able to change it. */
public class ChangePassword extends AppCompatActivity {
    // Initialising old, new and re-entered new passwords
    private EditText oldPass, newPass, reNewPass;
    private String oldPassStr, newPassStr, reNewPassStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        // Getting the currently logged in user's email address
        final String email = getIntent().getStringExtra("email");

        // Firebase Firestore initialisation and document containing user's data
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Students")
                .document(email);

        // Initialising fields and buttons on the page
        oldPass = findViewById(R.id.editTextOldPassword);
        newPass = findViewById(R.id.editTextNewPassword);
        reNewPass = findViewById(R.id.editTextReEnterNewPass);

        Button changePassword = findViewById(R.id.btnChngPassword);
        ImageButton backButton = findViewById(R.id.chngPasswordBackBtn);

        // Back button, which returns the user to the settings page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePassword.this, Settings.class));
            }
        });

        // Change password button
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Converts the data entered in the fields to strings
                oldPassStr = oldPass.getText().toString();
                newPassStr = newPass.getText().toString();
                reNewPassStr = reNewPass.getText().toString();

               // Gets the user's data
               docRef.get().addOnCompleteListener
                        (new OnCompleteListener<DocumentSnapshot>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Current snapshot of the data stored for the user
                                    final DocumentSnapshot doc = task.getResult();
                                    // Ensures all data entered is valid
                                    if (!UserInputValidation.changePasswordValidation
                                            (oldPassStr, newPassStr, reNewPassStr, doc).isEmpty()) {
                                        // Displays an error message to alert the user they have entered invalid data
                                        Toast.makeText(ChangePassword.this, UserInputValidation.changePasswordValidation
                                                (oldPassStr, newPassStr, reNewPassStr, doc), Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        /* Alert box will now display to ask for confirmation before changing
                                           the password */
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                                        builder.setTitle("Change Password");
                                        builder.setMessage("Are you sure you " +
                                                "would like to change your password?");
                                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // If user clicks 'yes'
                                                // Changes the user's password
                                                try {
                                                    // Updates the password in the database with hash and salt
                                                    String [] hashSalt = HashPassword.hashPassword(newPassStr);
                                                    docRef.update("password", hashSalt[0]);
                                                    docRef.update("salt", hashSalt[1]);
                                                } catch (NoSuchAlgorithmException e) {
                                                    e.printStackTrace();
                                                }
                                                // Returns the user to the home page
                                                Intent intent = new Intent(ChangePassword.this, Home.class);
                                                intent.putExtra("email", email);
                                                startActivity(intent);
                                                Toast.makeText(ChangePassword.this,
                                                        "You have successfully changed your password.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // If user clicks 'no' - refreshes the page
                                                Intent intent = new Intent(ChangePassword.this, ChangePassword.class);
                                                intent.putExtra("email", email);
                                                startActivity(intent);
                                            }
                                        });
                                        // Shows the alert box
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}