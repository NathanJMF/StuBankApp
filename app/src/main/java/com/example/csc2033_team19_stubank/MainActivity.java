package com.example.csc2033_team19_stubank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/* Author - Nathan Fenwick
   This class is for the main page of the app where the user can select sign in or sign up. */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialises the two buttons.
        Button signInBtn = (Button) findViewById(R.id.btnSignInMain);
        Button signUpBtn = (Button) findViewById(R.id.btnSignUpMain);
        // Listens for when either of the buttons are clicked and appropriately directs the user to the next page.
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignIn.class));
            }
        });

    }
}