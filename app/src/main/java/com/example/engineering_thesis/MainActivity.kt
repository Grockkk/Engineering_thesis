package com.example.engineering_thesis

import Data.Sleep
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var gsc: GoogleSignInClient
    private lateinit var googleBtn: ImageView

    private val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),

        HealthPermission.getReadPermission(DistanceRecord::class),

        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),

        HealthPermission.getWritePermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),

        HealthPermission.getWritePermission(SleepStageRecord::class),
        HealthPermission.getReadPermission(SleepStageRecord::class),

        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)


    )
    private lateinit var healthConnectClient: HealthConnectClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        healthConnectClient = HealthConnectClient.getOrCreate(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)

        googleBtn = findViewById(R.id.google_btn)
        googleBtn.setOnClickListener {

            lifecycleScope.launch {
                checkPermissionsAndRun()
            }
        }

    }
    
    private suspend fun checkPermissionsAndRun() {
        val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()
        if (grantedPermissions.containsAll(PERMISSIONS)) {
            signIn();
        } else {
            Toast.makeText(applicationContext, "Required permissions were not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = gsc.signInIntent
        startActivityForResult(signInIntent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                navigateToAllActivityScreen();
            } catch (e: ApiException) {
                e.printStackTrace()
            }

        }
    }

    private fun navigateToAllActivityScreen() {
        finish()
        val intent = Intent(this, AllActivityScreen::class.java)
        startActivity(intent)
    }

}