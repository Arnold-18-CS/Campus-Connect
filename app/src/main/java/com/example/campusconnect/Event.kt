package com.example.campusconnect

data class Event(
    val title: String,
    val description: String,
    val date: String,
    val rsvpLink: String,
    val userId: String,
    val eventPosterUri: String) {

}
