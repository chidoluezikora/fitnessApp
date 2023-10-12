package com.cj1_project.googlesignin

data class WorkoutModel(
    var workoutId: String? = null,
    var speedDBList: MutableList<String>? = null,
    var distanceDBList: MutableList<String>? = null,
    var timeDBList: MutableList<String>? = null,
    var calorieDBList: MutableList<String>? = null,
    var timeRecorded: String? = null,
    var userId: String? = null
)
