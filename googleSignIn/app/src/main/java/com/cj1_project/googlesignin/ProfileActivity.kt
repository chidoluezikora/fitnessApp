package com.cj1_project.googlesignin

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProfileActivity : AppCompatActivity() {

    private lateinit var profilePic: ImageView
    private lateinit var addPhoto: FloatingActionButton

    private lateinit var stepsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.GRAY))

        profilePic = findViewById(R.id.profilePic)
        addPhoto = findViewById(R.id.addPhoto)

        stepsButton = findViewById<Button>(R.id.stepsButton)

        addPhoto.setOnClickListener {

            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        stepsButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}