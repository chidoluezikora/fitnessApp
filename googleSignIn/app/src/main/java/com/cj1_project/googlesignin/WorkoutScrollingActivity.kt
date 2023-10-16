package com.cj1_project.googlesignin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.math.RoundingMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WorkoutScrollingActivity : AppCompatActivity() {

    // db reference
    private lateinit var reference: DatabaseReference


    //grid variables
    private lateinit var mGridAdapter : GridRVAdapter
    private lateinit var workoutGRV: GridView
    private lateinit var workoutList: MutableList<GridViewModel>

    private lateinit var mGridAdapterSpeed : GridRVAdapter
    private lateinit var workoutGRVSpeed: GridView
    private lateinit var workoutListSpeed: MutableList<GridViewModel>

    //grid update value variables (Need to add these to the database)
    private var avgSpeedGrid : String = "0.0"
    private var maxSpeedGrid : String = "0.0"
    private var minSpeedGrid : String = "0.0"

    private var distanceGrid : String = "0.0"
    private var timeGrid : String = "0"
    private var calorieGrid : String = "0"
    private var stepGrid : String = "0"

    //firebase variables
    // firebase realtime db reference
    private lateinit var database: DatabaseReference
    private lateinit var workoutActivity: WorkoutActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_scrolling)

        // realtime db
        reference = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val query = reference.child("Workout")
            .orderByChild("userId")
            .equalTo(userId)
            .limitToLast(1)

        // Get data from the database
        var speedDBList: MutableList<String> = mutableListOf()
        var distanceDBList: MutableList<String> = mutableListOf()
        var timeDBList: MutableList<String> = mutableListOf()
        var calorieDBList: MutableList<String> = mutableListOf()

        var weightInKg : String = ""

        // get the weight and height
        reference.child("User").child(userId).get().addOnSuccessListener { dataSnapshot ->
            weightInKg = dataSnapshot.child("weightInKg").value.toString()

        }.addOnFailureListener { exception ->
            Log.d("tag3", "Error: ${exception.message}")
        }

        // get the speed, distance, time and calories
        query.get().addOnSuccessListener { dataSnapshot ->
            val snapshot = dataSnapshot.children.firstOrNull()
            val workout = snapshot?.getValue(WorkoutModel::class.java)

            if (workout != null) {
                speedDBList = workout.speedDBList!!
                distanceDBList = workout.distanceDBList!!
                timeDBList = workout.timeDBList!!
                calorieDBList = workout.distanceDBList!!

                // calculate average speed
                val speedList: List<Double> = speedDBList.map { it.toDouble() }
                val averageSpeed : Double = if (speedList.isNotEmpty()) {
                    speedList.average()
                } else {
                    0.0
                }
                avgSpeedGrid = String.format("%.2f", averageSpeed)

                val maxSpeed :Double? = speedList.maxOrNull()
                maxSpeedGrid = String.format("%.2f", maxSpeed)

                val minSpeed :Double? = speedList.minOrNull()
                minSpeedGrid = String.format("%.2f", minSpeed)

                //calculate calories
                val calories = calorieCalculation(weightInKg.toFloat(), timeToSeconds(timeDBList.last()).toFloat())

                distanceGrid = distanceDBList.last().toString()
                stepGrid = calorieDBList.last()
                timeGrid = timeDBList.last()
                calorieGrid =  String.format("%.2f", calories.toDouble())

                // initializing variables of grid view with their ids.
                workoutGRV = findViewById(R.id.idGRV)
                workoutList = ArrayList()

                workoutList.add(GridViewModel("Time",timeGrid, R.drawable.clock))
                workoutList.add(GridViewModel("Distance",distanceGrid, R.drawable.distance))
                workoutList.add(GridViewModel("Calories", calorieGrid, R.drawable.calories))
                workoutList.add(GridViewModel("Steps",stepGrid, R.drawable.steps))

                mGridAdapter = GridRVAdapter(workoutList = workoutList, this@WorkoutScrollingActivity)
                workoutGRV.adapter = mGridAdapter
                workoutGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ -> }


                // initializing variables of grid view with their ids.
                workoutGRVSpeed = findViewById(R.id.speedGRV)
                workoutListSpeed = ArrayList()

                workoutListSpeed.add(GridViewModel("Max Speed",maxSpeedGrid, R.drawable.speed))
                workoutListSpeed.add(GridViewModel("Min Speed",minSpeedGrid, R.drawable.speed))
                workoutListSpeed.add(GridViewModel("Average Speed",avgSpeedGrid, R.drawable.speed))

                mGridAdapterSpeed = GridRVAdapter(workoutList = workoutListSpeed, this@WorkoutScrollingActivity)
                workoutGRVSpeed.adapter = mGridAdapterSpeed
                workoutGRVSpeed.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ -> }


            } else {
                Toast.makeText(this, "Could not fetch finished workout from db", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
            Log.d("tag3", "Error: ${exception.message}")
        }

        findViewById<Button>(R.id.historyButton).setOnClickListener {
            val intent = Intent(this@WorkoutScrollingActivity,WorkoutHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    // file:///C:/Users/shant/AppData/Local/Temp/MicrosoftEdgeDownloads/9d63bb8a-d5fb-4d10-becf-15d145a4a78f/125925132%20(1).pdf
    private fun calorieCalculation(weight: Float, duration: Float): String {
        val met = 2.8
        return ((met * 7.7) * ((weight * 2.2) / 200) * (duration / 60)).toString()
    }

    private fun timeToSeconds(timeString: String): Int {
        val parts = timeString.split(":")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid time format. Expected 'hh:mm:ss'")
        }

        try {
            val hours = parts[0].toInt()
            val minutes = parts[1].toInt()
            val seconds = parts[2].toInt()

            if (hours < 0 || minutes < 0 || seconds < 0) {
                throw IllegalArgumentException("Time components must be non-negative")
            }

            return hours * 3600 + minutes * 60 + seconds
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid time components. Expected integers.")
        }
    }

}