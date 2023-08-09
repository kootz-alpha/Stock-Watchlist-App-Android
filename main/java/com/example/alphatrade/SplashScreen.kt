package com.example.alphatrade

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat

class SplashScreen : AppCompatActivity() {

    private var TIME_OUT = 5000L
    private val loginDefault = DefaultData().loginData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val logoImageView = findViewById<ImageView>(R.id.logoImageView)

        // Create a transition name for the logo ImageView
        ViewCompat.setTransitionName(logoImageView, "logo")

        val loggedInSharedPreferences = getSharedPreferences("LoggedIn", Context.MODE_PRIVATE)

        if (!loggedInSharedPreferences.contains("login")) {

            loggedInSharedPreferences.edit().putString("login", loginDefault["login"]).apply()
        }

        val loggedInData = loggedInSharedPreferences.getString("login", "")!!.split(", ").toMutableList()


        // Set up a handler to post a runnable that will start the next activity after a delay
        Handler().postDelayed({

            val startAppIntent: Intent

            if (loggedInData[0].toBoolean()) {
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
    }
}