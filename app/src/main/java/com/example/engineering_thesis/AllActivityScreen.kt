package com.example.engineering_thesis

import Data.*
import Time.Time
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.health.connect.client.HealthConnectClient
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*



class AllActivityScreen : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener{

    private lateinit var gsc: GoogleSignInClient
    private lateinit var healthConnectClient : HealthConnectClient
    private lateinit var nextDayButton: Button
    private lateinit var prevDayButton: Button
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    //initialize textView and buttons
    private lateinit var stepsView: TextView
    private lateinit var dateOfTheDayView: TextView
    private lateinit var sleepView: TextView
    private lateinit var heartRatView: TextView
    private lateinit var distanceView: TextView

    val time = Time()
    //val cal = BurnCal()
    val dst = Distance()
    val hr = HeartRatio()
    val st = Steps()
    val sl = Sleep()

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_screen)

        //NavBar
        navBarMenu()

        // logowanie google
        healthConnectClient = InitializeGoogle()

        stepsView = findViewById(R.id.ID_totalSteps)
        dateOfTheDayView = findViewById(R.id.ID_currnetDay)
        sleepView = findViewById(R.id.ID_sleep)
        heartRatView = findViewById(R.id.ID_hr)
        distanceView = findViewById(R.id.ID_dist)
        //var burnedCal: TextView = findViewById(R.id.burned_cal)



        //refresh
        swipeRefreshLayout = findViewById(R.id.ID_swipe)
        swipeRefreshLayout.setOnRefreshListener(this)


        time.setDateToday()
        prevDayButton = findViewById(R.id.ID_prevDay)
        nextDayButton = findViewById(R.id.ID_nextDay)
        dateOfTheDayView.text = time.day.toString()

        swipeRefreshLayout.setOnRefreshListener {
            // wykonaj kod w momencie odświeżenia
            onRefresh()
        }

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
            val personName = acct.displayName
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

    fun navBarMenu(){
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.naView)
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ID_settings -> Toast.makeText(applicationContext,"nice",Toast.LENGTH_SHORT).show()
                R.id.ID_profile -> Toast.makeText(applicationContext,"nice",Toast.LENGTH_SHORT).show()
                R.id.ID_addAct -> Toast.makeText(applicationContext,"nice",Toast.LENGTH_SHORT).show()
                R.id.ID_startAct -> Toast.makeText(applicationContext,"nice",Toast.LENGTH_SHORT).show()
                R.id.ID_singOut -> singOut();
            }
            true
        }
    }

    private fun singOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        gsc.signOut().addOnCompleteListener { task ->
            finish()
            startActivity(Intent(this@AllActivityScreen, MainActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        refreshData()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun refreshData() {
        initializeData(healthConnectClient,stepsView,sleepView,heartRatView,distanceView,dateOfTheDayView)
    }
}
