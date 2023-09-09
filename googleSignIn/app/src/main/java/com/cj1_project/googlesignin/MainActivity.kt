package com.cj1_project.googlesignin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

//https://www.youtube.com/watch?v=H_maapn4Q3Q
//https://medium.com/swlh/google-login-and-logout-in-android-with-firebase-kotlin-implementation-73cf6a5a989e
//https://medium.com/firebase-tips-tricks/how-to-get-the-sha-1-fingerprint-certificate-for-debug-mode-in-android-studio-c9df7ae2401b


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    //location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val fab = findViewById<Button>(R.id.fab)
        fab.setOnClickListener {
            /*Snackbar.make(view,"Here's SnackBar",Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()*/
            val intent = Intent(this,WorkoutActivity::class.java)
            startActivity(intent)
            finish()
        }

        val fab1 = findViewById<Button>(R.id.fab1)
        fab1.setOnClickListener {
            /*Snackbar.make(view,"Here's SnackBar1",Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()*/
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    // Add a marker at the user's current location
                    mMap.addMarker(MarkerOptions().position(currentLatLng).title("My Location"))

                    // Move the camera to the user's current location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, re-run onMapReady
                onMapReady(mMap)
            }
        }
    }
}