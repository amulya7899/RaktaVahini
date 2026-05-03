package com.example.raktavahini

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var groupEditText: EditText
    private lateinit var listView: ListView


    private val currentLat = 12.9716
    private val currentLng = 77.5946

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        groupEditText = findViewById(R.id.group)
        val searchBtn = findViewById<Button>(R.id.searchBtn)
        listView = findViewById(R.id.listView)

        searchBtn.setOnClickListener {

            val inputGroup = groupEditText.text.toString().trim().uppercase()

            val filtered = MainActivity.users.filter {

                val result = FloatArray(1)

                Location.distanceBetween(
                    currentLat, currentLng,
                    it.latitude, it.longitude,
                    result
                )

                val distanceKm = result[0] / 1000

                it.blood == inputGroup &&
                        it.available &&
                        isEligible(it.date) &&
                        distanceKm <= 10
            }

            if (filtered.isEmpty()) {
                Toast.makeText(this, "No donors nearby", Toast.LENGTH_SHORT).show()
                listView.adapter = null
            } else {
                listView.adapter = DonorAdapter(filtered)
            }
        }
    }

    inner class DonorAdapter(private var donors: List<User>) : BaseAdapter() {

        override fun getCount() = donors.size
        override fun getItem(position: Int) = donors[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val view = convertView ?: LayoutInflater.from(this@SearchActivity)
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

                val updatedUser = donor.copy(date = System.currentTimeMillis())

                MainActivity.users.remove(donor)
                MainActivity.users.add(updatedUser)

                Toast.makeText(this@SearchActivity, "Donation recorded!", Toast.LENGTH_SHORT).show()


                val inputGroup = groupEditText.text.toString().trim().uppercase()

                donors = MainActivity.users.filter {
                    it.blood == inputGroup &&
                            it.available &&
                            isEligible(it.date)
                }

                notifyDataSetChanged()
            }

            return view
        }
    }
}