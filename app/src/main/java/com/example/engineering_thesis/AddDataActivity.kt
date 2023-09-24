package com.example.engineering_thesis

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.RelativeLayout
import androidx.health.connect.client.HealthConnectClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class AddDataActivity : AppCompatActivity() {
    private lateinit var gsc: GoogleSignInClient
    private lateinit var healthConnectClient : HealthConnectClient
    private lateinit var relativeLayoutSteps: RelativeLayout
    private lateinit var relativeLayoutDistance: RelativeLayout
    private lateinit var relativeLayoutSleep: RelativeLayout
    private lateinit var relativeLayoutCalories: RelativeLayout
    private lateinit var relativeLayoutHeartRate: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)

        healthConnectClient = initializeGoogle()
        relativeLayoutSteps = findViewById(R.id.ID_addSteps)
        relativeLayoutDistance = findViewById(R.id.ID_addDistance)
        relativeLayoutSleep = findViewById(R.id.ID_addSleep)
        relativeLayoutCalories = findViewById(R.id.ID_addSCalories)
        relativeLayoutHeartRate = findViewById(R.id.ID_addSHeartRatio)

        relativeLayoutDistance.setOnClickListener(){
            setContentView(R.layout.add_distance_data)
        }
        relativeLayoutSleep.setOnClickListener(){
            setContentView(R.layout.add_sleep_data)
        }
        relativeLayoutCalories.setOnClickListener(){
            setContentView(R.layout.add_calories_data)
        }
        relativeLayoutHeartRate.setOnClickListener(){
            setContentView(R.layout.add_heart_ratio_data)
        }
        relativeLayoutSteps.setOnClickListener(){
            setContentView(R.layout.add_steps_data)
        }


    }



    @SuppressLint("SetTextI18n")
    fun initializeGoogle(): HealthConnectClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct!= null) {
            val personName = acct.displayName
            val actionBar = supportActionBar
            actionBar?.title = "Hello " + personName.toString() + "!"
        }

        return HealthConnectClient.getOrCreate(this@AddDataActivity)
    }
}