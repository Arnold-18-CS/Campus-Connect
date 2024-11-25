package com.example.campusconnect.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campusconnect.Event
import com.example.campusconnect.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var eventTitleEditText: EditText
    private lateinit var eventDescriptionEditText: EditText
    private lateinit var eventDateEditText: EditText
    private lateinit var eventRsvpLinkEditText: EditText
    private lateinit var eventPosterImageView: ImageView
    private lateinit var createEventButton: Button
    private lateinit var selectPosterButton: Button
    private var eventPosterUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        auth = FirebaseAuth.getInstance()

        // Initialize views
        eventTitleEditText = findViewById(R.id.eventTitleEditText)
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText)
        eventDateEditText = findViewById(R.id.eventDateEditText)
        eventRsvpLinkEditText = findViewById(R.id.eventRsvpLinkEditText)
        eventPosterImageView = findViewById(R.id.eventPosterImageView)
        createEventButton = findViewById(R.id.createEventButton)
        selectPosterButton = findViewById(R.id.selectPosterButton)

        // Select Event Poster Image
        selectPosterButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        // Set date picker for event date
        eventDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val date = "$year-${month + 1}-$dayOfMonth"
                    eventDateEditText.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Create event on button click
        createEventButton.setOnClickListener {
            val title = eventTitleEditText.text.toString().trim()
            val description = eventDescriptionEditText.text.toString().trim()
            val date = eventDateEditText.text.toString().trim()
            val rsvpLink = eventRsvpLinkEditText.text.toString().trim()

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(date) || TextUtils.isEmpty(rsvpLink)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                createEvent(title, description, date, rsvpLink)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            eventPosterUri = data.data
            Picasso.get().load(eventPosterUri).into(eventPosterImageView)
            eventPosterImageView.visibility = ImageView.VISIBLE
        }
    }

    private fun createEvent(title: String, description: String, date: String, rsvpLink: String) {
        val userId = auth.currentUser?.uid
        val eventId = FirebaseDatabase.getInstance().getReference("events").push().key

        if (userId != null && eventId != null) {
            val event = Event(title, description, date, rsvpLink, userId, eventPosterUri.toString())

            val database = FirebaseDatabase.getInstance().getReference("events")
            database.child(eventId).setValue(event)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Event Created Successfully!", Toast.LENGTH_SHORT).show()
                        finish()  // Optionally finish the activity to return to the previous screen
                    } else {
                        Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
