package com.example.campusconnect.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campusconnect.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(){

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

       auth = FirebaseAuth.getInstance()

//        // Check if the user is already logged in
//        if (auth.currentUser != null) {
//            // If logged in, navigate to the ProfileActivity directly
//            val intent = Intent(this, ProfileActivity::class.java)
//            startActivity(intent)
//            finish()  // Close LoginActivity so the user can't go back to it
//        }

       val emailEditText: EditText = findViewById(R.id.emailEditText)
       val passwordEditText: EditText = findViewById(R.id.passwordEditText)
       val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
    
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Successfully logged in
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    // Navigate to next screen (e.g., MainActivity)
                    val intent = Intent(this, CreateEventActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }

    fun goToSignUp(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }


}
