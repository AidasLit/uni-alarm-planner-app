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
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MapSelectActivity : AppCompatActivity() {

    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private var selectedLatLng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_select)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap

            enableMyLocation() // <-- pridėta

            getLastKnownLocationOrFallback()

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

    private fun getLastKnownLocationOrFallback() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13f))
            } else {
                // Fallback – Kaunas
                val kaunas = LatLng(54.8985, 23.9036)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(kaunas, 12f))
            }
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }
    }

    // Jei nori gauti leidimą runtime, gali pridėt override fun onRequestPermissionsResult()
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocationOrFallback()
        }
    }

}
