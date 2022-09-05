package com.example.csc2033_team19_stubank;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/* Author - Will Holmes
   This class is for the Budget page. It shows the user information about their monthly spending compared
   to the budget goals they set. The current month and year is displayed, as well as how many days of the current month are left.
   The pie chart shows a breakdown of how much of their total spending is in different categories. All categories are colour coded.
   The category breakdown shows spending progress towards the set budget, showing the user how much of the set budget has been
   spent already or, if they've gone over budget, a message that they've gone over budget and by how much. The progress bars also
   indicate how much of the set budget has been spent. The user is also able to go to the Set Budgets page to update
   their set budgets for each category*/

public class Budget extends AppCompatActivity {

    private Double allTotal, groceriesTotal, shoppingTotal, billsTotal, entertainmentTotal, eatingOutTotal,
            universityTotal, transportTotal, otherTotal;
    private Button setBudgetButton;
    private ImageButton transferButton, accountButton, homeButton, potsButton;
    private TextView budgetDate, budgetDaysRemaining, totalText, groceriesText, shoppingText, billsText,
            entertainmentText, eatingOutText, universityText, transportText, otherText;
    private ProgressBar totalBar, groceriesBar, shoppingBar, billsBar, entertainmentBar, eatingOutBar,
            universityBar, transportBar, otherBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_page);

        //Gets the email of the current user
        final String email = getIntent().getStringExtra("email");

        allTotal = 0.0;
        groceriesTotal = 0.0;
        shoppingTotal = 0.0;
        billsTotal = 0.0;
        entertainmentTotal = 0.0;
        eatingOutTotal = 0.0;
        universityTotal = 0.0;
        transportTotal = 0.0;
        otherTotal = 0.0;

        // Initialises an instance of the Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference documentReference = db.collection("Students").document(email);
        final CollectionReference transactions = documentReference.collection("Transactions");

        //Updates the current time to the current Firebase server timestamp
        documentReference.update("currentDate", FieldValue.serverTimestamp()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                documentReference.get().addOnCompleteListener
                        (new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Gets current snapshot of user data
                                    DocumentSnapshot doc = task.getResult();

                                    final Double allBudget, groceriesBudget, shoppingBudget, billsBudget,
                                            entertainmentBudget, eatingOutBudget, universityBudget, transportBudget, otherBudget;

                                    allBudget = doc.getDouble("allBudget");
                                    groceriesBudget = doc.getDouble("groceriesBudget");
                                    shoppingBudget = doc.getDouble("shoppingBudget");
                                    billsBudget = doc.getDouble("billsBudget");
                                    entertainmentBudget = doc.getDouble("entertainmentBudget");
                                    eatingOutBudget = doc.getDouble("eatingOutBudget");
                                    universityBudget = doc.getDouble("universityBudget");
                                    transportBudget = doc.getDouble("transportBudget");
                                    otherBudget = doc.getDouble("otherBudget");

                                    //Gets the current date
                                    Date date = doc.getTimestamp("currentDate").toDate();
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(date);

                                    //Formats text showing current month and year
                                    String showMonth = new SimpleDateFormat("MMMM").format(cal.getTime())+", "+cal.get(Calendar.YEAR);
                                    //Formats text showing days left of current month
                                    String daysLeft = "Days left: "+((cal.getActualMaximum(Calendar.DAY_OF_MONTH)-cal.get(Calendar.DAY_OF_MONTH))+1);

                                    //Displays the date information on UI
                                    budgetDate = findViewById(R.id.budgetDate);
                                    budgetDaysRemaining = findViewById(R.id.budgetDaysRemaining);

                                    budgetDate.setText(showMonth);
                                    budgetDaysRemaining.setText(daysLeft);

                                    //Gets the timestamp of the start of the current month
                                    String startYear = String.valueOf(cal.get(Calendar.YEAR));
                                    String startMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
                                    java.sql.Timestamp startDate = java.sql.Timestamp.valueOf(startYear+"-"+startMonth+"-01 00:00:00.0");

                                    /* Gets all user transactions in descending order (most recent to oldest)
                                    until the start of the current month. This means only transactions made this month are selected */
                                    transactions.orderBy("date", Query.Direction.DESCENDING).endAt(startDate)
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                //For each transaction from this month set to be included in budget, add amount to the all and category totals.
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Double amount = document.getDouble("amount");
                                                    String category = document.getString("category");
                                                    boolean includeBudget = document.getBoolean("includeBudget");
                                                    //Checks whether transaction should be included in budget totals
                                                    if(includeBudget) {
                                                        //Add amount to all total and total for the transaction's category
                                                        amount = amount * -1;
                                                        if (category.equals("Groceries")) {
                                                            allTotal += amount;
                                                            groceriesTotal += amount;
                                                        } else if (category.equals("Shopping")) {
                                                            allTotal += amount;
                                                            shoppingTotal += amount;
                                                        } else if (category.equals("Bills")) {
                                                            allTotal += amount;
                                                            billsTotal += amount;
                                                        } else if (category.equals("Entertainment")) {
                                                            allTotal += amount;
                                                            entertainmentTotal += amount;
                                                        } else if (category.equals("Eating Out")) {
                                                            allTotal += amount;
                                                            eatingOutTotal += amount;
                                                        } else if (category.equals("University")) {
                                                            allTotal += amount;
                                                            universityTotal += amount;
                                                        } else if (category.equals("Transport")) {
                                                            allTotal += amount;
                                                            transportTotal += amount;
                                                        } else {
                                                            allTotal += amount;
                                                            otherTotal += amount;
                                                        }
                                                    }
                                                }

                                                //Draws pie chart shpwing monthly spending breakdown of different categories
                                                drawPieChart(allTotal, groceriesTotal, shoppingTotal, billsTotal,
                                                        entertainmentTotal, eatingOutTotal, universityTotal, transportTotal, otherTotal);

                                                //Sets text for each category to display to user
                                                totalText = findViewById(R.id.budgetTotalText);
                                                totalText.setText(displayText("Total", allTotal, allBudget));
                                                groceriesText = findViewById(R.id.budgetGroceriesText);
                                                groceriesText.setText(displayText("Groceries", groceriesTotal, groceriesBudget));
                                                shoppingText = findViewById(R.id.budgetShoppingText);
                                                shoppingText.setText(displayText("Shopping", shoppingTotal, shoppingBudget));
                                                billsText = findViewById(R.id.budgetBillsText);
                                                billsText.setText(displayText("Bills", billsTotal, billsBudget));
                                                entertainmentText = findViewById(R.id.budgetEntertainmentText);
                                                entertainmentText.setText(displayText("Entertainment", entertainmentTotal, entertainmentBudget));
                                                eatingOutText = findViewById(R.id.budgetEatingOutText);
                                                eatingOutText.setText(displayText("Eating Out", eatingOutTotal, eatingOutBudget));
                                                universityText = findViewById(R.id.budgetUniversityText);
                                                universityText.setText(displayText("University", universityTotal, universityBudget));
                                                transportText = findViewById(R.id.budgetTransportText);
                                                transportText.setText(displayText("Transport", transportTotal, transportBudget));
                                                otherText = findViewById(R.id.budgetOtherText);
                                                otherText.setText(displayText("Other", otherTotal, otherBudget));

                                                //Sets progress of each progress bar for each category showing how much of set budget spent
                                                totalBar = findViewById(R.id.budgetTotalBar);
                                                totalBar.setProgress(budgetProgress(allTotal, allBudget));
                                                groceriesBar = findViewById(R.id.budgetGroceriesBar);
                                                groceriesBar.setProgress(budgetProgress(groceriesTotal, groceriesBudget));
                                                shoppingBar = findViewById(R.id.budgetShoppingBar);
                                                shoppingBar.setProgress(budgetProgress(shoppingTotal, shoppingBudget));
                                                billsBar = findViewById(R.id.budgetBillsBar);
                                                billsBar.setProgress(budgetProgress(billsTotal, billsBudget));
                                                entertainmentBar = findViewById(R.id.budgetEntertainmentBar);
                                                entertainmentBar.setProgress(budgetProgress(entertainmentTotal, entertainmentBudget));
                                                eatingOutBar = findViewById(R.id.budgetEatingOutBar);
                                                eatingOutBar.setProgress(budgetProgress(eatingOutTotal, eatingOutBudget));
                                                universityBar = findViewById(R.id.budgetUniversityBar);
                                                universityBar.setProgress(budgetProgress(universityTotal, universityBudget));
                                                transportBar = findViewById(R.id.budgetTransportBar);
                                                transportBar.setProgress(budgetProgress(transportTotal, transportBudget));
                                                otherBar = findViewById(R.id.budgetOtherBar);
                                                otherBar.setProgress(budgetProgress(otherTotal, otherBudget));
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        });

        //Takes user to Set Budgets page to set new category budgets
        setBudgetButton = (Button) findViewById(R.id.setBudgets);

        setBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Budget.this, SetBudget.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        //Navigation bar buttons
        transferButton = (ImageButton) findViewById(R.id.transferButtonBudgetPage);
        accountButton = (ImageButton) findViewById(R.id.accountButtonBudgetPage);
        homeButton = (ImageButton) findViewById(R.id.homeButtonBudgetPage);
        potsButton = (ImageButton) findViewById(R.id.potsButtonBudgetPage);

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Budget.this, Transfer.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Budget.this, AccountPage.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Budget.this, Home.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        potsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Budget.this, Pots.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }

    //Draws pie chart shpwing user spending breakdown for different categories
    public void drawPieChart(Double allValue, Double groceriesValue, Double shoppingValue, Double billsValue, Double entertainmentValue,
                             Double eatingOutValue, Double universityValue, Double transportValue, Double otherValue) {
        //Finds pie chart in UI
        AnimatedPieView budgetPieChart = findViewById(R.id.budgetPieChart);
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        //If some transactions (set to be included in budget) have been made this month, display pie chart with categories and their colours
        if (allValue > 0) {
            config.startAngle(-90)
                    .addData(new SimplePieInfo(groceriesValue, Color.parseColor("#47a025"), "Groceries"))
                    .addData(new SimplePieInfo(shoppingValue, Color.parseColor("#ed1c24"), "Shopping"))
                    .addData(new SimplePieInfo(billsValue, Color.parseColor("#345995"), "Bills"))
                    .addData(new SimplePieInfo(entertainmentValue, Color.parseColor("#ffb30f"), "Entertainment"))
                    .addData(new SimplePieInfo(eatingOutValue, Color.parseColor("#933be0"), "Eating Out"))
                    .addData(new SimplePieInfo(universityValue, Color.parseColor("#f26419"), "University"))
                    .addData(new SimplePieInfo(transportValue, Color.parseColor("#ff1a7a"), "Transport"))
                    .addData(new SimplePieInfo(otherValue, Color.parseColor("#7f5539"), "Other"))
                    .strokeMode(false).duration(2000).textSize(35).drawText(false);
            //If no transactions (set to be included in budget) made this month, show fully grey pie chart
        } else {
            config.startAngle(-90)
                    .addData(new SimplePieInfo(100, Color.parseColor("#7c7c7c"), "No Transactions"))
                    .strokeMode(false).duration(2000).textSize(35).drawText(false);
        }

        //Set pie chart data and draw
        budgetPieChart.applyConfig(config);
        budgetPieChart.start();
    }

    //Formats category spending text
    public String displayText(String name, Double total, Double goal) {
        String displayMessage;
        //If not gone over budget total, show category name, how much spent and set budget
        if (total > goal) {
            Double overBudget = total - goal;
            displayMessage = name + " • £" + String.format("%.2f", overBudget) + " over budget!";
            //If over budget, show category name and how much the user has gone over budget by
        } else {
            displayMessage = name + " • £" + String.format("%.2f", total) + " / £" + String.format("%.2f", goal);
        }
        return displayMessage;
    }

    //Calculates how much of the category progress bar to fill
    public Integer budgetProgress(Double total, Double goal) {
        Double progressValue;
        //If goal is set to 0, set progress on specific cases to avoid dividing by zero
        if (goal == 0) {
            //If no spending done in category, fill none of the progress bar
            if (total == 0) {
                progressValue = 0.0;
                //If any spending in category, fill entire progress bar
            } else {
                progressValue = 100.0;
            }
            //Calculate percentage of progress bar relating to percentage of spending to goal
        } else {
            progressValue = (total/goal) * 100;
            //If over budget, fill entire progress bar
            if (progressValue > 100) {
                progressValue = 100.0;
            }
        }
        //Return double as integer for progress bar
        return (int)Math.round(progressValue);
    }
}