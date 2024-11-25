package com.example.campusconnect.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.campusconnect.R
import com.example.campusconnect.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profileImageView: ImageView
    private var profileImageUri: Uri? = null

    // Register Activity Result Launchers
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Handle the selected image URI (set it to ImageView, upload it, etc.)
            this.profileImageUri = it
            profileImageView.setImageURI(it)
        }
    }

    private val permissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            pickImageLauncher.launch("image/*") // Open the image picker
        } else {
            Toast.makeText(this, "Permission denied. Unable to select profile image.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        profileImageView = findViewById(R.id.profileImageView)

        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val emailEditText: EditText = findViewById(R.id.signupEmailEditText)
        val passwordEditText: EditText = findViewById(R.id.signupPasswordEditText)
        val confirmPasswordEditText: EditText = findViewById(R.id.signupConfirmPasswordEditText)
        val signupButton: Button = findViewById(R.id.signupButton)
        val selectImageButton: Button = findViewById(R.id.button)

        // Handle image selection
        selectImageButton.setOnClickListener {
            // Check for the permission based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {  // Android 14+
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED) {
                    pickImageLauncher.launch("image/*")  // Launch the image picker
                } else {
                    permissionRequestLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)  // Request permission
                }
            } else {
                pickImageLauncher.launch("image/*")  // For Android versions below Android 14
            }
        }

        // Handle sign up button click
        signupButton.setOnClickListener {
            // Retrieve user input
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Validate inputs
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // Proceed with registration
                registerUser(email, password, name)
            }
        }
    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Successfully registered user
                    val userId = auth.currentUser?.uid
                    val user = User(name, email, profileImageUri.toString())

                    val database = FirebaseDatabase.getInstance().getReference("users")
                    userId?.let {
                        database.child(it).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                                    // Optionally navigate to login or home screen
                                } else {
                                    Toast.makeText(this, "Error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                } else {
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Navigate to login activity
    fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
