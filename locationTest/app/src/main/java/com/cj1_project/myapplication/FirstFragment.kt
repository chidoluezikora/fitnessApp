package com.cj1_project.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.cj1_project.myapplication.databinding.FragmentFirstBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.mikhaellopez.circularprogressbar.CircularProgressBar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() /*, SensorEventListener*/ {

    private var _binding: FragmentFirstBinding? = null

    // Register text views
    private lateinit var latitudeTextView : TextView
    private lateinit var longitudeTextView: TextView
    private lateinit var speedTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var locationManager: GeoLocation
    private var locationAndSpeedArr : MutableList<Array<String>> = mutableListOf();
    private lateinit var workoutReference : DatabaseReference

    private val LOCATION_PERMISSION_CODE = 1000

    private var locationTrackingRequested = false

    private var kmphSpeed = 0f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Create GeoLocationManager
        locationManager = GeoLocation(activity as Context)
        workoutReference = FirebaseDatabase.getInstance().getReference("Workout")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //locationResult ?: return
            for (location in locationResult.locations){
                // Update UI
                latitudeTextView.text = location.latitude.toString()
                longitudeTextView.text = location.longitude.toString()
                println(location.latitude.toString())
                if (location.hasSpeed()){
                    kmphSpeed = (location.speed * 3.6).toFloat()
                    speedTextView.text = kmphSpeed.toString()
                }
                else
                    speedTextView.text = "0.0"
                val newAccRecord = arrayOf(location.latitude.toString(), location.longitude.toString(), kmphSpeed.toString());
                locationAndSpeedArr.add(newAccRecord);
            }
        }
    }

    private fun requestLocationPermission(): Boolean {
        var permissionGranted = false// If system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val cameraPermissionNotGranted = ContextCompat.checkSelfPermission(
                activity as Context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
            if (cameraPermissionNotGranted){
                val permission = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)  // Display permission dialog
                requestPermissions(permission, LOCATION_PERMISSION_CODE) //can be replaced by toast
            }
            else{
                // Permission already granted
                permissionGranted = true
            }
        }
        else{
            // Android version earlier than M -> no need to request permission
            permissionGranted = true
        }
        return permissionGranted
    }

    // Handle Allow or Deny response from the permission dialog
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode === LOCATION_PERMISSION_CODE) {
            if (grantResults.size === 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED){
                // Permission was granted
                locationManager.startLocationTracking(locationCallback)
                statusTextView.text = getString(R.string.startedKT)
            }
            else{
                // Permission was denied
                showAlert("Location permission was denied. Unable to track location.")
            }
        }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(activity as Context)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        locationManager.stopLocationTracking()
        statusTextView.text = getString(R.string.stoppedKT)
        val workoutId = workoutReference.push().key!!
        val userId = "testUserExample"
        val workout = WorkoutModel(workoutId, locationAndSpeedArr, userId)
        workoutReference.child(workoutId).setValue(workout)
            .addOnCompleteListener {
                Toast.makeText(activity as Context, "Workout recorded successfully", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { err ->
                Toast.makeText(activity as Context, "Error recording workout: ${err.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onResume() {
        super.onResume()

        //running = true

        if  (locationTrackingRequested) {
            locationManager.startLocationTracking(locationCallback)
            statusTextView.text = getString(R.string.startedKT)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Register text views
        latitudeTextView = view.findViewById(R.id.textview_latitude)
        longitudeTextView = view.findViewById(R.id.textview_longitude)
        statusTextView = view.findViewById(R.id.textview_status)
        speedTextView = view.findViewById(R.id.textview_speed)


        // Register button click listeners
        view.findViewById<Button>(R.id.button_start_location_scan).setOnClickListener {

            val permissionGranted = requestLocationPermission()
            if (permissionGranted) {
                locationManager.startLocationTracking(locationCallback)
                locationTrackingRequested = true
                statusTextView.text = getString(R.string.startedKT)
            }
        }

        view.findViewById<Button>(R.id.button_stop_location_scan).setOnClickListener {
            locationManager.stopLocationTracking()
            locationTrackingRequested = false
            statusTextView.text = getString(R.string.stoppedKT)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}