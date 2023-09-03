package com.cj1_project.googlesignin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.cj1_project.googlesignin.databinding.ActivitySignUpEmailBinding

class SignUpEmailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpEmailBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.alreadyRegistered.setOnClickListener{
            val intent  = Intent(this, SignInEmailActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUpEmail.setOnClickListener {
            val email = binding.signUpEmail.text.toString()
            val password = binding.signUpPassword.text.toString()
            val confirmPassword = binding.signUpPasswordConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password  ==  confirmPassword) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful) {
                            val intent  = Intent(this, SignInEmailActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this,  it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this,  "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this,  "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}