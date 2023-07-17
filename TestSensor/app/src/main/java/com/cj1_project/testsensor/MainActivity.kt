package com.cj1_project.testsensor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mSensorManager: SensorManager
    private var mAccelerometer : Sensor ?= null
    private var resume = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) // we exclude gravity while calculating speed

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val button: Button = findViewById(R.id.gps)
        button.setOnClickListener {
            getLocation()
        }

        val button1: Button = findViewById(R.id.start)
        button1.setOnClickListener {
            resumeReading()
        }

        val button2: Button = findViewById(R.id.stop)
        button2.setOnClickListener {
            pauseReading()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && resume) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val accList = mutableListOf(event.values[0],event.values[1],event.values[2])
                findViewById<TextView>(R.id.values).text =
                    accList.toString() //The accelerometer returns 3 values, for the 3 physical dimensions. For now, let’s only read the value along the x-axis.
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    private fun resumeReading() {
        this.resume = true
    }

    private fun pauseReading() {
        this.resume = false
    }

    private fun getLocation() {
        /*fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                "Current location is \n" + "lat : ${location?.latitude}\n" +
                        "long : ${location?.longitude}\n" + "fetched at ${System.currentTimeMillis()}"
            }*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                if (requestCode == locationPermissionCode) {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnSuccessListener {
                location: Location? ->
            findViewById<TextView>(R.id.location).text = "Current location is \n" + "lat : ${location?.latitude}\n" +
                    "long : ${location?.longitude}\n" + "fetched at ${System.currentTimeMillis()}"
        }
    }

   /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }*/
}