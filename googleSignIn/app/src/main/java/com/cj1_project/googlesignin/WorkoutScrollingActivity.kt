package com.cj1_project.googlesignin

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Random


class WorkoutScrollingActivity : AppCompatActivity() {
    // db reference
    private lateinit var reference: DatabaseReference

    //grid variables
    private lateinit var mGridAdapter : GridRVAdapter
    private lateinit var workoutGRV: GridView
    private lateinit var workoutList: MutableList<GridViewModel>

    //grid update value variables (Need to add these to the database)
    private var speedGrid : String = "0.0"
    private var distanceGrid : String = "0.0"
    private var timeGrid : String = "0"
    private var calorieGrid : String = "0"

    val width = 600.0
    val height = 150.0

    //

    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart

    //firebase variables
    // firebase realtime db reference
    private lateinit var database: DatabaseReference
    private lateinit var workoutActivity: WorkoutActivity
// ...




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

        // 4 workout lists
        var speedDBList: MutableList<String>
        var distanceDBList: MutableList<String>
        var timeDBList: MutableList<String>
        var calorieDBList: MutableList<String>

        query.get().addOnSuccessListener { dataSnapshot ->
            val snapshot = dataSnapshot.children.firstOrNull()
            val workout = snapshot?.getValue(WorkoutModel::class.java)

            if (workout != null) {
                speedDBList = workout.speedDBList!!
                distanceDBList = workout.distanceDBList!!
                timeDBList = workout.timeDBList!!
                calorieDBList = workout.distanceDBList!!
            } else {
                Toast.makeText(this, "Could not fetch finished workout from db", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
            Log.d("tag3", "Error: ${exception.message}")
        }

        // initializing variables of grid view with their ids.
        workoutGRV = findViewById(R.id.idGRV)
        workoutList = ArrayList()

        workoutList.add(GridViewModel("Speed",speedGrid,R.drawable.speed))
        workoutList.add(GridViewModel("Distance",distanceGrid, R.drawable.distance))
        workoutList.add(GridViewModel("Time",timeGrid, R.drawable.clock))
        workoutList.add(GridViewModel("Steps",calorieGrid, R.drawable.steps))

        mGridAdapter = GridRVAdapter(workoutList = workoutList, this@WorkoutScrollingActivity)
        workoutGRV.adapter = mGridAdapter
        workoutGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ -> }

        //initializing graphs and charts
        lineChart = findViewById(R.id.lineChart)
        barChart = findViewById(R.id.barChart)

        // Create a random data set for the LineChart
        val lineEntries = ArrayList<Entry>()
        val random = Random()

        for (i in 0..10) {
            val value = random.nextFloat() * 100
            lineEntries.add(Entry(i.toFloat(), value))
        }

        // Create a LineDataSet for the LineChart
        val lineDataSet = LineDataSet(lineEntries, "Line Data - Speed")
        lineDataSet.setDrawValues(false) // Disable data values on the points

        // Customize the LineChart
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        val lineDescription = Description()
        lineDescription.text = "Line Data Chart"
        lineChart.description = lineDescription

        // Refresh the LineChart
        lineChart.invalidate()

        // Create a random data set for the BarChart
        val barEntries = ArrayList<BarEntry>()

        for (i in 0..10) {
            val value = random.nextFloat() * 100
            barEntries.add(BarEntry(i.toFloat(), value))
        }

        // Create a BarDataSet for the BarChart
        val barDataSet = BarDataSet(barEntries, "Bar Data")
        barDataSet.colors = ColorTemplate.createColors(ColorTemplate.JOYFUL_COLORS)
        barDataSet.valueTextColor = ColorTemplate.rgb("#000000")
        barDataSet.valueTextSize = 12f
        barDataSet.axisDependency = YAxis.AxisDependency.LEFT
        barDataSet.isHighlightEnabled = false

        // Customize the BarChart
        val barDataSets: ArrayList<IBarDataSet> = ArrayList()
        barDataSets.add(barDataSet)

        val barData = BarData(barDataSets)
        barData.barWidth = 0.5f
        barChart.data = barData

        val barDescription = Description()
        barDescription.text = "Bar Data Chart"
        barChart.description = barDescription

        // Refresh the BarChart
        barChart.invalidate()

    }


}