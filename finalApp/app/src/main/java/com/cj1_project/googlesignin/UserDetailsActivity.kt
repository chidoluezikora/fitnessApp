package com.cj1_project.googlesignin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.cj1_project.googlesignin.databinding.ActivityUserDetailsBinding

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var userReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        userReference = FirebaseDatabase.getInstance().getReference("User")

        binding.btnEnterDetails.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val heightInCm = binding.height.text.toString()
            val weightInKg = binding.weight.text.toString()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Could not retrieve user's ID", Toast.LENGTH_LONG).show()
            } else {
                val userId = currentUser.uid
                if (firstName.isNotEmpty() && lastName.isNotEmpty() && heightInCm.isNotEmpty() && weightInKg.isNotEmpty()) {
                    val user = UserModel(userId, firstName, lastName, heightInCm, weightInKg)
                    userReference.child(userId).setValue(user)
                        .addOnCompleteListener {
                            Toast.makeText(this, "User details recorded successfully", Toast.LENGTH_LONG).show()
                            val intent  = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                        }.addOnFailureListener { err ->
                            Toast.makeText(
                                this,
                                "Error entering user details: ${err.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } else {
                    Toast.makeText(this,  "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}