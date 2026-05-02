package com.example.raktavahini

fun isEligible(lastDonationMillis: Long): Boolean {
    val currentTime = System.currentTimeMillis()
    val diff = currentTime - lastDonationMillis
    val days = diff / (1000 * 60 * 60 * 24)
    return days >= 90
}