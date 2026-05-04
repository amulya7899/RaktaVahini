package com.example.raktavahini

data class User(
    val name: String,
    val blood: String,
    val location: String,
    var date: Long,
    var available: Boolean,
    val phone: String,
    val latitude: Double,
    val longitude: Double
)