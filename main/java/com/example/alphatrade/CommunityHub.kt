package com.example.alphatrade

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EdgeEffect
import android.widget.EditText
import android.widget.LinearLayout
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class CommunityHub : AppCompatActivity() {

    private lateinit var customViewsContainer: LinearLayout
    private lateinit var messageBox: EditText
    private lateinit var chatSend: Button
    private lateinit var name: String
    private lateinit var encodedImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_hub)

        customViewsContainer = findViewById(R.id.chatBox)
        messageBox = findViewById(R.id.messageBox)
        chatSend = findViewById(R.id.chatSend)

        name = intent.getStringExtra("name")!!
        encodedImage = intent.getStringExtra("profile_picture")!!

        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                refreshCustomViews()
                handler.postDelayed(this, 30000) // Run every 30 seconds
            }
        }
        handler.postDelayed(runnable, 100) // Initial delay before the first run
    }

    fun refreshCustomViews() {

        val collectionRef = FirebaseFirestore.getInstance().collection("community_hub")
        customViewsContainer.removeAllViews()

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // width
            LinearLayout.LayoutParams.WRAP_CONTENT // height
        )
        layoutParams.setMargins(20, 30, 20, 30)

        collectionRef.orderBy(FieldPath.documentId())
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Enqueue the documents in the desired order
                for (documentSnapshot in querySnapshot.documents) {

                    val data = documentSnapshot.data!!
                    val name = data["name"].toString()
                    val profile_picture = data["profile_picture"].toString()
                    val message = data["message"].toString()

                    val customView = CustomViewChat(this, profile_picture, message, name)
                    customViewsContainer.addView(customView, layoutParams)
                }
            }
            .addOnFailureListener { e ->
                // Error occurred while retrieving the documents
            }
    }

    fun sendMessage() {

        val message = messageBox.text.toString()
        messageBox.setText("")

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // width
            LinearLayout.LayoutParams.WRAP_CONTENT // height
        )
        layoutParams.setMargins(20, 30, 20, 30)

        val customView = CustomViewChat(this, encodedImage, message, name)
        customViewsContainer.addView(customView, layoutParams)

    }
}