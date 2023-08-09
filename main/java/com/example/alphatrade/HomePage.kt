package com.example.alphatrade

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class HomePage : AppCompatActivity() {

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private  lateinit var drawerLayout: DrawerLayout
    var apiKeyFinnub: String = ""
    var apiKeyAlphaVantage: String = ""
    lateinit var userID: String
    private var userData: DocumentSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        drawerLayout = findViewById(R.id.drawerLayout)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.actionbar)))

        val navDrawerUsername = findViewById<TextView>(R.id.nav_drawer_username)
        val imageView = findViewById<ImageView>(R.id.userImage)

        val firestore = FirebaseFirestore.getInstance()

        userID = intent.getStringExtra("UserID")!!
        var documentRef = firestore.collection("user_profiles").document(userID)

        documentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    // Document exists, retrieve the value of a specific key
                    userData = documentSnapshot

                    navDrawerUsername.text = userData?.getString("name")
                    loadImage(imageView, this, userData?.getString("profile_picture"))

                }
            }
            .addOnFailureListener { e ->
                // Error occurred while retrieving the document
                Log.e("Firestore", "Error retrieving document", e)
            }

        val apiDocRef = firestore.collection("api_keys").document("api_keys")
        apiDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    // Document exists, retrieve the value of a specific key
                    apiKeyFinnub = documentSnapshot.getString("finnub")!!
                    apiKeyAlphaVantage = documentSnapshot.getString("alphaVantage")!!
                    Log.d("FinnubAPI", "$apiKeyFinnub\n$apiKeyAlphaVantage")

                    val watchListFragment = WatchListPage()

                    // Get the FragmentManager and start a transaction
                    val fragmentManager = supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()

                    // Replace the container layout with the SignInFragment
                    fragmentTransaction.replace(R.id.container, watchListFragment)

                    // Add the transaction to the back stack
                    fragmentTransaction.addToBackStack(null)

                    // Commit the transaction
                    fragmentTransaction.commit()
                }
            }
            .addOnFailureListener { e ->
                // Error occurred while retrieving the document
                Log.e("FirestoreAPI", "Error retrieving API document", e)
            }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.action_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        if (item.itemId == R.id.btn_sign_out) {

            val loggedInSharedPreferences = getSharedPreferences("LoggedIn", Context.MODE_PRIVATE)
            val loggedInList = loggedInSharedPreferences.getString("login", "")!!.split(", ").toMutableList()
            loggedInList[0] = "false"

            loggedInSharedPreferences.edit().putString("login", loggedInList.joinToString(", ")).apply()
            val restartIntent = Intent(this, SplashScreen::class.java)

            restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(restartIntent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    fun addStock(view: View) {

        val addStockFragment = AddStockPage()

        // Get the FragmentManager and start a transaction
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.container, addStockFragment)

        // Add the transaction to the back stack
        fragmentTransaction.addToBackStack(null)

        // Commit the transaction
        fragmentTransaction.commit()
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun loadImage(imageView: ImageView , context: Context, encodedImage: String?){

        if (encodedImage != null) {

            val imageBytes = Base64.decode(encodedImage , Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes , 0 , imageBytes.size)
            imageView.setImageBitmap(decodedImage)
        }
    }

    fun communityHub(view: View) {

        val intent = Intent(this, CommunityHub::class.java)

        intent.putExtra("profile_picture", userData?.getString("profile_picture"))
        intent.putExtra("name", userData?.getString("name"))
        startActivity(intent)
    }

}