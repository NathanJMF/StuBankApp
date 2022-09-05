package com.example.csc2033_team19_stubank;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/* Author - Megan O'Doherty
   This is the settings page, used to toggle the rounding up of transactions, activate dark/light
   mode display, change the user's password and log out of their account. */
public class Settings extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        // Firebase Firestore initialisation
        final String email = getIntent().getStringExtra("email");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the document containing the user's data
        final DocumentReference documentReference = db.collection("Students")
                .document(email);

        // Using the light/dark mode switch
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch lightMode = findViewById(R.id.switchDarkMode);
        lightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // Sets the app to dark mode if the switch is set to on
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    // Sets the app to light mode if the switch is set to off
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        // Using the switch for turning on/off rounding up transactions
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch roundUpSwitch = findViewById(R.id.switchRoundUp);
        roundUpSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // Automatically rounds up transfers, storing the difference in the savings pot
                    documentReference.update("roundUp", true);
                }
                else {
                    // Turns off the rounding up feature
                    documentReference.update("roundUp", false);
                }
            }
        });

        // Initialises all the buttons on this page.
        Button changePasswordButton = findViewById(R.id.btnChangePassword);
        Button logOutButton = findViewById(R.id.btnLogOut);
        ImageButton backButton = findViewById(R.id.settingsBackBtn);

        // Listens for when the buttons are clicked and appropriately directs the user to
        // the correct page.
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, ChangePassword.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alert box will display to ask for confirmation, preventing accidental log outs
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setMessage("Are you sure you would like to log out of your account?");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent loginPage = new Intent(Settings.this, MainActivity.class);
                        // Resets the activity so users can log in with a different account
                        loginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // Opens the login/sign up page
                        startActivity(loginPage);
                        // Finishes the current context
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Sends user back to settings page
                        Intent intent = new Intent(Settings.this, Settings.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        
                    }
                });
                // Shows the alert box
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, AccountPage.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
}
