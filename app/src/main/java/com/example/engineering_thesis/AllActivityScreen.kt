package com.example.engineering_thesis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

class AllActivityScreen : AppCompatActivity() {

    private lateinit var gsc: GoogleSignInClient
    private lateinit var person_name: TextView
    private lateinit var steps: TextView
    private lateinit var increment_btn: Button
    val time = Time();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_screen)

        // logowanie google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        person_name = findViewById(R.id.name)
        if (acct!= null) {
            val personName = acct.displayName;
            person_name.setText(personName)

        }
            // kroki

            val healthConnectClient = HealthConnectClient.getOrCreate(this@AllActivityScreen)
            var steps: TextView = findViewById(R.id.total_steps)





            GlobalScope.launch {


                val distanceTotalInMeters = readSteps(healthConnectClient, time.getStartTime(), time.getEndTime())
                runOnUiThread {
                    steps.text = distanceTotalInMeters.toString()
                }

            }

            increment_btn = findViewById(R.id.incr)
            increment_btn.setOnClickListener(){
                time.incrDay()
                GlobalScope.launch {


                    val distanceTotalInMeters = readSteps(healthConnectClient, time.getStartTime(), time.getEndTime())

                        withContext(Dispatchers.Main) {
                            // update the TextView on the main thread
                            steps.text = distanceTotalInMeters.toString()
                        }


                }
            }





    }
    suspend fun readSteps(healthConnectClient : HealthConnectClient, startTime: Instant, endTime: Instant): Int? {
        val response =
            healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
// The result may be null if no data is available in the time range.
        return response[StepsRecord.COUNT_TOTAL]?.toInt()
    }
}