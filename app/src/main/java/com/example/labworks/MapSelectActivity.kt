package com.example.labworks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapSelectActivity : AppCompatActivity() {

    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private var selectedLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_select)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            val defaultLocation = LatLng(54.6872, 25.2797)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

            map.setOnMapClickListener { latLng ->
                marker?.remove()
                marker = map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
                selectedLatLng = latLng

            }
        }

        findViewById<FloatingActionButton>(R.id.save_location_button).setOnClickListener {
            if (selectedLatLng != null) {
                val resultIntent = Intent().apply {
                    putExtra("lat", selectedLatLng!!.latitude)
                    putExtra("lng", selectedLatLng!!.longitude)
                }
                Log.d("MapSelectActivity", "Selected LatLng: $selectedLatLng")

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please tap on the map to select a location.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
