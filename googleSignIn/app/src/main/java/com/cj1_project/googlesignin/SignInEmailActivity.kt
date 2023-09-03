package com.cj1_project.googlesignin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cj1_project.googlesignin.databinding.ActivitySignInEmailBinding
import com.google.firebase.auth.FirebaseAuth

class SignInEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInEmailBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.notYetRegistered.setOnClickListener{
            val intent  = Intent(this, SignUpEmailActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignInEmail.setOnClickListener {
            val email = binding.signInEmail.text.toString()
            val password = binding.signInPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }  else {
                Toast.makeText(this, "Fields must not be empty!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if(auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}