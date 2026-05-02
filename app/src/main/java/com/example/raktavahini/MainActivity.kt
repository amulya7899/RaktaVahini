package com.example.raktavahini

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        val users = ArrayList<User>()
    }

    private var selectedMillis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
        val name = findViewById<EditText>(R.id.name)
        val blood = findViewById<EditText>(R.id.blood)
        val location = findViewById<EditText>(R.id.location)
        val dateField = findViewById<EditText>(R.id.lastDonationDate)
        val available = findViewById<SwitchMaterial>(R.id.available)

        val save = findViewById<Button>(R.id.saveBtn)
        val goSearch = findViewById<Button>(R.id.goSearch)

        // 📅 Date picker
        dateField.setOnClickListener {
            val calendar = Calendar.getInstance()

            val dialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, day)

                    selectedMillis = selectedDate.timeInMillis

                    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    dateField.setText(format.format(selectedDate.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            dialog.datePicker.maxDate = System.currentTimeMillis()
            dialog.show()
        }

        // 💾 Save button
        save.setOnClickListener {

            if (name.text.isEmpty() || blood.text.isEmpty() || location.text.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedMillis == 0L) {
                Toast.makeText(this, "Select date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(
                name.text.toString(),
                blood.text.toString(),
                location.text.toString(),
                selectedMillis,
                available.isChecked
            )

            users.add(user)

            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()

            showThankYouNotification(this)

            // Clear fields
            name.text.clear()
            blood.text.clear()
            location.text.clear()
            dateField.text.clear()
            available.isChecked = false
            selectedMillis = 0L
        }

        // 🔍 Go to search screen
        goSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    // 🔔 Notification
    private fun showThankYouNotification(activity: MainActivity) {

        val channelId = "donor_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Donor Notifications",
                NotificationManager.IMPORTANCE_HIGH   // 🔥 IMPORTANT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // must exist
            .setContentTitle("Thank You ❤️")
            .setContentText("You successfully registered as a donor!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)   // 🔥 IMPORTANT
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }
}