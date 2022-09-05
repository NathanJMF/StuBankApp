package com.example.csc2033_team19_stubank;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/* Author - Megan O'Doherty
   This class is the Transfer page in the app - where users make transfers to other
   people or bank accounts. They can also choose a category for their transfer, allowing
   for more accurate budgeting.

   If the user is sending money to another StuBank user, the payee will also receive the transfer
   into their account, adding to their balance. */
public class Transfer extends AppCompatActivity {
    // Variable initialisation
    private EditText amount, sortCode, accNumber, password;
    private Spinner spinner;
    private Double amountDouble;
    private String sortCodeStr;
    private int accNumberInt;
    private String passwordStr;
    private String categoryStr;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference students = db.collection("Students");
    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_transfer);

        // Firebase Firestore initialisation
        final String email = getIntent().getStringExtra("email");
        final DocumentReference documentReference = db.collection("Students").document(email);
        final DocumentReference savingsPot = documentReference.collection("Pots").document("Savings");

        // Reference to collection which stores user's transactions
        final CollectionReference collRef = documentReference.collection("Transactions");
        final CollectionReference accNos = db.collection("AccountNumbers");

        // Initialising transfer information
        amount = findViewById(R.id.editTextTransferAmount);
        sortCode = findViewById(R.id.editTextPayeeSortCode);
        accNumber = findViewById(R.id.editTextPayeeAccNo);
        password = findViewById(R.id.editTextTransferPassword);
        spinner = findViewById(R.id.Category);

        // Transfer button press logic
        Button transferBtn = (Button) findViewById(R.id.makeTransferBtn);
        transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Setting values for the payment amount, payee's sort code and account number,
                   the user's password and transaction category */

                if ((amount.length() == 0)) {
                    amountDouble = (Double) 0.0;
                } else {
                    amountDouble = Double.parseDouble(amount.getText().toString());
                }

                sortCodeStr = sortCode.getText().toString();

                if ((accNumber.length() == 0)) {
                    accNumberInt = 0;
                } else {
                    accNumberInt = Integer.parseInt(accNumber.getText().toString());
                }

                passwordStr = password.getText().toString();
                categoryStr = spinner.getSelectedItem().toString();

                // Adds the transfer to the user's transactions
                documentReference.get().addOnCompleteListener
                        (new OnCompleteListener<DocumentSnapshot>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Current snapshot of the data stored for the user
                                    final DocumentSnapshot doc = task.getResult();
                                    assert doc != null;
                                    final Double balance = doc.getDouble("balance");
                                    final Double overdraft = doc.getDouble("overdraft");
                                    final Timestamp creationDate = doc.getTimestamp("creationDate");
                                    final boolean frozen = doc.getBoolean("frozen");
                                    final boolean round = doc.getBoolean("roundUp");
                                    // Ensures all data entered is valid
                                    if (!UserInputValidation.transferValidation(
                                            amount.getText().toString(),amountDouble, sortCodeStr,
                                            accNumberInt, passwordStr, doc).isEmpty()) {
                                        // Displays an error message to alert the user they have entered invalid data
                                        Toast.makeText(Transfer.this, UserInputValidation.transferValidation
                                                (amount.getText().toString(), amountDouble, sortCodeStr,
                                                        accNumberInt, passwordStr, doc), Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Transfer.this);
                                        /* Stops the user from making any transfers while they have
                                           frozen their card. */
                                        if(frozen){
                                            builder.setTitle("Card Frozen");
                                            builder.setMessage("You cannot currently make any " +
                                                    "transfers as your card is frozen. Please unfreeze " +
                                                    "your card before trying to make another transfer.");
                                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(Transfer.this, Transfer.class);
                                                    intent.putExtra("email", email);
                                                    startActivity(intent);
                                                }
                                            });
                                            // Show the dialog box
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }

                                        /* If the transfer would make the user go over their
                                           overdraft limit, they are prevented from going ahead. */
                                        else if(balance - amountDouble < -1000) {
                                            builder.setMessage("You do not have enough funds to make this " +
                                                    "transfer, as you will have exceeded your overdraft limit.");
                                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(Transfer.this, Transfer.class);
                                                    intent.putExtra("email", email);
                                                    startActivity(intent);
                                                }
                                            });
                                            // Show the dialog box
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                        /* Alert box will now display to ask for confirmation, giving the user a chance
                                           to double check the details before approving the transfer */
                                        else{
                                            @SuppressLint("DefaultLocale") String amountFormat = String.format("%.2f", amountDouble);
                                            builder.setTitle("Transfer Confirmation");
                                            builder.setMessage("You are about to make a transfer of: £" +
                                                    amountFormat + " to sort code: " + sortCodeStr + " and account number: " +
                                                    accNumberInt + ". Is this correct?");
                                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // If user clicks 'yes'
                                                    String accNo = accNumber.getText().toString();
                                                    addTransfer(accNo, balance, overdraft, creationDate, documentReference, collRef);

                                                    if (sortCodeStr.equals("11-22-33") && round) {
                                                        transferBetweenUsers(accNo, accNos, documentReference);
                                                        documentReference.update("balance", balance - amountDouble);
                                                        roundUpTransaction(amountDouble, savingsPot, documentReference);
                                                    }

                                                    if(!sortCodeStr.equals("11-22-33") && round){
                                                        roundUpTransaction(amountDouble, savingsPot, documentReference);
                                                    }
                                                    Intent intent = new Intent(Transfer.this, Transfer.class);
                                                    intent.putExtra("email", email);
                                                    startActivity(intent);
                                                    Toast.makeText(Transfer.this,
                                                            "Your transfer has been made successfully.", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // If user clicks 'no' - refreshes the transfer page
                                                    Intent intent = new Intent(Transfer.this, Transfer.class);
                                                    intent.putExtra("email", email);
                                                    startActivity(intent);

                                                }
                                            });
                                            // Show the dialog box
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    }
                                }
                            }
                        });
            }
        });

        // Initialising the buttons on the bottom of the page
        ImageButton account =  findViewById(R.id.accountButtonTransfer);
        ImageButton home =  findViewById(R.id.homeButtonTransfer);
        ImageButton pots =  findViewById(R.id.potsButtonTransfer);
        ImageButton budget =  findViewById(R.id.budgetButtonTransfer);

        // Click events for each button, to redirect the user to the appropriate page
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Transfer.this, AccountPage.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Transfer.this, Home.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        pots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Transfer.this, Pots.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Transfer.this, Budget.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }

    /**
     * Adds the current transfer to the user's collection of transactions in the Firebase
     * Firestore.
     *
     * @param accNo - the account number of the payee
     * @param balance - the user's current balance
     * @param overdraft - the user's current available overdraft
     * @param documentReference - reference to the Firebase document containing the user's details
     * @param collRef - reference to the Transactions collection for the user
     */
    private void addTransfer(String accNo, final Double balance, final Double overdraft, final Timestamp creationDate,
                             final DocumentReference documentReference, final CollectionReference collRef) {
        final Double newBal = (balance - amountDouble);
        // Stores the difference in days between the account creation date and transfer date
        long timeDiff = (Timestamp.now().toDate().getTime() - creationDate.toDate().getTime());
        final int timeDiffInt = (int) TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
        // Adds the transfer as a transaction
        if(!sortCodeStr.equals("11-22-33")) {
            Transaction transfer = new Transaction(amountDouble * -1,
                    categoryStr, FieldValue.serverTimestamp(),
                    "Money Transfer to " + sortCodeStr +
                            " " + accNumberInt, "moneyTransfer",
                    balance, newBal, timeDiffInt, getDayOfWeek(calendar), true);
            // Updates the user's balance and overdraft and adds the transfer to their transactions
            documentReference.update("balance", newBal);
            documentReference.update("overdraft", getOverdraftAmount(newBal));
            collRef.add(transfer);
        }
        else{
            students.whereEqualTo("accountNumber", accNo).get().addOnCompleteListener
                    (new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                assert querySnapshot != null;
                                /* If there is no currently registered user with the entered
                                   account number */
                                if(querySnapshot.isEmpty()){
                                    // Adds a new transaction as a transfer to 'another user'
                                    Transaction transfer = new Transaction(amountDouble * -1,
                                            categoryStr, FieldValue.serverTimestamp(),
                                            "Money Transfer to Other StuBank User", "moneyTransfer",
                                            balance, (balance - amountDouble), timeDiffInt, getDayOfWeek(calendar), true);

                                    // Updates the user's balance and overdraft and adds the transfer to their transactions
                                    documentReference.update("balance", (newBal));
                                    documentReference.update("overdraft", getOverdraftAmount(newBal));
                                    collRef.add(transfer);
                                }
                                else {
                                    // If there is a registered user with the entered account number
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        assert document != null;
                                        // Gets the current user's name
                                        String firstName = document.getString("firstName");
                                        String lastName = document.getString("lastName");

                                        // Transaction details
                                        Transaction transfer = new Transaction(amountDouble * -1,
                                                categoryStr, FieldValue.serverTimestamp(),
                                                "Money Transfer to " + firstName +
                                                        " " + lastName, "moneyTransfer",
                                                balance, (balance - amountDouble), timeDiffInt, getDayOfWeek(calendar), true);

                                        /* Updates the user's balance and overdraft and adds the transfer
                                           to their transactions */
                                        documentReference.update("balance", (newBal));
                                        documentReference.update("overdraft", getOverdraftAmount(newBal));
                                        collRef.add(transfer);
                                    }
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Allows for the transfer of money between StuBank users.
     * If the user inputs the StuBank sort code, this will add money to the
     * account with the entered account number. If there is no user with the entered account number,
     * the transfer is sent to 'another StuBank user'.
     *
     * @param accNo - the payee's account number
     * @param accNos - a reference to the collection which stores all registered account numbers.
     * @param currentUser - a reference to the document, storing the current user's data.
     */
    private void transferBetweenUsers(final String accNo, CollectionReference accNos, final DocumentReference currentUser) {
        // Reference to the payee's account number
        DocumentReference dr = accNos.document(accNo);
        final Transaction transaction = new Transaction();
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        // Gets the payee's email address
                        String email = document.getString("email");
                        assert email != null;
                        // Referencing the payee's details and Transactions collection
                        final DocumentReference dr = db.collection("Students")
                                .document(email);
                        final CollectionReference cr = dr.collection("Transactions");

                        dr.get().addOnCompleteListener
                                (new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            assert doc != null;
                                            // Gets the user's balance and account creation date
                                            Double balance = doc.getDouble("balance");
                                            final Double overdraft = doc.getDouble("overdraft");
                                            Timestamp creationDate = doc.getTimestamp("creationDate");
                                            /* Getting difference between transaction date and account
                                               creation date to store in the database */
                                            long timeDiff = (Timestamp.now().toDate().getTime() - creationDate.toDate().getTime());
                                            final int timeDiffInt = (int) TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                                            assert balance != null;
                                            /*Sets the transaction previous balance to the
                                              user's current balance */
                                            transaction.setPreviousBalance(balance);
                                            transaction.setTimeDiff(timeDiffInt);
                                            currentUser.get().addOnCompleteListener
                                                    (new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot doc = task.getResult();
                                                                assert doc != null;
                                                                // Gets the current user's name
                                                                String firstName = doc.getString("firstName");
                                                                String lastName = doc.getString("lastName");

                                                                /* Sets the transaction details, updates the payee's balance
                                                                   and adds the transaction to their collection of transactions */
                                                                transaction.setAmount(amountDouble);
                                                                transaction.setCategory(categoryStr);
                                                                transaction.setDate(FieldValue.serverTimestamp());
                                                                transaction.setName("Money Transfer from " + firstName + " " + lastName);
                                                                transaction.setReference("moneyTransfer");
                                                                transaction.setNewBalance(transaction.getPreviousBalance() + amountDouble);
                                                                transaction.setDayOfWeek(getDayOfWeek(calendar));
                                                                transaction.setIncludeBudget(false);
                                                                dr.update("balance", transaction.getNewBalance());
                                                                dr.update("overdraft", getOverdraftAmount(transaction.getNewBalance()));
                                                                cr.add(transaction);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });

                    }
                }
            }
        });
    }

    /**
     * Rounds up the user's transfer and stores the difference in their savings pot, if they
     * have turned on the rounding feature.
     *
     * @param amount - the transfer amount
     * @param savingsPot - reference to the Firebase document, containing the user's savings pot
     * @param user - reference to the user's data within Firebase
     */
    private void roundUpTransaction(final double amount, final DocumentReference savingsPot,
                                    final DocumentReference user){
        if((amount % 1)!= 0){
            // Rounds up the transaction amount and gets how much to store in pot
            double roundedUp = Math.ceil(amount);
            final double amountToSave = (roundedUp - amount);

            savingsPot.get().addOnCompleteListener
                    (new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                // Gets the user's savings pot
                                DocumentSnapshot doc = task.getResult();
                                assert doc != null;
                                double potBal = doc.getDouble("balance");
                                // Adds the remainder of the rounding to the pot's balance
                                savingsPot.update("balance", potBal + amountToSave);
                            }
                        }
                    });

            user.get().addOnCompleteListener
                    (new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                assert doc != null;
                                double balance = doc.getDouble("balance");
                                /* Updates the user's account balance, to subtract the rounded
                                   amount */
                                user.update("balance", balance - amountToSave);
                                user.update("overdraft", getOverdraftAmount(balance - amountToSave));
                            }
                        }
                    });

        }
    }

    /**
     * Gets the current day of the week, and returns it as a zero-based
     * integer, starting on Monday.
     *
     * @param calendar - the calendar
     * @return the day of the week as an integer
     */
    public static Integer getDayOfWeek(Calendar calendar) {

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return 0;
        }
        else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            return 1;
        }
        else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            return 2;
        }
        else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            return 3;
        }
        else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            return 4;
        }
        else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return 5;
        }
        else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return 6;
        }
        return null;
    }

    /**
     * Calculates and returns the user's overdraft amount, if their balance
     * will be below £0 after making a transfer.
     *
     * @param balance - the user's balance after making the transfer
     * @return the user's overdraft amount
     */
    public static Double getOverdraftAmount(Double balance) {
        if (balance < 0) {
            return (1000.0 + balance);
        } else {
            return 1000.0;
        }
    }
}