package com.cj1_project.testsensor

data class WorkoutModel(
    var workoutId: String,
    var totalSteps: Float,
    var workoutVal: MutableList<Array<Float>>,
    var userId: String
)