package com.cj1_project.googlesignin

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlin.math.*
import com.cj1_project.googlesignin.Constants.TIMER_INTERVAL
import com.cj1_project.googlesignin.Utility.getFormattedStopWatch
import com.cj1_project.googlesignin.databinding.ActivityMainBinding

/*
* https://www.atlantis-press.com/article/125925132.pdf - Calorie Formula
* https://medium.com/swlh/how-to-create-a-stopwatch-in-android-117912264491 - StopWatch
* */
class WorkoutActivity : AppCompatActivity(), SensorEventListener {

    //grid variables
    private lateinit var mGridAdapter : GridRVAdapter
    private lateinit var workoutGRV: GridView
    private lateinit var workoutList: MutableList<GridViewModel>

    //grid update value variables (Need to add these to the database)
    private var speedGird : String = "0.0"
    private var distanceGrid : String = "0.0"
    private var timeGrid : String = "0"
    private var calorieGrid : String = "0"

    //variables for calculating distance
    private lateinit var workoutLatitudeList : MutableList<Double>
    private lateinit var workoutLongitudeList : MutableList<Double>
    private var totalDistance : Double = 0.0

    //location Manager
    private var kmphSpeed = 0f
    private var locationTrackingRequested = false
    private lateinit var locationManager: GeoLocation

    //pedometer
    private var magPreviousStep = 0.0
    private var sensorManager: SensorManager? = null
    private var running : Boolean = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    //stopwatch
    private val mInterval = TIMER_INTERVAL
    private var mHandler: Handler? = null

    private var timeInSeconds = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // --------------------------------------------------------------
        // initializing variables of grid view with their ids.
        workoutGRV = findViewById(R.id.idGRV)
        workoutList = ArrayList()

        workoutList.add(GridViewModel("Speed", speedGird,R.drawable.speed))
        workoutList.add(GridViewModel("Distance",distanceGrid, R.drawable.distance))
        workoutList.add(GridViewModel("Time",timeGrid, R.drawable.clock))
        workoutList.add(GridViewModel("Steps",calorieGrid, R.drawable.steps))

        mGridAdapter = GridRVAdapter(workoutList = workoutList, this@WorkoutActivity)
        workoutGRV.adapter = mGridAdapter
        workoutGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ -> }
        // --------------------------------------------------------------

        //location
        workoutLatitudeList = ArrayList()
        workoutLongitudeList = ArrayList()
        locationManager = GeoLocation(this)

        //pedometer
        loadData()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //stopwatch
        initStopWatch()

        // Register button click listeners
        findViewById<Button>(R.id.startButton).setOnClickListener {
            locationManager.startLocationTracking(locationCallback)
            locationTrackingRequested = true
            startTimer()
            findViewById<TextView>(R.id.textWorkout).text = "Workout Started"
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            locationManager.stopLocationTracking()
            locationTrackingRequested = false
            stopTimer()
            findViewById<TextView>(R.id.textWorkout).text = "Workout Stopped"
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //locationResult ?: return
            for (location in locationResult.locations){

                // Update UI
                workoutLatitudeList.add(location.latitude)
                workoutLongitudeList.add(location.longitude)

                val listCount = workoutLatitudeList.count()
                if (listCount < 2){
                    totalDistance += 0
                    distanceGrid = totalDistance.toString()
                } else {
                    totalDistance += distBetCoordinates(
                        workoutLatitudeList[listCount-2],
                        workoutLongitudeList[listCount-2],
                        workoutLatitudeList[listCount-1],
                        workoutLongitudeList[listCount-1])

                    distanceGrid = totalDistance.toString()
                }

                if (location.hasSpeed()){
                    kmphSpeed = (location.speed * 3.6).toFloat()
                    speedGird = kmphSpeed.toString()
                    /*println("Latitude" + location.latitude.toString())
                      println("Longitude" + location.longitude.toString())
                      println("Speed" + location.speed.toString())
                      println("Speed KMPH $kmphSpeed")*/
                }
                else
                    speedGird = "0.0"

                val number3digits:Double = String.format("%.3f", distanceGrid.toDouble()).toDouble()
                val number2digits:Double = String.format("%.2f", number3digits).toDouble()
                distanceGrid = number2digits.toString()

                workoutList[0] = GridViewModel("Speed", speedGird,R.drawable.speed)
                workoutList[1] = GridViewModel("Distance",distanceGrid, R.drawable.distance)
                //workoutList[3] = GridViewModel("Calories",calorieGrid, R.drawable.calories)
                mGridAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, re-run onMapReady
                locationManager.startLocationTracking(locationCallback)
            }
        }
    }

    private fun distBetCoordinates(lat1 : Double,lon1: Double,lat2: Double,lon2: Double) : Double{
        val earthRadiusKm = 6371
        val dLat = degreesToRadians(lat2-lat1)
        val dLon = degreesToRadians(lon2-lon1)

        val newLat1 = degreesToRadians(lat1)
        val newLat2 = degreesToRadians(lat2)

        val a = sin(dLat/2) * sin(dLat/2) + sin(dLon/2) * sin(dLon/2) * cos(newLat1) * cos(newLat2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))
        return earthRadiusKm * c
    }

    private fun degreesToRadians(degrees : Double): Double {
        return degrees * Math.PI / 180
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
            stepAccelerometer != null -> {
                sensorManager.registerListener(this, stepAccelerometer, SensorManager.SENSOR_DELAY_UI)
            }
            stepDetector != null -> {
                sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI)
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

        if(event!!.sensor.type == Sensor.TYPE_ACCELEROMETER){
            val xa : Float = event.values[0]
            val ya : Float = event.values[1]
            val za : Float = event.values[2]
            val mag : Double = sqrt((xa * xa + ya * ya + za * za).toDouble())
            val magDelta : Double = mag - magPreviousStep
            magPreviousStep = mag

            if (magDelta > 6){
                totalSteps++
            }
            val step : Int = totalSteps.toInt()

            calorieGrid = step.toString()
            workoutList[3] = GridViewModel("Steps",calorieGrid, R.drawable.steps)
            mGridAdapter.notifyDataSetChanged()

        } else {
            if (running) {
                totalSteps = event.values[0]

                // Current steps are calculated by taking the difference of total steps
                // and previous steps
                val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
                println(currentSteps)
                // It will show the current steps to the user
                calorieGrid = currentSteps.toString()
                workoutList[3] = GridViewModel("Calories",calorieGrid, R.drawable.calories)
                mGridAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun resetSteps() {
        val stepsTaken = findViewById<TextView>(R.id.stopButton)

        stepsTaken.setOnLongClickListener {
            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            calorieGrid = 0.toString()
            workoutList[3] = GridViewModel("Calories",calorieGrid, R.drawable.calories)
            mGridAdapter.notifyDataSetChanged()
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

    private fun initStopWatch() {
       // binding?.textViewStopWatch?.text = getString(R.string.init_stop_watch_value)
        timeGrid = "00:00:00"
        workoutList[2] = GridViewModel("Time",timeGrid, R.drawable.clock)
        mGridAdapter.notifyDataSetChanged()
    }

    private fun startTimer() {
        mHandler = Handler(Looper.getMainLooper())
        mStatusChecker.run()
    }

    private fun stopTimer() {
        mHandler?.removeCallbacks(mStatusChecker)
    }

    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                timeInSeconds += 1
                Log.e("timeInSeconds", timeInSeconds.toString())
                updateStopWatchView(timeInSeconds)
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler!!.postDelayed(this, mInterval.toLong())
            }
        }
    }

    private fun updateStopWatchView(timeInSeconds: Long) {
        val formattedTime = getFormattedStopWatch((timeInSeconds * 1000))
        Log.e("formattedTime", formattedTime)
        timeGrid = formattedTime
        workoutList[2] = GridViewModel("Time",timeGrid, R.drawable.clock)
        mGridAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
}

