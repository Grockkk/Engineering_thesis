package com.example.engineering_thesis

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TimePicker
import androidx.health.connect.client.HealthConnectClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.time.Month
import java.util.Calendar

class AddDataActivity : AppCompatActivity(){
    private lateinit var gsc: GoogleSignInClient
    private lateinit var healthConnectClient : HealthConnectClient
    private lateinit var layoutSteps: LinearLayout
    private lateinit var layoutDistance: LinearLayout
    private lateinit var layoutSleep: LinearLayout
    private lateinit var layoutCalories: LinearLayout
    private lateinit var layoutHeartRate: LinearLayout

    private lateinit var btn: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        healthConnectClient = initializeGoogle()
        layoutSteps = findViewById(R.id.ID_addSteps)
        layoutDistance = findViewById(R.id.ID_addDistance)
        layoutSleep = findViewById(R.id.ID_addSleep)
        layoutCalories = findViewById(R.id.ID_addSCalories)
        layoutHeartRate = findViewById(R.id.ID_addSHeartRate)

        //pickDate()

        layoutDistance.setOnClickListener(){
            navigateToAddDistance()
        }
        layoutSleep.setOnClickListener(){
            navigateToAddSleep()
        }
        layoutCalories.setOnClickListener(){
            navigateToAddCalories()
        }
        layoutHeartRate.setOnClickListener(){
            navigateToAddHR()
        }
        layoutSteps.setOnClickListener(){
            navigateToAddSteps()
        }
    }

    @SuppressLint("SetTextI18n")
    fun initializeGoogle(): HealthConnectClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        val acct = GoogleSignIn.getLastSignedInAccount(this)

        return HealthConnectClient.getOrCreate(this@AddDataActivity)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, AllActivityScreen::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToAddSteps() {
        finish()
        val intent = Intent(this, addStepsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAddSleep() {
        finish()
        val intent = Intent(this, addSleepActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAddHR() {
        finish()
        val intent = Intent(this, addHeartRateActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAddDistance() {
        finish()
        val intent = Intent(this, addDistanceActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAddCalories() {
        finish()
        val intent = Intent(this, addCaloriesActivity::class.java)
        startActivity(intent)
    }
}