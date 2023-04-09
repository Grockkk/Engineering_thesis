package com.example.engineering_thesis

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {

    private lateinit var gsc: GoogleSignInClient
    private lateinit var googleBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)

        googleBtn = findViewById(R.id.google_btn)
        googleBtn.setOnClickListener {
            // Code to execute when the button is clicked
            signIn();
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

    fun navigateToAllActivityScreen() {
        finish()
        val intent = Intent(this, AllActivityScreen::class.java)
        startActivity(intent)
    }

}