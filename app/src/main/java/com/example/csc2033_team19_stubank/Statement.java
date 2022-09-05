package com.example.csc2033_team19_stubank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/* Author Karolis Zilius
  Gets user to choose a month where they've made a transaction
  when clicks button creates a pdf with transaction data */

public class Statement extends AppCompatActivity{

    private static final int STORAGE_CODE = 1000;

    // HashMap of months mapped to their number values in ascending order
    HashMap<String, Integer> monthsToNumbers = new HashMap<>();

    // HashMap of numbers mapped to months
    HashMap<Integer, String> numbersToMonths = new HashMap<>();

    // Month abbreviations to full words
    HashMap<String, String> monthsToMonths = new HashMap<>();

    // Variable for the selected date
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        // Currently logged in user's email address
        final String email = getIntent().getStringExtra("email");

        // Retrieve spinner and button objects
        Button generateStatementButton = findViewById(R.id.prepareStatement);
        ImageButton backButton = findViewById(R.id.statementBackBtn);

        // Calculate all months and years that the statements is available for
        getMonthsYears(new getMonthYearsCallback() {
            @Override
            public void onCallback(ArrayList<String> result) {

                // Initialize the spinner with the calculated results
                initializeSpinner(result);
            }

        });

        generateStatementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(date.equals("Select One")){

                    Toast.makeText(Statement.this,"Please select a valid period",Toast.LENGTH_LONG).show();

                }
                else{

                    getSpecifiedTransactions(new getSpecifiedTransactionsCallback() {
                        @Override
                        public void onCallback(ArrayList<QueryDocumentSnapshot> specifiedTransactions) {

                            ArrayList<QueryDocumentSnapshot> transactionsSorted = sortTransactionsDate(specifiedTransactions);

                            grantPermissions(transactionsSorted);
                        }
                    });
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sends the user back to the account page
                Intent intent = new Intent(Statement.this, AccountPage.class);
                intent.putExtra("email", email);
                startActivity(intent);

            }
        });
    }

    protected void getMonthsYears(final getMonthYearsCallback getMonthYearsCallback){

        String email = getIntent().getStringExtra("email");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final ArrayList<String> results = new ArrayList<>();

        results.add("Select One");

        // Read all documents from the firebase and convert it into months to display in a spinner
        db.collection("Students").document(email).collection("Transactions").get().addOnCompleteListener
                (new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        monthsToNumbers.put("Jan", 1);
                        monthsToNumbers.put("Feb", 2);
                        monthsToNumbers.put("Mar", 3);
                        monthsToNumbers.put("Apr", 4);
                        monthsToNumbers.put("May", 5);
                        monthsToNumbers.put("Jun", 6);
                        monthsToNumbers.put("Jul", 7);
                        monthsToNumbers.put("Aug", 8);
                        monthsToNumbers.put("Sep", 9);
                        monthsToNumbers.put("Oct", 10);
                        monthsToNumbers.put("Nov", 11);
                        monthsToNumbers.put("Dec", 12);

                        numbersToMonths.put(1, "Jan");
                        numbersToMonths.put(2, "Feb");
                        numbersToMonths.put(3, "Mar");
                        numbersToMonths.put(4, "Apr");
                        numbersToMonths.put(5, "May");
                        numbersToMonths.put(6, "Jun");
                        numbersToMonths.put(7, "Jul");
                        numbersToMonths.put(8, "Aug");
                        numbersToMonths.put(9, "Sep");
                        numbersToMonths.put(10, "Oct");
                        numbersToMonths.put(11, "Nov");
                        numbersToMonths.put(12, "Dec");

                        monthsToMonths.put("Jan", "January");
                        monthsToMonths.put("Feb", "February");
                        monthsToMonths.put("Mar", "March");
                        monthsToMonths.put("Apr", "April");
                        monthsToMonths.put("May", "May");
                        monthsToMonths.put("Jun", "June");
                        monthsToMonths.put("Jul", "July");
                        monthsToMonths.put("Aug", "August");
                        monthsToMonths.put("Sep", "September");
                        monthsToMonths.put("Oct", "October");
                        monthsToMonths.put("Nov", "November");
                        monthsToMonths.put("Dec", "December");

                        // Find the month and the year when first transaction was made
                        int minMonth = 12;
                        int minYear = 3000;
                        int maxMonth = 0;
                        int maxYear = 0;

                        if (task.isSuccessful()){

                            // Find the year the first transaction was made
                            for (QueryDocumentSnapshot document : task.getResult()){

                                // [1] - month, [5] - year

                                String[] monthYear = (document.getDate("date")).toString().split(" ");

                                // Compare years (find lowest)

                                if (minYear > Integer.parseInt(monthYear[5])){

                                    minYear = Integer.parseInt(monthYear[5]);

                                }

                                if (maxYear < Integer.parseInt(monthYear[5])){

                                    maxYear = Integer.parseInt(monthYear[5]);
                                }

                            }
                            // Find the month the first transaction was made
                            for (QueryDocumentSnapshot document : task.getResult()){

                                String[] monthYear = (document.getDate("date")).toString().split(" ");

                                if (minYear == Integer.parseInt(monthYear[5]) && minMonth > monthsToNumbers.get(monthYear[1])){

                                    minMonth = monthsToNumbers.get(monthYear[1]);
                                }

                                if (maxYear == Integer.parseInt(monthYear[5]) && maxMonth < monthsToNumbers.get(monthYear[1])){

                                    maxMonth = monthsToNumbers.get(monthYear[1]);
                                }
                            }

                            // Calculate how many unique months are there
                            int allMonths = (maxYear - minYear) * 12 + (maxMonth-minMonth);

                            // Loop from the first month to the month before current month

                            int monthTracker = minMonth;
                            int yearTracker = minYear;

                            for (int i=0; i<allMonths+1; i++){

                                // Start with min year and month tracker. When month tracker hits 13
                                // make it 1 instead and add +1 to the year

                                if (monthTracker<13){

                                    results.add(numbersToMonths.get(monthTracker)+" "+yearTracker);

                                    monthTracker +=1;

                                }
                                else if (monthTracker==13){

                                    monthTracker = 1;
                                    yearTracker +=1;

                                    results.add(numbersToMonths.get(monthTracker)+" "+yearTracker);
                                }
                            }

                            getMonthYearsCallback.onCallback(results);
                        }
                    }
                });
    }

    private interface getMonthYearsCallback{
        void onCallback(ArrayList<String> result);
    }

    public void initializeSpinner(ArrayList<String> arrayList){

        Spinner spinner = findViewById(R.id.spinnerStatement);

        // Add the data to the Spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                date= parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSpecifiedTransactions(final getSpecifiedTransactionsCallback getSpecifiedTransactionsCallback){

        // Get the email from intent to query the transactions collection
        String email = getIntent().getStringExtra("email");

        // Get an instance of firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // An Array for all the transactions from the specified date
        final ArrayList<QueryDocumentSnapshot> allTransactions = new ArrayList<>();

        // Get all the transactions specified in the period
        db.collection("Students").document(email).collection("Transactions").get().addOnCompleteListener
                (new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            // The month and year requested by the user
                            String[] monthYearRequested = date.split(" ");

                            for (QueryDocumentSnapshot document : task.getResult()){

                                // Retrieve the date from the document and split it into an array
                                String[] documentDate = (document.getDate("date")).toString().split(" ");

                                // If the requested month and year matched the ones od the document
                                // add it to the array
                                if (documentDate[1].equals(monthYearRequested[0]) && documentDate[5].equals(monthYearRequested[1])){

                                    allTransactions.add(document);

                                }
                            }
                            getSpecifiedTransactionsCallback.onCallback(allTransactions);
                        }
                    }
                });
    }

    private interface getSpecifiedTransactionsCallback{

        void onCallback(ArrayList<QueryDocumentSnapshot> specifiedTransactions);

    }

    ArrayList<QueryDocumentSnapshot> sortTransactionsDate(ArrayList<QueryDocumentSnapshot> allTransactions){

        // Array for sorted transactions
        final ArrayList<QueryDocumentSnapshot> allTransactionsSorted = new ArrayList<>();

        // Sort the transactions in chronological order (sort by day in ascending order)
        // Iterate through 31 days (the maximum that a month can have)
        for (int i=1; i<32; i++){

            for (QueryDocumentSnapshot document : allTransactions){

                // Split the date into a String array so the day could be compared
                String[] documentDate = (document.getDate("date")).toString().split(" ");

                // If the day matches the outer loop add it to the sorted array
                if (documentDate[2].equals(String.valueOf(i))){

                    allTransactionsSorted.add(document);
                }
            }
        }
        return allTransactionsSorted;
    }

    // Create a PDF from the specified data
    public void grantPermissions (ArrayList<QueryDocumentSnapshot> transactions) {


        // Check if OS is lower or bigger than Marshmallow (storage permission needed)

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            // Permission not granted, request it
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, STORAGE_CODE);

        } else {
            // Permission already granted, call save PDF method

            createPDF((transactions));

        }
    }

    // When permissions are granted or denied matching messages are displayed
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(Statement.this, "Permission granted.", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(Statement.this, "Permission denied.", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void createPDF(final ArrayList<QueryDocumentSnapshot> transactions){

        // Create a Document class object
        final Document mDoc = new Document();

        // Fonts used in the PDF
        Font titles = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD, BaseColor.BLACK);
        final Font titles2 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.BLACK);
        final Font gainMoney = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, new BaseColor(0, 204, 0));
        final Font transferMoney = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);
        final Font personalDetailsFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL,  BaseColor.BLACK);
        final Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL,  BaseColor.BLACK);

        // Check if there are any transactions for the chosen month
        if(String.valueOf(transactions.size()).equals("0")){

            Toast.makeText(Statement.this,"No transactions available for this period",Toast.LENGTH_LONG).show();

        }
        else{

            // PDF file name
            final String mFileName = "StuBank Statement "+date;
            //PDF file path
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            final String mFilePath = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + mFileName+ ".pdf";

            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            try{
                // Create instance of PDFWriter class
                PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
                //Open the document for writing
                mDoc.open();

                // Add Metadata
                mDoc.addAuthor("StuBank Statement");

                // Add the our logo to the statements
                try {
                    // Get output stream and add the logo to the statement using it
                    Drawable drawable = getResources().getDrawable(R.drawable.logostatement);
                    BitmapDrawable bitDw = ((BitmapDrawable) drawable);
                    Bitmap bmp = bitDw.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = Image.getInstance(stream.toByteArray());
                    mDoc.add(image);
                } catch(IOException ex) {

                    Log.w("IMAGE", ex.getMessage());

                }

                // Add the title to the statement
                String [] statementDate = Objects.requireNonNull(transactions.get(0).getDate("date")).toString().split(" ");
                final Paragraph statementTitle = new Paragraph("Statement "+monthsToMonths.get(statementDate[1])+" "+statementDate[5], titles);
                statementTitle.setAlignment(Element.ALIGN_CENTER);
                mDoc.add(statementTitle);
                mDoc.add(Chunk.NEWLINE);
                mDoc.add(Chunk.NEWLINE);

                // Personal details title
                Paragraph personalDetailsTitle = new Paragraph("Personal Details", titles2);
                statementTitle.setAlignment(Element.ALIGN_LEFT);
                mDoc.add(personalDetailsTitle);
                mDoc.add(Chunk.NEWLINE);

                writePersonalDetails(new writePersonalDetailsCallback() {
                    @Override
                    public void onCallback(String[] personalDetails) throws DocumentException {

                        // Personal details
                        Paragraph personalDetailsName = new Paragraph("Full Name: "+personalDetails[0]+" "+personalDetails[1], personalDetailsFont);
                        personalDetailsName.setAlignment(Element.ALIGN_LEFT);
                        mDoc.add(personalDetailsName);
                        mDoc.add(Chunk.NEWLINE);

                        Paragraph personalDetailsAccountNumber = new Paragraph("Account Number: "+personalDetails[2], personalDetailsFont);
                        personalDetailsAccountNumber.setAlignment(Element.ALIGN_LEFT);
                        mDoc.add(personalDetailsAccountNumber);
                        mDoc.add(Chunk.NEWLINE);

                        // Transactions title
                        Paragraph transactionsTitle = new Paragraph("Transactions", titles2);
                        statementTitle.setAlignment(Element.ALIGN_LEFT);
                        mDoc.add(transactionsTitle);
                        mDoc.add(Chunk.NEWLINE);

                        // Pdf Table with 4 columns and columns proportions
                        PdfPTable table = new PdfPTable(4);
                        table.setWidths(new int [] {3,4,4,12});

                        // Headers
                        PdfPCell header = new PdfPCell(new Phrase("Date", normalFont));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setBorder(PdfPCell.NO_BORDER | PdfPCell.LEFT);
                        table.addCell(header);

                        header = new PdfPCell(new Phrase("Amount", normalFont));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setBorder(PdfPCell.NO_BORDER | PdfPCell.LEFT);
                        table.addCell(header);

                        header = new PdfPCell(new Phrase("Balance", normalFont));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setBorder(PdfPCell.NO_BORDER | PdfPCell.LEFT);
                        table.addCell(header);

                        header = new PdfPCell(new Phrase("Description", normalFont));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setBorder(PdfPCell.NO_BORDER | PdfPCell.LEFT | PdfPCell.RIGHT);
                        table.addCell(header);

                        // Empty line
                        PdfPCell space = new PdfPCell(new Phrase(" "));
                        space.setBorder(PdfPCell.NO_BORDER | PdfPCell.LEFT);
                        table.addCell(space);

                        space = new PdfPCell(new Phrase(" "));
                        space.setBorder(PdfPCell.NO_BORDER | PdfPCell.LEFT);
                        table.addCell(space);

                        space = new PdfPCell(new Phrase(" "));
                        space.setBorder(PdfPCell.NO_BORDER |  PdfPCell.LEFT);
                        table.addCell(space);

                        space = new PdfPCell(new Phrase(" "));
                        space.setBorder(PdfPCell.NO_BORDER |  PdfPCell.LEFT | PdfPCell.RIGHT);
                        table.addCell(space);

                        table.setHeaderRows(1);

                        // Variable for PDF table cell
                        PdfPCell cellData;

                        // The budget wil be shown at the end of every day in the statement
                        long budget = getStartingBudget(transactions);

                        // For loop to display all the cells
                        for (int i=0; i<transactions.size(); i++){

                            // Retrieve data frm the transactions object
                            String [] transactionDate = transactions.get(i).getDate("date").toString().split(" ");
                            long amount = transactions.get(i).getLong("amount");
                            String description = transactions.get(i).getString("name");

                            // Add the amount spent to the budget
                            budget += amount;

                            // Display date only for the first transaction of the same date
                            if(i==0){

                                cellData = new PdfPCell(new Phrase(transactionDate[2]+" "+transactionDate[1], normalFont));

                            }
                            else{

                                String [] transactionPreviousDate = transactions.get(i-1).getDate("date").toString().split(" ");

                                if (Integer.parseInt(transactionPreviousDate[2]) != Integer.parseInt(transactionDate[2])){

                                    cellData = new PdfPCell(new Phrase(transactionDate[2]+" "+transactionDate[1], normalFont));
                                }
                                else{
                                    cellData = new PdfPCell(new Phrase(" "));
                                }
                            }

                            cellData.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cellData.setBorder(PdfPCell.NO_BORDER | PdfPCell.LEFT);
                            table.addCell(cellData);

                            // Display amount in green if money gained and in red if transferred out
                            if (amount>0){
                                cellData = new PdfPCell(new Phrase("£ "+amount, gainMoney));

                            }else{

                                cellData = new PdfPCell(new Phrase("£ "+amount, transferMoney));

                            }
                            cellData.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cellData.setBorder(PdfPCell.NO_BORDER | PdfPCell.LEFT);
                            table.addCell(cellData);

                            // Display budget on the same line as the last transaction of the day
                            if (i == transactions.size()-1){

                                cellData = new PdfPCell(new Phrase("£ "+budget, normalFont));

                            }else{

                                String [] transactionNextDate = transactions.get(i+1).getDate("date").toString().split(" ");

                                if (Integer.parseInt(transactionNextDate[2]) != Integer.parseInt(transactionDate[2])){

                                    cellData = new PdfPCell(new Phrase("£ "+budget, normalFont));
                                }
                                else{
                                    cellData = new PdfPCell(new Phrase(" "));
                                }

                            }

                            cellData.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cellData.setBorder(PdfPCell.NO_BORDER | PdfPCell.LEFT);
                            table.addCell(cellData);

                            // Display the description of the transaction
                            cellData = new PdfPCell(new Phrase(description, normalFont));
                            cellData.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cellData.setBorder(PdfPCell.NO_BORDER | PdfPCell.LEFT | PdfPCell.RIGHT);
                            table.addCell(cellData);

                        }

                        // Add the table to the document
                        mDoc.add(table);

                        // Close the document
                        mDoc.close();

                        // Make a message stating where the document was created
                        Toast.makeText(Statement.this,mFileName+".pdf\nis saved to\n"+mFilePath,Toast.LENGTH_LONG).show();
                    }
                });


            }catch(Exception e){
                // Display a log of the error
                Log.d("PDF_Table", e.getMessage());
            }
        }
    }

    private void writePersonalDetails(final writePersonalDetailsCallback writePersonalDetailsCallback){

        final String [] result = new String[3];

        String email = getIntent().getStringExtra("email");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve full name and account number details and print tem
        db.collection("Students").document(email).get().addOnCompleteListener
                (new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();

                            assert doc != null;
                            String firstName = doc.getString("firstName");
                            String lastName= doc.getString("lastName");
                            String accountNumber = doc.getString("accountNumber");

                            result[0] =firstName;
                            result[1]= lastName;
                            result[2]=accountNumber;

                            try {
                                writePersonalDetailsCallback.onCallback(result);
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                            Log.w("Statement_page", "Error getting document", task.getException());

                        }
                    }
                });
    }

    private interface writePersonalDetailsCallback{

        void onCallback(String [] personalDetails) throws DocumentException;

    }

    // Check all the transactions and find the first transaction of the month, then retrieve a budget
    // from the transaction, so later transaction could be counted by adding or substracting to it
    private Long getStartingBudget(ArrayList<QueryDocumentSnapshot> transactions){

        int minH = 24;
        int minMin = 60;
        int minSec = 60;
        int minDay = 31;
        int position = 0;

        // Find minimum time
        for (int i=0; i<transactions.size(); i++){

            String [] transactionDate = Objects.requireNonNull(transactions.get(i).getDate("date")).toString().split(" ");
            String [] time = transactionDate[3].split(":");

            // Check for th lower day
            if (minDay>Integer.parseInt(transactionDate[2])){

                minDay = Integer.parseInt(transactionDate[2]);
                minH = Integer.parseInt(time[0]);
                minMin = Integer.parseInt(time[1]);
                minSec = Integer.parseInt(time[2]);
                position = i;

            }
            // Check for th lower hours
            else if(minDay == Integer.parseInt(transactionDate[2]) && minH> Integer.parseInt(time[0])){

                minH = Integer.parseInt(time[0]);
                minMin = Integer.parseInt(time[1]);
                minSec = Integer.parseInt(time[2]);
                position = i;

            }
            // Check for the lower minute
            else if(minDay == Integer.parseInt(transactionDate[2]) && minH == Integer.parseInt(time[0]) && minMin> Integer.parseInt(time[1])){

                minMin = Integer.parseInt(time[1]);
                minSec = Integer.parseInt(time[2]);
                position = i;

            }
            // Check for the lower second
            else if(minDay == Integer.parseInt(transactionDate[2]) && minH == Integer.parseInt(time[0]) && minMin == Integer.parseInt(time[1]) && minSec> Integer.parseInt(time[2])){

                minSec = Integer.parseInt(time[2]);
                position = i;

            }
        }

        // Return balance of the lowest time position found
        return transactions.get(position).getLong("previousBalance");
    }
}