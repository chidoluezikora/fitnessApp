package com.cj1_project.myapplication

data class WorkoutModel(
    var workoutId: String,
    var locationAndSpeedVal: MutableList<Array<String>>,
    var userId: String
)
