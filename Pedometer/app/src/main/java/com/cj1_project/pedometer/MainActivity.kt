package com.cj1_project.pedometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.lang.Math.sqrt

//https://developer.android.com/guide/topics/sensors/sensors_overview
//https://www.youtube.com/watch?v=sOGmivei73I

/*class MainActivity : AppCompatActivity(), SensorEventListener {

    var running = false
    var sensorManager:SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepsSensor == null) {
            Toast.makeText(this, "No Step Counter Sensor !", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (running) {
            var stepsValue= findViewById<TextView>(R.id.tv_stepsTaken)
            stepsValue.text = "${event.values[0]}"
        }
    }
}*/

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var magPreviousStep = 0.0
    private var sensorManager: SensorManager? = null
    private var running : Boolean = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()
        resetSteps()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        running = true

        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        // So don't forget to add the following permission in AndroidManifest.xml present in manifest folder of the app.
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        val stepAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        when{
            stepSensor != null -> {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
            }
            stepDetector != null -> {
                sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI)
            }
            stepAccelerometer != null -> {
                sensorManager.registerListener(this, stepAccelerometer, SensorManager.SENSOR_DELAY_UI)
            }
            else -> {
                Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView
        val stepsTaken = findViewById<TextView>(R.id.steps)
        val stepBar = findViewById<CircularProgressBar>(R.id.StepBar)

        if(event!!.sensor.type == Sensor.TYPE_ACCELEROMETER){
            val xa : Float = event.values[0]
            val ya : Float = event.values[1]
            val za : Float = event.values[2]
            val mag : Double = kotlin.math.sqrt((xa * xa + ya * ya + za * za).toDouble())
            val magDelta : Double = mag - magPreviousStep
            magPreviousStep = mag

            if (magDelta > 6){
                totalSteps++
            }
            val step : Int = totalSteps.toInt()

            stepsTaken.text = step.toString()
            stepBar.apply {
                setProgressWithAnimation(step.toFloat())
            }

        } else {
            if (running) {
                totalSteps = event.values[0]

                // Current steps are calculated by taking the difference of total steps
                // and previous steps
                val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
                println(currentSteps)
                // It will show the current steps to the user
                stepsTaken.text = currentSteps.toString()

                stepBar.apply {
                    setProgressWithAnimation(currentSteps.toFloat())
                }
            }
        }
    }

    private fun resetSteps() {
        val stepsTaken = findViewById<TextView>(R.id.steps)
        stepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        stepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            stepsTaken.text = 0.toString()

            // This will save the data
            saveData()

            true
        }
    }

    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }
}
