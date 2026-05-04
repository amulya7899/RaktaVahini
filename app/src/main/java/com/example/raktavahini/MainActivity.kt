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

        val name = findViewById<EditText>(R.id.name)
        val phone = findViewById<EditText>(R.id.phone)
        val blood = findViewById<EditText>(R.id.blood)
        val location = findViewById<EditText>(R.id.location)
        val dateField = findViewById<EditText>(R.id.lastDonationDate)
        val available = findViewById<Switch>(R.id.available)

        val save = findViewById<Button>(R.id.saveBtn)
        val goSearch = findViewById<Button>(R.id.goSearch)


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


        save.setOnClickListener {

            if (name.text.isEmpty() || blood.text.isEmpty() ||
                location.text.isEmpty() || phone.text.isEmpty()
            ) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedMillis == 0L) {
                Toast.makeText(this, "Select donation date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val lat = 12.9716
            val lng = 77.5946

            val user = User(
                name.text.toString().trim(),
                blood.text.toString().trim().uppercase(),
                location.text.toString().trim(),
                selectedMillis,
                available.isChecked,
                phone.text.toString().trim(),
                lat,
                lng
            )

            users.add(user)

            Toast.makeText(this, "Thank you ❤️", Toast.LENGTH_LONG).show()

            showNotification()


            name.text.clear()
            blood.text.clear()
            location.text.clear()
            phone.text.clear()
            dateField.text.clear()
            available.isChecked = false
            selectedMillis = 0L
        }

        goSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun showNotification() {
        val channelId = "donor_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Donor Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Thank You ❤️")
            .setContentText("You registered as a donor!")
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }
}