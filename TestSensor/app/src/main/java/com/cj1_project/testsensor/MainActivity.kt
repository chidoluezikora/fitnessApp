package com.cj1_project.testsensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlin.math.pow
import kotlin.math.sqrt

/*
* https://stackoverflow.com/questions/45958226/get-location-android-kotlin
* last code snippet look at it
* */

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mSensorManager: SensorManager
    private var mAccelerometer : Sensor ?= null
    private var resume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) // we exclude gravity while calculating speed

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
            if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                val accList = mutableListOf(event.values[0],event.values[1],event.values[2])
                findViewById<TextView>(R.id.values).text =
                    "Linear Acceleration is \n" + "X : ${event.values[0]}\n" +
                            "Y : ${event.values[1]}\n" + "Z : ${event.values[2]}"
                val abs = sqrt((event.values[0].pow(2) + event.values[1].pow(2) + event.values[2].pow(2)))
                val speed = abs /
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
}