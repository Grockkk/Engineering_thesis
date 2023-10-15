package com.example.engineering_thesis

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.health.connect.client.HealthConnectClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class CreatorsActivity2 : AppCompatActivity() {

    private lateinit var gsc: GoogleSignInClient
    private lateinit var healthConnectClient : HealthConnectClient
    private lateinit var buttonChange: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creators_2)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        healthConnectClient = initializeGoogle()

        buttonChange = findViewById(R.id.ID_Credits2Button)

        // Fire
        val Judanna_Fire: ImageView = findViewById(R.id.ID_Judanna_Fire)
        Judanna_Fire.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/fire"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        // Settings
        val PixelPerfect_UserSettings: ImageView = findViewById(R.id.ID_PixelPerfect_UserSettings)
        PixelPerfect_UserSettings.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/user"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        // Run
        val UsAndUp_running: ImageView = findViewById(R.id.ID_UsAndUp_running)
        UsAndUp_running.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/running-shoes"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        // Sleep
        val ProsymbolsPremium_sleepingIcon: ImageView = findViewById(R.id.ID_ProsymbolsPremium_sleepingIcon)
        ProsymbolsPremium_sleepingIcon.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/sleep"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })

        // BMI
        val juicy_fish_BMI: ImageView = findViewById(R.id.ID_juicy_fish_BMI)
        juicy_fish_BMI.setOnClickListener(View.OnClickListener {
            val url = "https://www.flaticon.com/free-icons/bmi"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        })



        buttonChange.setOnClickListener {
            navigateToCreators()
        }
    }

    private fun navigateToCreators() {
        finish()
        val intent = Intent(this, CreatorsActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    fun initializeGoogle(): HealthConnectClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        return HealthConnectClient.getOrCreate(this@CreatorsActivity2)
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