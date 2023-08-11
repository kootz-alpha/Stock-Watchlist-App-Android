# Alpha Trade<br>
It is a stock market watchlist application. User can add certain stocks listed in the NYSE(New York Stock Exchange) into their watchlist and track the current price of those stocks. User can view the daily price movement for the past 50 days for each of those stocks, user can see a brief description of the stock. User can also set reminders to check on a particular stock at a certain time in future.<br><br>

## Output<br>

![Splashscreen](img-github/0.png)<br>
Splash screen<br><br>

![loginpage](img-github/1.png)<br>
Login page<br><br>

![Watchlist page](img-github/2.png)<br>
Watchlist page<br><br>

![Add-stock page](img-github/4.png)<br>
Add-stock page<br><br>

![Stock details page](img-github/5.png)<br>
Stock details page<br><br>

![Navigation drawer](img-github/3.png)<br>
Navigation drawer. Community hub is planned to be a global-chat discuss section, it isn't implemented yet.<br><br>

___
<br>

## How to know which user is currently signed into the app in a device ?<br><br>
We will store login-data locally. For this project I used shared preference. Data is of the form {bool loggedIn, userID}.<br>
```kotlin
val loggedInSharedPreferences = getSharedPreferences("LoggedIn", Context.MODE_PRIVATE)

if (!loggedInSharedPreferences.contains("login")) {

    loggedInSharedPreferences.edit().putString("login", loginDefault["login"]).apply() // To create LoggedIn sharedPreference if it doesn't exist
}

val loggedInData = loggedInSharedPreferences.getString("login", "")!!.split(", ").toMutableList() // Data is stored as ", " separated string, so we convert it into a list.


// Set up a handler to post a runnable that will start the next activity after a delay
Handler().postDelayed({

    val startAppIntent: Intent

    if (loggedInData[0].toBoolean()) { // loggedInData[0] will be the boolean indicating whether someone is already logged in or not
        startAppIntent = Intent(this, HomePage::class.java)
        startAppIntent.putExtra("UserID", loggedInData[1])
    }
    else {
        startAppIntent = Intent(this, LoginPage::class.java)
    }

    // Get the ActivityOptionsCompat object for the transition animation
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
        this, logoImageView, ViewCompat.getTransitionName(logoImageView)!!
    )

    // Start the Login activity with the transition animation
    startActivity(startAppIntent, options.toBundle())
    finish()
}, TIME_OUT)
```
<br>

___


<br>

## How to login a user using Firebase Authentication ?<br><br>

```kotlin
var int = Intent(this,HomePage::class.java)

val user_id = uname.text.toString() // uname refers to the textview for username in login page
val password = pass.text.toString() // pass refers to the textview for password in login page

val auth = FirebaseAuth.getInstance() // Creating an instance of Firebase Authentication
auth.signInWithEmailAndPassword("$user_id@dummy.com", password) // Since this function allows login with email and password only, and as we have username instead of email. We pass in our username in the form of a dummy email
    .addOnCompleteListener { task -> // As signInWithEmailAndPassword is an asynchronous function, we will have to do all our next steps inside a call-back function
        if (task.isSuccessful) {
            // User authentication successful
            val user = auth.currentUser!!
            val uid = user.uid

            val loggedInSharedPreferences = getSharedPreferences("LoggedIn", Context.MODE_PRIVATE)
            val loggedInData = loggedInSharedPreferences.getString("login", "")!!.split(", ").toMutableList()
            loggedInData[0] = "true"
            loggedInData[1] = uid

            loggedInSharedPreferences.edit().putString("login", loggedInData.joinToString(", ")).apply()

            int.putExtra("UserID", uid)
            startActivity(int)
            finish()

        } else {
            // User authentication failed
            Toast.makeText(this, "Incorrect Id or Password", Toast.LENGTH_SHORT).show()
        }
    }
```

<br>

___


<br>

## How to create a new user in Firebase Firestore ?<br>

In Firestore a collection is a group of related documents, which is a single data record in a collection. In the case of this application we have a collection called "user_profiles" which holds user data of all users. A document in "user_profiles" would contain details about a particular user, like email, profile picture as a encoded string, name of the user, and the stocks in that user's watchlist.<br>

```kotlin
val auth = FirebaseAuth.getInstance()
val firestore = FirebaseFirestore.getInstance() // Creating an instance of Firebase Firestore
val userProfilesCollection = firestore.collection("user_profiles") // Creating/opening collection called "user_profiles"


auth.createUserWithEmailAndPassword("$user_id@dummy.com", password).addOnCompleteListener { task ->
    if (task.isSuccessful) {
        // User registration successful
        val user = auth.currentUser!!
        val uid = user.uid

        val userProfileData = hashMapOf( // data to be stored in the document
            "email" to user_email,
            "profile_picture" to encodedImage,
            "name" to full_name,
            "watchlist" to ""
        )

        userProfilesCollection.document(uid).set(userProfileData) // Setting value of the document uid in the collection
            .addOnSuccessListener {
                Toast.makeText(this , "SignUp successful" , Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
        // Perform any necessary operations with the user object
    } else {
        // User registration failed
        Toast.makeText(this , "SignUp failed!" , Toast.LENGTH_SHORT).show()
    }
}
```

<br>

___


<br>

## How to setup navigation drawer ?<br>

Layout file<br>
It should open with a DrawerLayout widget. Then add the layout for the main screen inside. Then add NavigationView widget to set the layout for the navigation drawer. Note that the NavigationView should be written only after the main screen layout otherwise it will mess up the touch functions.

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/drawerLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".HomePage">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/colorPrimaryDark"
        android:id="@+id/container">

    </FrameLayout>

    <com.google.android.material.navigation.NavigationView
        android:id="@+id/navigationView"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:background="#141414"
        android:layout_gravity="start">

        <LinearLayout
            xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:id="@+id/user_frame"
                android:background="@drawable/bg_purple"
                android:orientation="vertical">

                <com.google.android.material.imageview.ShapeableImageView
                    android:id="@+id/userImage"
                    android:layout_width="150dp"
                    android:layout_height="150dp"
                    android:src="@drawable/default_user"
                    android:layout_margin="10dp"
                    android:layout_gravity="center_horizontal"
                    app:shapeAppearanceOverlay="@style/circular"/>

                <TextView
                    android:id="@+id/nav_drawer_username"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="User Name"
                    android:textSize="30sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="10dp"
                    android:gravity="center"
                    android:layout_gravity="center_horizontal"/>
            </LinearLayout>

            <Button
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Add Stock to Watchlist"
                android:layout_marginHorizontal="20dp"
                android:layout_marginVertical="30dp"
                android:onClick="addStock"
                android:background="@drawable/bg_buttons"/>

            <Button
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Community Hub"
                android:layout_marginHorizontal="20dp"
                android:layout_marginVertical="30dp"
                android:onClick="communityHub"
                android:background="@drawable/bg_buttons"/>

        </LinearLayout>

    </com.google.android.material.navigation.NavigationView>



</androidx.drawerlayout.widget.DrawerLayout>

```

Activity file

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home_page)

    drawerLayout = findViewById(R.id.drawerLayout)
    drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) // This class is used to tie together the navigation drawer and the action bar. It allows the navigation drawer to be opened and closed by tapping the app's "hamburger" icon in the action bar.
    drawerLayout.addDrawerListener(drawerToggle) // This line adds the ActionBarDrawerToggle as a listener to the DrawerLayout. This enables the toggle to respond to drawer events, such as opening and closing.
    drawerToggle.syncState() // This method synchronizes the state of the toggle icon (hamburger icon) with the state of the drawer.


    supportActionBar?.setDisplayHomeAsUpEnabled(true) // This allows home button (hamburger) to also act as a back button when drawer is opened
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {

    if (drawerToggle.onOptionsItemSelected(item)) {
        return true
    }
}

```
