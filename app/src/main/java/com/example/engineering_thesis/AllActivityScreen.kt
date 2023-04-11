package com.example.engineering_thesis

import Data.Steps
import Time.Time
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SleepStageRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.*
import java.time.Duration
import java.time.Instant

class AllActivityScreen : AppCompatActivity() {

    private lateinit var gsc: GoogleSignInClient
    private lateinit var person_name: TextView
    private lateinit var nextDayButton: Button
    private lateinit var prevDayButton: Button
    val time = Time();
    val st = Steps();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_screen)

        // logowanie google
        val healthConnectClient = InitializeGoogle()

        //initialize textView and buttons
        var steps: TextView = findViewById(R.id.total_steps)
        var dateOfTheDay: TextView = findViewById(R.id.currnet_day)
        var sleep: TextView = findViewById(R.id.sleep)
        prevDayButton = findViewById(R.id.prev_day)
        nextDayButton = findViewById(R.id.next_day)
        dateOfTheDay.text = time.day.toString()


            GlobalScope.launch(Dispatchers.Main) {
                val numberOfSteps = readSteps(healthConnectClient, time.getStartTime(), time.getEndTime())
                val timeOfSleep = readSleepDuration(healthConnectClient, time.getStartTime(), time.getEndTime())
                runOnUiThread {
                        steps.text = numberOfSteps.toString()
                    sleep.text = timeOfSleep.toString()

                }

            }

        // przyciski ruchu dat

            prevDayButton.setOnClickListener(){
                time.incrDay()
                GlobalScope.launch(Dispatchers.Main) {
                    val numberOfSteps = readSteps(healthConnectClient, time.getStartTime(), time.getEndTime())
                    val timeOfSleep = readSleepDuration(healthConnectClient, time.getStartTime(), time.getEndTime())
                    runOnUiThread {
                        steps.text = numberOfSteps.toString()
                        dateOfTheDay.text = time.day.toString()
                        sleep.text = timeOfSleep.toString()
                    }
               }
            }


            nextDayButton.setOnClickListener(){
                time.decrDay()
                GlobalScope.launch(Dispatchers.Main) {
                    val numberOfSteps = readSteps(healthConnectClient, time.getStartTime(), time.getEndTime())
                    val timeOfSleep = readSleepDuration(healthConnectClient, time.getStartTime(), time.getEndTime())
                    runOnUiThread {
                        steps.text = numberOfSteps.toString()
                        dateOfTheDay.text = time.day.toString()
                        sleep.text = timeOfSleep.toString()
                    }
                }
            }
        // koniec przycisków ruchu dat




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
        if(response[StepsRecord.COUNT_TOTAL]?.toInt() == null) {
            return 0
        }
        else{
            return response[StepsRecord.COUNT_TOTAL]?.toInt()
        }
    }

    suspend fun readSleepDuration(healthConnectClient : HealthConnectClient, startTime: Instant, endTime: Instant): AggregateMetric<Duration> {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        for (sleepRecord in response.records) {
            // Process each exercise record
            // Optionally pull in sleep stages of the same time range
            val sleepStageRecords =
                healthConnectClient
                    .readRecords(
                        ReadRecordsRequest(
                            SleepStageRecord::class,
                            timeRangeFilter =
                            TimeRangeFilter.between(sleepRecord.startTime, sleepRecord.endTime)
                        )
                    )
                    .records
        }
        val sleep = SleepSessionRecord.SLEEP_DURATION_TOTAL
        return sleep
    }
    fun InitializeGoogle(): HealthConnectClient {
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

        return HealthConnectClient.getOrCreate(this@AllActivityScreen)
    }


}

