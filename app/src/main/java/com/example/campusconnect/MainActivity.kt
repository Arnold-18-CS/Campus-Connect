package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.campusconnect.ui.EntryActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, EntryActivity::class.java))
        finish() // Close MainActivity after launching EntryActivity
    }
}