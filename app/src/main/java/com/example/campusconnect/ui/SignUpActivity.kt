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
import com.example.campusconnect.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val emailEditText: EditText = findViewById(R.id.signupEmailEditText)
        val passwordEditText: EditText = findViewById(R.id.signupPasswordEditText)
        val confirmPasswordEditText: EditText = findViewById(R.id.signupConfirmPasswordEditText)
        val signupButton: Button = findViewById(R.id.signupButton)

        signupButton.setOnClickListener{
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Check if any field is empty
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
            // Check if passwords match
            else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
            else {
                registerUser(email, password, name)
            }
        }
    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful) {
                    // Successfully registered user
                    val userId = auth.currentUser?.uid
                    val user = User(name, email)

                    val database = FirebaseDatabase.getInstance().getReference("users")
                    userId?.let { it ->
                        database.child(it).setValue(user)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    // User data saved successfully
                                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                                    // Proceed to next activity
                                    } else {
                                        // Registration failed
                                        Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                } else {
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }

            }
    }

    fun goToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}

