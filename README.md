# HelloAndroid
Tutorial for Android development

Hello Android
Simple Sign-up, Sign-in application using a local Database to which we are performing write and read operations using an ORM layer. ORM stands for Object-Relational Mapping and it is a technique which removes the need to write SQL (database) code. The ORM tool does that for us. This app uses an ORM called ORMLite which is sort of industry standard for Android, although it is a open source third party tool (i.e. Google has not developed themselves).

The app consists of three parts, those are called "Activities" which is the main part in Android development. Each Activity has at least one view associated with it (but can have multiple, which is called "Fragments", but we are not going to work with that today).

This app consists of the following Activities:
* LoginActivity
* LoginHistoryActivity
* SignUpActivity

(As you can see, a class which is an Activity should always have the suffix "Activity", this is an important naming convention. Although it is not necessary for the app to work. But following standards in programming is important so that it is easier for other programmers to understand your code. This is especially important if you are working as a consultant ;) )

When the user has logged in, a view (LoginHistoryActivity) with a welcome message showing the email she used to login with is shown. Besides that a list with the date and time for the five latest logins are shown.
Each Activity has its own "life cycle". By that we mean that it is created/started, paused, resumed, stopped, restarted and recreated. The Android operating system is responsible for the life cycle of the Activity and we as developers gets notified about the state changes and can perform the actions we want for each state change. For example, if the user is in the Activity (view) A and presses the home button, which takes the user out of the app, then Activity A is paused. The Android operating system will then call the method in the class "Activity A" called "onPause" in which we can put code/logic.
Let's say that the app in question is a game and that A holds some information about the game's state. Then we want to save the game state in the method "onPause", so that no important state info is lost for the user, when the user returns to the app (Activity A).

Each Activity in the app inherits from the superclass "Activity". Each Activity has a "Layout" file, of type .XML associated with it. The view (i.e. Layout file) is loaded in the Activity Life Cycle method called "onCreate", by calling the method setContentView(R.layout.activity_login); (in the case of the LoginActivity)

Your activity class can retrieve the (sub)views/widgets (such as buttons and labels) created/declared it's associated Layout file by calling the findViewById method, so that you can interact with those views/widgets programmatically. 

YOUR MISSION - Improve this app!

TASKS:
1. When signing up the user only needs to input the password field once
2. Change colors of the background and or buttons. You can do this in three different ways, of which two relates to each other.
..1. Programmatically, with Java code in the activity <---- NOT RECOMMENDED
..2. In the layout file
....1. Using the "Text" tab, showing "RAW" XML <---- RECOMMENDED (but old school)
....2. Using the Design tab <---- NOT RECOMMENDED

2) In the LoginActivity, if the user tries to login with an email address that does not exist in the database, the error message "No user with that email" is being presented as an error on the email "field". This is a very informative message for the user, so that she knows that it was the email she entered that was incorrect and not the password that was incorrectly typed. However, by showing this message any user can type in any email and will get feedback from our app whether that user has an account at this service or not. Oh no! This is not good!

Your task is to anonymize our service and not give so informative feedback. You will do this by not using the specific error messages about email or password at all. The error messages are being presented by using the method on the input fields (of class EditText) called "setError". We don't want to use this at all. Instead let's create a new UI element of type TextView, which is used to present text. Set the text of this TextView to some generic error message, that does not reveal information whether the email or the password was wrong. Set the "visibility" of this element to "gone" and set it to "visible" when you want to show the error message.

ADVANCED
1) When Signed in and when the LoginHistoryActivity is presented the email address is being shown. It would be nicer to show your name.
Add two new fields to the sign up page
Firstname
Lastname

(TIP: create/declare those views in the Layout file, and then retrieve them in the Activity using findViewById (in the onCreate method) so that you can interact with them programmatically)

You need to modify the User class, adding the two new fields. You also need to change the two Parcelable methods in the bottom.

