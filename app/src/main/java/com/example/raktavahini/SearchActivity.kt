package com.example.raktavahini

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val groupEditText = findViewById<EditText>(R.id.group)
        val searchBtn = findViewById<Button>(R.id.searchBtn)
        val resultText = findViewById<TextView>(R.id.result)

        searchBtn.setOnClickListener {

            val inputGroup = groupEditText.text.toString().trim()

            if (inputGroup.isEmpty()) {
                resultText.text = "Enter blood group"
                return@setOnClickListener
            }

            val filtered = MainActivity.users.filter {
                it.blood.equals(inputGroup, ignoreCase = true) &&
                        it.available &&
                        isEligible(it.date)
            }

            if (filtered.isEmpty()) {
                resultText.text = "No eligible donors found"
            } else {
                val display = filtered.joinToString("\n\n") {
                    "Name: ${it.name}\nLocation: ${it.location}"
                }
                resultText.text = display
            }
        }
    }

    // 📞 Optional call simulation
    private fun callDonor() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:1234567890")
        startActivity(intent)
    }
}