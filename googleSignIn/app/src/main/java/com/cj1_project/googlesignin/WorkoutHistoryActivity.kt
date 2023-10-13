package com.cj1_project.googlesignin

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WorkoutHistoryActivity : AppCompatActivity() {
    private lateinit var listView : ListView
    private lateinit var reference: DatabaseReference

    private var list : MutableList<WorkoutModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_history)

        listView = findViewById(R.id.listView)

        // get workouts associated with user from database
        reference = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val query = reference.child("Workout")
            .orderByChild("userId")
            .equalTo(userId)
            .limitToLast(1)

        query.get().addOnSuccessListener { dataSnapshot ->
            list.clear()
            val snapshot = dataSnapshot.children.filterNotNull()
            for (sp in dataSnapshot.children) {
                val workout = sp.getValue(WorkoutModel::class.java)
                workout?.let {
                    list.add(it)
                }
            }
            list.reverse()
            val adapter = WorkoutAdapter(this@WorkoutHistoryActivity, R.layout.workout_item, list)
            listView.adapter = adapter

        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
            Log.d("tag3", "Error: ${exception.message}")
        }
    }
}