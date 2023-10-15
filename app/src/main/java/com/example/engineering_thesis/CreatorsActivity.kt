package com.example.engineering_thesis

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.health.connect.client.HealthConnectClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class CreatorsActivity : AppCompatActivity() {

    private lateinit var gsc: GoogleSignInClient
    private lateinit var healthConnectClient : HealthConnectClient
    private lateinit var buttonChange: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creators)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        healthConnectClient = initializeGoogle()

        buttonChange = findViewById(R.id.ID_CreditsButton)

        // Calories Burned
        val Freepik_caloriesBurned: ImageView = findViewById(R.id.ID_Freepik_caloriesBurned)
        Freepik_caloriesBurned.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/calories"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        // Add Sleep
        val Freepik_sleepMoon: ImageView = findViewById(R.id.ID_Freepik_sleepMoon)
        Freepik_sleepMoon.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/sleeping"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        // Cardiogram
        val Freepik_cardiogram: ImageView = findViewById(R.id.ID_Freepik_cardiogram)
        Freepik_cardiogram.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/heart"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        // Heart Rate
        val BertFlint_heartRate: ImageView = findViewById(R.id.ID_BertFlint_heartRate)
        BertFlint_heartRate.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/heart-rate"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        // Credits
        val monkik_credits: ImageView = findViewById(R.id.ID_monkik_credits)
        monkik_credits.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/author"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        // Add Steps
        val Icongeek26_steps: ImageView = findViewById(R.id.ID_Icongeek26_steps)
        Icongeek26_steps.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/steps"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        // Distance
        val ThoseIcons_Distance: ImageView = findViewById(R.id.ID_ThoseIcons_Distance)
        ThoseIcons_Distance.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/road"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        buttonChange.setOnClickListener {
            navigateToCreators()
        }
    }

    private fun navigateToCreators() {
        finish()
        val intent = Intent(this, CreatorsActivity2::class.java)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    fun initializeGoogle(): HealthConnectClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        return HealthConnectClient.getOrCreate(this@CreatorsActivity)
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

}