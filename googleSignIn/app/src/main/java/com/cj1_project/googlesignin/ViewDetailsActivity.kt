package com.cj1_project.googlesignin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ViewDetailsActivity : AppCompatActivity() {

    //logout
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    private lateinit var reference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_details)

        //authentication
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        var textView = findViewById<TextView>(R.id.textName)
        var textWeight = findViewById<TextView>(R.id.textWeight)
        var textHeight = findViewById<TextView>(R.id.textHeight)
        var textBMI = findViewById<TextView>(R.id.textBMI)

        var userName = ""
        textView.text = userName
        reference = FirebaseDatabase.getInstance().reference
        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        reference.child("User").child(userID).get().addOnSuccessListener { dataSnapshot ->
            val firstName = dataSnapshot.child("firstName").value.toString()
            val lastName = dataSnapshot.child("lastName").value.toString()
            val weightInKg = dataSnapshot.child("weightInKg").value.toString()
            val heightInCm = dataSnapshot.child("heightInCm").value.toString()

            userName = "$firstName $lastName"
            textView.text = userName

            textWeight.text = weightInKg
            textHeight.text = heightInCm
            textBMI.text = calculateBMI(weightInKg.toFloat(),heightInCm.toFloat())

        }.addOnFailureListener { exception ->
            Log.d("tag3", "Error: ${exception.message}")
        }


        val viewDetailButton = findViewById<Button>(R.id.btnLogOut)
        viewDetailButton.setOnClickListener {
            signOutAndStartSignInActivity()
        }

        val history = findViewById<Button>(R.id.historyBtn)
        history.setOnClickListener {
            val intent = Intent(this@ViewDetailsActivity, WorkoutHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@ViewDetailsActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun calculateBMI(weight: Float, height: Float): String {
        if (weight <= 0.0 || height <= 0.0) {
            throw IllegalArgumentException("Weight and height must be positive values.")
        }
        val heightInMeters = height / 100
        val bmi = weight / (heightInMeters * heightInMeters)
        return bmi.toString()
    }
}