package com.example.raktavahini

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val groupEditText = findViewById<EditText>(R.id.group)
        val searchBtn = findViewById<Button>(R.id.searchBtn)
        val listView = findViewById<ListView>(R.id.listView)

        searchBtn.setOnClickListener {

            val inputGroup = groupEditText.text.toString().trim().uppercase()

            if (inputGroup.isEmpty()) {
                Toast.makeText(this, "Enter blood group", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val filtered = MainActivity.users.filter {
                it.blood == inputGroup &&
                        it.available &&
                        isEligible(it.date)
            }

            if (filtered.isEmpty()) {
                Toast.makeText(this, "No eligible donors found", Toast.LENGTH_SHORT).show()
            } else {
                listView.adapter = DonorAdapter(filtered)
            }
        }
    }

    inner class DonorAdapter(private val donors: List<User>) : BaseAdapter() {

        override fun getCount(): Int = donors.size
        override fun getItem(position: Int): Any = donors[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val view = LayoutInflater.from(this@SearchActivity)
                .inflate(R.layout.item_donor, parent, false)

            val donor = donors[position]

            val nameText = view.findViewById<TextView>(R.id.nameText)
            val locationText = view.findViewById<TextView>(R.id.locationText)
            val callBtn = view.findViewById<Button>(R.id.callBtn)
            val donateBtn = view.findViewById<Button>(R.id.donateBtnItem)

            nameText.text = donor.name
            locationText.text = donor.location

            callBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${donor.phone}")
                startActivity(intent)
            }

            donateBtn.setOnClickListener {
                donor.date = System.currentTimeMillis()
                donor.available = false
                notifyDataSetChanged()
                Toast.makeText(this@SearchActivity, "Donation updated", Toast.LENGTH_SHORT).show()
            }

            return view
        }
    }
}