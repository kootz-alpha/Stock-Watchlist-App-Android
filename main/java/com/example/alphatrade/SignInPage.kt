package com.example.alphatrade

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInPage : AppCompatActivity() {

    lateinit var name: EditText
    lateinit var pass: EditText
    lateinit var userId: EditText
    lateinit var emailId: EditText
    lateinit var cameraSwitch: Switch
    lateinit var encodedImage: String
    lateinit var click_image: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private val pic_id = 742

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_page)

        name = findViewById(R.id.etName)
        pass = findViewById(R.id.etPass)
        userId = findViewById(R.id.etUName)
        emailId = findViewById(R.id.etEmail)
        cameraSwitch = findViewById(R.id.camera_switch)
        click_image = findViewById(R.id.click_image)

        sharedPreferences = getSharedPreferences("UserData" , Context.MODE_PRIVATE)

        cameraSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The switch is on
                val camera_intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                } else {
                    TODO("VERSION.SDK_INT < CUPCAKE")
                }
                // Start the activity with camera_intent, and request pic id
                startActivityForResult(camera_intent, pic_id)
            } else {
                // The switch is off
                val layoutParams = click_image.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.width = 0 // set width in pixels
                layoutParams.height = 0 // set height in pixels
                layoutParams.topMargin = 0 // set top margin in pixel
                layoutParams.bottomMargin = 0
                click_image.layoutParams = layoutParams

                saveImage(click_image)
            }
        }

    }

    fun signUp(v: View){

        val full_name = name.text.toString().trim()
        val password = pass.text.toString()
        val user_id = userId.text.toString().trim()
        val user_email = emailId.text.toString().trim()

        if (full_name != "" && password != "" && user_id != "" && encodedImage != "") {

            if (Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {

                val auth = FirebaseAuth.getInstance()
                val firestore = FirebaseFirestore.getInstance()
                val userProfilesCollection = firestore.collection("user_profiles")


                auth.createUserWithEmailAndPassword("$user_id@dummy.com", password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User registration successful
                        val user = auth.currentUser!!
                        val uid = user.uid

                        val userProfileData = hashMapOf(
                            "email" to user_email,
                            "profile_picture" to encodedImage,
                            "name" to full_name,
                            "watchlist" to ""
                        )

                        userProfilesCollection.document(uid).set(userProfileData)
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

                //sharedPreferences.edit().putString(user_name, "$password, $full_name").apply()
            }
            else {
                Toast.makeText(this , "Incorrect email-id!" , Toast.LENGTH_SHORT).show()
            }

        }
        else {
            Toast.makeText(this , "Fill the details carefully" , Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Match the request 'pic id with requestCode
        if (requestCode == pic_id) {
            val photo = data!!.extras!!["data"] as Bitmap?
            click_image.setImageBitmap(photo)

            val layoutParams = click_image.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.width = 350
            layoutParams.height = 350
            layoutParams.topMargin = 60
            layoutParams.bottomMargin = 40
            click_image.layoutParams = layoutParams

            saveImage(click_image)
        }
    }

    fun saveImage(imageView: ImageView){

        val baos = ByteArrayOutputStream()
        val bitmap = imageView.drawable.toBitmap()
        bitmap.compress(Bitmap.CompressFormat.PNG , 100,baos)
        encodedImage = Base64.encodeToString(baos.toByteArray() , Base64.DEFAULT)
    }
}