package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
//Author Megan Jinks
//displays all of the users pots
public class Pots extends AppCompatActivity {
    private ImageButton transferButton, accountButton, homeButton, budgetButton;
    private Button newPotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pot_page);

        final String email = getIntent().getStringExtra("email");

        // Initialises the buttons
        transferButton = (ImageButton) findViewById(R.id.transferButton2);
        accountButton = (ImageButton) findViewById(R.id.accountButton2);
        homeButton = (ImageButton) findViewById(R.id.homePageButton2);
        budgetButton = (ImageButton) findViewById(R.id.budgetButtonPotPage);
        newPotButton = (Button) findViewById(R.id.newPotButton);
        final Button potButton1 = (Button) findViewById(R.id.potButton1);
        final Button potButton2 = (Button) findViewById(R.id.potButton2);
        final Button potButton3 = (Button) findViewById(R.id.potButton3);
        final Button potButton4 = (Button) findViewById(R.id.potButton4);
        final Button potButton5 = (Button) findViewById(R.id.potButton5);
        final Button potButton6 = (Button) findViewById(R.id.potButton6);
        final Button potButton7 = (Button) findViewById(R.id.potButton7);
        final Button potButton8 = (Button) findViewById(R.id.potButton8);
        final Button potButton9 = (Button) findViewById(R.id.potButton9);
        final Button potButton10 = (Button) findViewById(R.id.potButton10);
        final Button potButton11 = (Button) findViewById(R.id.potButton11);

        //create list of all pot buttons
        final List<Button> listButtons = new ArrayList<>();
        listButtons.add(potButton1);
        listButtons.add(potButton2);
        listButtons.add(potButton3);
        listButtons.add(potButton4);
        listButtons.add(potButton5);
        listButtons.add(potButton6);
        listButtons.add(potButton7);
        listButtons.add(potButton8);
        listButtons.add(potButton9);
        listButtons.add(potButton10);
        listButtons.add(potButton11);

        //create empty list
        final List<String> assignedButtons = new ArrayList<>();

        // Initialises an instance of the Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Students").document(email)
                .collection("Pots").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //create list of all pots from database of user
                    List<DocumentSnapshot> listPots = task.getResult().getDocuments();
                    for (int i = 0; i < listPots.size(); i++) {
                        DocumentSnapshot dc = listPots.get(i);
                        // Gets the pot name and add to assignedButtons list
                        final String namePot = dc.getString("name");
                        assignedButtons.add(namePot);

                        //get button index i and set text to name pot
                        listButtons.get(i).setText(namePot);
                        //get button index i and set visible
                        listButtons.get(i).setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        // Listens for when either of the buttons are clicked and appropriately directs the user to the next page.
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, Transfer.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, AccountPage.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, Home.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, Budget.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        newPotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, NewPot.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        potButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //send pot with assignedButton[0]
                intent.putExtra("pot", assignedButtons.get(0));
                startActivity(intent);
                finish();
            }
        });
        potButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //if list larger than 0 send index 1
                if(assignedButtons.size() >0) {
                    intent.putExtra("pot", assignedButtons.get(1));
                }
                startActivity(intent);
                finish();
            }
        });
        potButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //if list larger than 1 send index 2
                if(assignedButtons.size() >1) {
                    intent.putExtra("pot", assignedButtons.get(2));
                }
                startActivity(intent);
                finish();
            }
        });
        potButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //if list larger than 2 send index 3
                if(assignedButtons.size() >2){
                intent.putExtra("pot", assignedButtons.get(3));
                }
                startActivity(intent);
                finish();
            }
        });
        potButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //if list larger than 3 send index 4
                if(assignedButtons.size()>3) {
                    intent.putExtra("pot", assignedButtons.get(4));
                }
                startActivity(intent);
                finish();
            }
        });
        potButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //if list larger than 4 send index 5
                if(assignedButtons.size()>4) {
                    intent.putExtra("pot", assignedButtons.get(5));
                }
                startActivity(intent);
                finish();
            }
        });
        potButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //if list larger than 5 send index 6
                if(assignedButtons.size() >5) {
                    intent.putExtra("pot", assignedButtons.get(6));
                }
                startActivity(intent);
                finish();
            }
        });
        potButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //if list larger than 6 send index 7
                if(assignedButtons.size() >6) {
                    intent.putExtra("pot", assignedButtons.get(7));
                }
                startActivity(intent);
                finish();
            }
        });
        potButton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //if list larger than 7 send index 8
                if(assignedButtons.size() >7) {
                    intent.putExtra("pot", assignedButtons.get(8));
                }
                startActivity(intent);
                finish();
            }
        });
        potButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //if list larger than 8 send index 9
                if(assignedButtons.size() >8) {
                    intent.putExtra("pot", assignedButtons.get(9));
                }
                startActivity(intent);
                finish();
            }
        });
        potButton11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pots.this, SavingPot.class);
                intent.putExtra("email", email);
                //if list larger than 9 send index 10
                if(assignedButtons.size() >9) {
                    intent.putExtra("pot", assignedButtons.get(10));
                }
                startActivity(intent);
                finish();
            }
        });
    }
}
