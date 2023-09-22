package com.cj1_project.googlesignin

data class WorkoutModel(
    var workoutId: String,
    var sppedDBList: MutableList<String>,
    var distanceDBList: MutableList<String>,
    var timeDBList: MutableList<String>,
    var calorieDBList: MutableList<String>,
    var userId: String
)
