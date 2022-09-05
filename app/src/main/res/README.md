# StuBank CSC2033 Project - Team Alpha/Team 19

The application has a sign in and sign up feature where the user can create an account and log in.

The user will be sent a link in a verification email to their university email address. This link must be
clicked to verify the email and allow the user to continue with the sign up process.

For security, two-factor authentication is used. The user will need to download the Google Authenticator app 
(download here: <https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2&hl=en_GB&gl=US>)
to scan the QR code or enter the given code to get a random 6 digit code. This authentication code changes every 30 seconds
and is used each time the user logs in.

Once the user has signed in there are 5 main pages; Transfer where the user can send money to others, 
Account where they can see their balance and overdraft, Home page where balance, overdraft and recent 
transactions are shown, Pots where the user can put money aside to save up, and budget where the user
can set goals of how much they will spend throughout the month.

In settings the user can change their password, change to dark or light mode, choose whether they want 
their transactions to be rounded up and placed in their saving pot and log out.

In budgets the user has 8 categories: groceries, shopping, bills, entertainment, eating out, university
transport and other. They can set budgets for how much they would like to spend each month and will
be able to keep track if they are on target. The spending breakdown for the month in each category is
displayed in a pie chart with category spending information and progress bars beneath.

In pots the user can create up to 11 pots and transfer money in and out of pots and between pots. The 
user can set goals for their pots and delete their pots. 

In the accounts page there are multiple buttons with additional features. The user can generate a statements PDF for a given month.
The user can predict future spending where it will show the user a prediction of how much they will spend on the next transaction 
in each category and how many days until this will happen in terms of days since account creation.
The user can view the card details of their digital card. Also, the user can change their card details 
if they expect fraudulent activity and can freeze their account to prevent them from sending any 
transactions. The user can also view recent transactions where they can change the name of transaction, transaction category and 
whether to include it in the budget it was for.

## Features:
* Sign in and Sign up
* Reset password
* 2FA
* Email verification
* Home screen showing balance, overdraft and recent transactions
* Account page showing balance and overdraft
* Card details with option to generate random new details
* Transactions
* Transactions between two StuBank users
* Predicting future spending using machine learning
* Set budget for 8 categories
* Display budget and monthly spending information for all 8 categories
* View statements
* Light and dark mode
* Rounding up transactions and moved to savings pot
* Log out
* Change password
* Pot page which displays all users pots
* Create new pots
* Edit a goal of a pot and delete a pot
* Move money in and out of a pot
* Freeze account
* View transactions
* Edit transaction information

## Instructions

To create a new account, click the Sign Up button on the welcome page. Email must be a valid university email 
address (.ac.uk). You must have access to the entered university email address as you must be able to click a link sent
to the given email to verify the email address. This email is often sent to the Junk folder, so be sure to check there for it. 
The user will need to download the Google Authenticator app (download here: 
<https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2&hl=en_GB&gl=US>) to complete 
the sign up process and use 2FA when signing in. From the home page, any of the features described above can be used. 
To use the Predict Future Spending feature, a Python server connection must be established. Instructions on how to do 
this are in the Python README file. The account must also have enough transactional data for each category to make predictions.
(There must be a minimum of two transactions for a category to make a prediction for that category.)

All new accounts are set to have a balance of £0 and an available overdraft of £1000.
This means new accounts will be spending in overdraft unless they are sent money by another user. This can be done by creating another 
account and sending a transaction to the account number and sort code shown on the Card Details page (accessed through the Account page).
All StuBank users have a sort code of 11-22-33.

To access a Statements PDF file, go to your device's file storage and the PDF is saved at 
storage/emulated/0/Android/data/com.example.CSC2033_Team19_StuBank/Files.

## Development Information

This project was developed using Android Studio 4.1.1 in Java version 15.0.1. The app should run on any Android device running Android 8.1 or 
higher. This project uses Google Firebase.

This project has been tested on and emulated Pixel 3A, an emulated Pixel 4XL and a physical OnePlus 7T Pro.

## Authors
Megan Jinks, Karolis Zilius, Megan O'Doherty, Nathan Fenwick, Will Holmes

## License
Icons made by <https://www.flaticon.com/authors/freepik> title="Freepik">Freepik
from <https://www.flaticon.com/> title="Flaticon">www.flaticon.com>