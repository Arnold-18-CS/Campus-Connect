package com.example.campusconnect.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campusconnect.R
import com.example.campusconnect.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var logoutButton: Button
    private lateinit var auth: FirebaseAuth
    private var profileImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        auth = FirebaseAuth.getInstance()
//        if (auth.currentUser == null) {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//            return
//        }
        Toast.makeText(this@ProfileActivity, "User not logged in", Toast.LENGTH_SHORT).show()

        setContentView(R.layout.activity_profile)

        // Initialize views
        profileImageView = findViewById(R.id.profileImageView)
        nameEditText = findViewById(R.id.profileNameTextView)
        emailEditText = findViewById(R.id.profileEmailTextView)
        logoutButton = findViewById(R.id.logoutButton)
        auth = FirebaseAuth.getInstance()

        // Load the current user's profile data
        loadProfileData()
        Log.d("ProfileActivity", "Current user UID: ${auth.currentUser?.uid}")


        // Set the logout button click listener
        logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    // Load the current user data
    private fun loadProfileData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val database = FirebaseDatabase.getInstance().getReference("users").child(userId)

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            // Display user details
                            nameEditText.setText(user.name)  // Use setText for EditText
                            emailEditText.setText(user.email)  // Use setText for EditText

                            // Load the profile image using Picasso (or any image loading library)
                            if (!user.profileImageUri.isNullOrEmpty()) {
                                Picasso.get().load(user.profileImageUri).into(profileImageView)
                            }
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Logout the user
    private fun logoutUser() {
        // Sign out the user from FirebaseAuth
        auth.signOut()

        // Redirect to login screen
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
