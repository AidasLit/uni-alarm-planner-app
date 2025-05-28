package com.example.labworks

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Camera
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var selectedMarker: Marker? = null

    var onLocationSelected: ((LatLng) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_view) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        view.findViewById<android.widget.ImageButton>(R.id.back_button)?.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableUserLocation()

        val lat = arguments?.getDouble(ARG_LAT)
        val lng = arguments?.getDouble(ARG_LNG)

        if (lat != null && lng != null) {
            val location = LatLng(lat, lng)
            selectedMarker?.remove()
            selectedMarker = map.addMarker(MarkerOptions().position(location).title("Saved Location"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }


    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            enableUserLocation()
        }
    }

    companion object {
        private const val ARG_LAT = "lat"
        private const val ARG_LNG = "lng"

        fun newInstance(lat: Double, lng: Double): MapFragment {
            val fragment = MapFragment()
            val args = Bundle().apply {
                putDouble(ARG_LAT, lat)
                putDouble(ARG_LNG, lng)
            }
            fragment.arguments = args
            return fragment
        }
    }

}
