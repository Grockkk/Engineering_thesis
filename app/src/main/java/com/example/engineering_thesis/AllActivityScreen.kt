package com.example.engineering_thesis

import Data.*
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
    //val cal = BurnCal();
    val dst = Distance();
    val hr = HeartRatio();
    val st = Steps();
    val sl = Sleep();
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_screen)

        // logowanie google
        val healthConnectClient = InitializeGoogle()

        //initialize textView and buttons
        var steps: TextView = findViewById(R.id.total_steps)
        var dateOfTheDay: TextView = findViewById(R.id.currnet_day)
        var sleep: TextView = findViewById(R.id.sleep)
        var heartRat: TextView = findViewById(R.id.hr)
        var dinstance: TextView = findViewById(R.id.dist)
        //var burnedCal: TextView = findViewById(R.id.burned_cal)
        prevDayButton = findViewById(R.id.prev_day)
        nextDayButton = findViewById(R.id.next_day)
        dateOfTheDay.text = time.day.toString()

            GlobalScope.launch(Dispatchers.Main) {
                val numberOfSteps = st.readSteps(healthConnectClient, time.getStartTime(), time.getEndTime())
                val timeOfSleep = sl.readSleepDuration(healthConnectClient, time.getStartTime(), time.getEndTime())
                val meanBPM = hr.aggregateHeartRate(healthConnectClient, time.getStartTime(), time.getEndTime())
                val totalDistance = dst.readDistance(healthConnectClient, time.getStartTime(), time.getEndTime())
                //val calories = cal.readBurnedCalories(healthConnectClient, time.getStartTime(), time.getEndTime())
                runOnUiThread {
                    steps.text = "Ilość kroków tego dnia: " + numberOfSteps.toString()
                    sleep.text = "Całkowity czas snu: " + timeOfSleep
                    heartRat.text = "Średne bicie serca to: " + hr.getMeanHeartRate().toString()
                    dinstance.text = "Przebyta odległość: " + totalDistance.toString() + " meters"
                    //burnedCal.text = calories.toString()
                }
            }

        // przyciski ruchu dat

            prevDayButton.setOnClickListener(){
                time.incrDay()
                GlobalScope.launch(Dispatchers.Main) {
                    val numberOfSteps = st.readSteps(healthConnectClient, time.getStartTime(), time.getEndTime())
                    val timeOfSleep = sl.readSleepDuration(healthConnectClient, time.getStartTime(), time.getEndTime())
                    val meanBPM = hr.aggregateHeartRate(healthConnectClient, time.getStartTime(), time.getEndTime())
                    val totalDistance = dst.readDistance(healthConnectClient, time.getStartTime(), time.getEndTime())
                    //val calories = cal.readBurnedCalories(healthConnectClient, time.getStartTime(), time.getEndTime())
                    runOnUiThread {
                        steps.text = "Ilość kroków tego dnia: " + numberOfSteps.toString()
                        dateOfTheDay.text = time.day.toString()
                        sleep.text = "Całkowity czas snu: " + timeOfSleep
                        heartRat.text = "Średne bicie serca to: " + hr.getMeanHeartRate().toString()
                        dinstance.text = "Przebyta odległość: " + totalDistance.toString() + " meters"
                        //burnedCal.text = calories.toString()
                    }
               }
            }

            nextDayButton.setOnClickListener(){
                time.decrDay()
                GlobalScope.launch(Dispatchers.Main) {
                    val numberOfSteps = st.readSteps(healthConnectClient, time.getStartTime(), time.getEndTime())
                    val timeOfSleep = sl.readSleepDuration(healthConnectClient, time.getStartTime(), time.getEndTime())
                    val meanBPM = hr.aggregateHeartRate(healthConnectClient, time.getStartTime(), time.getEndTime())
                    val totalDistance = dst.readDistance(healthConnectClient, time.getStartTime(), time.getEndTime())
                    //val calories = cal.readBurnedCalories(healthConnectClient, time.getStartTime(), time.getEndTime())
                    runOnUiThread {
                        steps.text = "Ilość kroków tego dnia: " + numberOfSteps.toString()
                        dateOfTheDay.text = time.day.toString()
                        sleep.text = "Całkowity czas snu: " + timeOfSleep
                        heartRat.text = "Średne bicie serca to: " + hr.getMeanHeartRate().toString()
                        dinstance.text = "Przebyta odległość: " + totalDistance.toString() + " meters"
                        //burnedCal.text = calories.toString()
                    }
                }
            }
        // koniec przycisków ruchu dat

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
            person_name.text = "Witaj " + personName

        }

        return HealthConnectClient.getOrCreate(this@AllActivityScreen)
    }
}

