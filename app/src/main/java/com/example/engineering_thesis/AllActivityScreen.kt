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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.*
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

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_screen)


        // logowanie google
        val healthConnectClient = InitializeGoogle()

        //initialize textView and buttons
        var stepsView: TextView = findViewById(R.id.ID_totalSteps)
        var dateOfTheDayView: TextView = findViewById(R.id.ID_currnetDay)
        var sleepView: TextView = findViewById(R.id.ID_sleep)
        var heartRatView: TextView = findViewById(R.id.ID_hr)
        var distanceView: TextView = findViewById(R.id.ID_dist)
        //var burnedCal: TextView = findViewById(R.id.burned_cal)

        prevDayButton = findViewById(R.id.ID_prevDay)
        nextDayButton = findViewById(R.id.ID_nextDay)
        dateOfTheDayView.text = time.day.toString()

        initializeData(healthConnectClient,stepsView,sleepView,heartRatView,distanceView,dateOfTheDayView)

        // przyciski ruchu dat
            prevDayButton.setOnClickListener(){
                time.incrDay()
                initializeData(healthConnectClient,stepsView,sleepView,heartRatView,distanceView,dateOfTheDayView)
            }

            nextDayButton.setOnClickListener(){
                time.decrDay()
                initializeData(healthConnectClient,stepsView,sleepView,heartRatView,distanceView,dateOfTheDayView)
            }
        // koniec przycisków ruchu dat

        stepsView.setOnClickListener(){

        }

        sleepView.setOnClickListener(){

        }

        heartRatView.setOnClickListener(){

        }

        distanceView.setOnClickListener(){

        }

    }
    @SuppressLint("SetTextI18n")
    fun InitializeGoogle(): HealthConnectClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct!= null) {
            val personName = acct.displayName;
            val actionBar = supportActionBar
            actionBar?.title = "Witaj " + personName.toString()
        }

        return HealthConnectClient.getOrCreate(this@AllActivityScreen)
    }
    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    fun initializeData(healthConnectClient: HealthConnectClient, steps: TextView, sleep: TextView, heartRat: TextView, distance: TextView, dateOfTheDay:TextView){
        GlobalScope.launch(Dispatchers.Main) {

            val numberOfSteps = st.readSteps(healthConnectClient, time.getStartTime(), time.getEndTime())
            val timeOfSleep = sl.readSleepDuration(healthConnectClient, time.getStartTime(), time.getEndTime())
            val meanBPM = hr.aggregateHeartRate(healthConnectClient, time.getStartTime(), time.getEndTime())
            val totalDistance = dst.readDistance(healthConnectClient, time.getStartTime(), time.getEndTime())
            //val calories = cal.readBurnedCalories(healthConnectClient, time.getStartTime(), time.getEndTime())
            runOnUiThread {
                dateOfTheDay.text = time.day.toString()
                steps.text = "Ilość kroków tego dnia: " + numberOfSteps.toString()
                sleep.text = "Całkowity czas snu: " + timeOfSleep
                heartRat.text = "Średne bicie serca to: " + hr.getMeanHeartRate().toString()+ " BPM"
                distance.text = "Przebyta odległość: " + totalDistance.toString() + " km"
                //burnedCal.text = calories.toString()
            }
        }
    }
}

