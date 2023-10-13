package com.cj1_project.googlesignin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class ProfileActivity : AppCompatActivity(), SensorEventListener {

    //user photo
    private lateinit var profilePic: ImageView
    private lateinit var addPhoto: FloatingActionButton

    //pedometer
    private var magPreviousStep = 0.0
    private var sensorManager: SensorManager? = null
    private var running : Boolean = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    // user reference
    private lateinit var reference : DatabaseReference

    //logout
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //authentication
        /*mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()*/

        //mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //add photo
        //supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.GRAY))
        profilePic = findViewById(R.id.profilePic)
        addPhoto = findViewById(R.id.addPhoto)
        addPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        //pedometer
        loadData()
        //resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //User Name
        var textView = findViewById<TextView>(R.id.userName)
        var userName = ""
        textView.text = userName
        val reference = FirebaseDatabase.getInstance().reference
        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        reference.child("User").child(userID).get().addOnSuccessListener { dataSnapshot ->
            val firstName = dataSnapshot.child("firstName").value.toString()
            val lastName = dataSnapshot.child("lastName").value.toString()
            userName = "$firstName $lastName"
            // Set the textView.text inside the success listener
            textView.text = userName
        }.addOnFailureListener { exception ->
            Log.d("tag3", "Error: ${exception.message}")
        }
        //sign in and out
        //google sign in

        val viewDetailButton = findViewById<Button>(R.id.viewDetail)
        viewDetailButton.setOnClickListener {
            //signOutAndStartSignInActivity()
            val intent = Intent(this,ViewDetailsActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.maps).setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    /*private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@ProfileActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }*/
    override fun onResume() {
        super.onResume()

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        running = true

        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        // So don't forget to add the following permission in AndroidManifest.xml present in manifest folder of the app.
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        val stepAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        when{
            stepSensor != null -> {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
            }
            stepDetector != null -> {
                sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI)
            }
            stepAccelerometer != null -> {
                sensorManager.registerListener(this, stepAccelerometer, SensorManager.SENSOR_DELAY_UI)
            }
            else -> {
                Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView
        val stepsTaken = findViewById<TextView>(R.id.steps)
        val stepBar = findViewById<CircularProgressBar>(R.id.StepBar)

        if(event!!.sensor.type == Sensor.TYPE_ACCELEROMETER){
            val xa : Float = event.values[0]
            val ya : Float = event.values[1]
            val za : Float = event.values[2]
            val mag : Double = kotlin.math.sqrt((xa * xa + ya * ya + za * za).toDouble())
            val magDelta : Double = mag - magPreviousStep
            magPreviousStep = mag

            if (magDelta > 6){
                totalSteps++
            }
            val step : Int = totalSteps.toInt()

            stepsTaken.text = step.toString()
            stepBar.apply {
                setProgressWithAnimation(step.toFloat())
            }

        } else {
            if (running) {
                totalSteps = event.values[0]

                // Current steps are calculated by taking the difference of total steps
                // and previous steps
                val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
                println(currentSteps)
                // It will show the current steps to the user
                stepsTaken.text = currentSteps.toString()

                stepBar.apply {
                    setProgressWithAnimation(currentSteps.toFloat())
                }
            }
        }
    }

    private fun resetSteps() {
        if (totalSteps >3000){
            val stepsTaken = findViewById<TextView>(R.id.steps)
            stepsTaken.setOnClickListener {
                // This will give a toast message if the user want to reset the steps
                Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
            }
            stepsTaken.setOnLongClickListener {
                previousTotalSteps = totalSteps
                // When the user will click long tap on the screen,
                // the steps will be reset to 0
                stepsTaken.text = 0.toString()
                // This will save the data
                saveData()
                true
            }
        }

    }

    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        profilePic.setImageURI(data?.data)
    }
}