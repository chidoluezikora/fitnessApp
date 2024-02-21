package com.cj1_project.googlesignin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class WorkoutAdapter(context: Context, resource: Int, objects: List<WorkoutModel>) :
    ArrayAdapter<WorkoutModel>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val workoutItem = getItem(position)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.workout_item, parent, false)

        // access layout elements
        val avgSpeed = view.findViewById<TextView>(R.id.avgSpeed)
        val distance = view.findViewById<TextView>(R.id.distance)
        val calories = view.findViewById<TextView>(R.id.calories)
        val timeElapsed = view.findViewById<TextView>(R.id.timeElapsed)
        val timeRecorded = view.findViewById<TextView>(R.id.timeRecorded)

        // calculate average speed
        val speedList: List<Double> = workoutItem?.speedDBList!!.map { it.toDouble() }
        val averageSpeed : Double = if (speedList.isNotEmpty()) {
            speedList.average()
        } else {
            0.0
        }
        val averageSpeedString: String = String.format("%.2f", averageSpeed)

        // set the text for each TextView
        avgSpeed.text = averageSpeedString
        distance.text = workoutItem?.distanceDBList!!.last()
        calories.text = workoutItem?.calorieDBList!!.last()
        timeElapsed.text = workoutItem?.timeDBList!!.last()
        timeRecorded.text = workoutItem?.timeRecorded

        return view
    }
}
