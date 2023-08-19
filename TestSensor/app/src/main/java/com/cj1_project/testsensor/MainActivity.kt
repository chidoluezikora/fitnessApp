package com.cj1_project.testsensor

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mikhaellopez.circularprogressbar.CircularProgressBar
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
    private var totalStepsWorkout : Float = 0.0F;
    private var accelerationValsEndWorkout : MutableList<Array<Float>> = mutableListOf();
    private lateinit var workoutReference : DatabaseReference
    //pedometer
    private var running = false
    /*private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private lateinit var stepsTaken : TextView*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        workoutReference = FirebaseDatabase.getInstance().getReference("Workout")

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

        //pedometer
        /*loadData()
        resetSteps()*/

        // not that accurate implementation, use accelerometer to check and increase accuracy
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && resume) {
            if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                val hi = "Linear Acceleration is \n" + "X : ${event.values[0]}\n" +
                        "Y : ${event.values[1]}\n" + "Z : ${event.values[2]}"
                val newAccRecord = arrayOf(event.values[0], event.values[1], event.values[2]);
                accelerationValsEndWorkout.add(newAccRecord);
                findViewById<TextView>(R.id.values).text = hi

                //val abs = sqrt((event.values[0].pow(2) + event.values[1].pow(2) + event.values[2].pow(2)))
                //val speed = abs /
            }
        }


        if (running) {
            totalStepsWorkout = event!!.values[0]
        }

        /* val te = "RUnning value = $running"
         println(te)
         if (running){
             totalSteps = event!!.values[0]
             val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
             findViewById<TextView>(R.id.stepsTaken).text = ("$currentSteps") // set the text view
             println(totalSteps)
             println(previousTotalSteps)
             println(currentSteps)

             val circularProgressBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)
             circularProgressBar.apply{
                 setProgressWithAnimation(currentSteps.toFloat())
             }
         }*/
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        //running = true
        //println("Running On Resume")
        //pedometer
        /*val stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)*/

        /* if(stepSensor == null){
             Toast.makeText(this,"No sensor on this device",Toast.LENGTH_SHORT).show()

         } else {
             mSensorManager.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_NORMAL)
         }*/

    }
    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
        val workoutId = workoutReference.push().key!!
        val userId = "testUserExample"
        val workout = WorkoutModel(workoutId, totalStepsWorkout, accelerationValsEndWorkout, userId)
        workoutReference.child(workoutId).setValue(workout)
            .addOnCompleteListener {
                Toast.makeText(this, "Workout recorded successfully", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error recording workout: ${err.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun resumeReading() {
        this.resume = true
    }

    private fun pauseReading() {
        this.resume = false
    }

    /*private fun resetSteps(){
        findViewById<TextView>(R.id.stepsTaken ).setOnClickListener {
            Toast.makeText(this,"Long tap to rest Steps",Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.stepsTaken).setOnLongClickListener {
            previousTotalSteps = totalSteps
            findViewById<TextView>(R.id.stepsTaken).text = 0.toString()
            saveData()

            true
        }
    }*/

    /* private fun saveData() {
         val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
         val editor = sharedPreferences.edit()
         editor.putFloat("key1", previousTotalSteps)
         editor.apply()
     }*/

    /* private fun loadData(){
         val sharedPreferences = getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
         val savedNumber = sharedPreferences.getFloat("key1",0f)
         Log.d("MainActivity", "$savedNumber")
         previousTotalSteps = savedNumber
     }*/
}