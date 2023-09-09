package com.cj1_project.googlesignin

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class WorkoutActivity : AppCompatActivity() {

    private lateinit var workoutGRV: GridView
    lateinit var workoutList: List<GridViewModal>
    private var speed : Float = 0.0f
    private var distance : Float = 0.0f
    private var time : Float = 0.0f
    private var calorie : Float = 0.0f

    private lateinit var locationManager: GeoLocation
    private val LOCATION_PERMISSION_CODE = 1000
    private var locationTrackingRequested = false
    private var kmphSpeed = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // --------------------------------------------------------------
        // initializing variables of grid view with their ids.
        workoutGRV = findViewById<GridView>(R.id.idGRV)
        workoutList = ArrayList<GridViewModal>()

        workoutList = workoutList + GridViewModal("Speed", speed.toString(),R.drawable.speed)
        workoutList = workoutList + GridViewModal("Distance",distance.toString(), R.drawable.distance)
        workoutList = workoutList + GridViewModal("Time","00:00:00", R.drawable.clock)
        workoutList = workoutList + GridViewModal("Calories",calorie.toString(), R.drawable.calories)

        val courseAdapter = GridRVAdapter(workoutList = workoutList, this@WorkoutActivity)
        workoutGRV.adapter = courseAdapter
        workoutGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ -> }
        // --------------------------------------------------------------

        //location
        // Create GeoLocationManager
        locationManager = GeoLocation(this)

        // Register button click listeners
        findViewById<Button>(R.id.startButton).setOnClickListener {

            val permissionGranted = requestLocationPermission()
            println("reached 1")
            if (permissionGranted) {
                locationManager.startLocationTracking(locationCallback)
                locationTrackingRequested = true
                println("reached 2")
                findViewById<TextView>(R.id.textWorkout).text = "Workout Started"
            }
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            locationManager.stopLocationTracking()
            locationTrackingRequested = false
            findViewById<TextView>(R.id.textWorkout).text = "Workout Stopped"
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //locationResult ?: return
            for (location in locationResult.locations){
                // Update UI
                distance = location.latitude.toFloat()
                calorie = location.longitude.toFloat()
                println(location.latitude.toString())
                if (location.hasSpeed()){
                    kmphSpeed = (location.speed * 3.6).toFloat()
                    speed = kmphSpeed
                }
                else
                    speed = 0.0F
            }
        }
    }

    private fun requestLocationPermission(): Boolean {
        var permissionGranted = false// If system os is Marshmallow or Above, we need to request runtime permission
        val cameraPermissionNotGranted = ContextCompat.checkSelfPermission(
            this,
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
        return permissionGranted
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        locationManager.stopLocationTracking()
        findViewById<TextView>(R.id.textWorkout).text = "Workout hahahaha"
    }

    override fun onResume() {
        super.onResume()
        //running = true
        if  (locationTrackingRequested) {
            locationManager.startLocationTracking(locationCallback)
            findViewById<TextView>(R.id.textWorkout).text = "Workout Started"
        }

    }

}