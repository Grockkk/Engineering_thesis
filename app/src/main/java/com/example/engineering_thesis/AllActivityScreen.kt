package com.example.engineering_thesis

import Data.*
import Time.Time
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
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
import java.util.*


class AllActivityScreen : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener{

    private lateinit var gsc: GoogleSignInClient
    private lateinit var healthConnectClient : HealthConnectClient
    private lateinit var nextDayButton: ImageButton
    private lateinit var prevDayButton: ImageButton
    private lateinit var HeartIcon: ImageView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBarSteps: ProgressBar
    //initialize textView and buttons
    //private lateinit var stepsView: TextView
    private lateinit var dateOfTheDayView: TextView
    private lateinit var sleepView: TextView
    private lateinit var heartRatView: TextView
    //private lateinit var distanceView: TextView
    //private lateinit var caloriesView: TextView

    private lateinit var relativeLayoutSteps: RelativeLayout
    private lateinit var relativeLayoutSleep: RelativeLayout
    private lateinit var relativeLayoutHeartRatio: RelativeLayout


    private val time = Time()
    private val cal = BurnCal()
    private val dst = Distance()
    private val hr = HeartRatio()
    private val st = Steps()
    private val sl = Sleep()

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_screen)

        //NavBar
        navBarMenu()

        // logowanie google
        healthConnectClient = initializeGoogle()

        //stepsView = findViewById(R.id.ID_totalSteps)
        dateOfTheDayView = findViewById(R.id.ID_currnetDay)
        //HeartIcon = findViewById(R.id.ID_Heart_icon)
        sleepView = findViewById(R.id.ID_sleep)
        heartRatView = findViewById(R.id.ID_hr)
        //distanceView = findViewById(R.id.ID_dist)
        //caloriesView = findViewById(R.id.ID_calories)
        //progressBarSteps = findViewById(R.id.stats_progressbar)
        //var burnedCal: TextView = findViewById(R.id.burned_cal)

        relativeLayoutSleep = findViewById(R.id.sleep_Layout)
        relativeLayoutSteps = findViewById(R.id.steps_Layout)
        relativeLayoutHeartRatio = findViewById(R.id.heart_ratio_Layout)
        //refresh
        swipeRefreshLayout = findViewById(R.id.ID_swipe)
        swipeRefreshLayout.setOnRefreshListener(this)

        prevDayButton = findViewById(R.id.ID_prevDay)
        nextDayButton = findViewById(R.id.ID_nextDay)
        dateOfTheDayView.text = time.day.toString()

        swipeRefreshLayout.setOnRefreshListener {
            onRefresh()
        }

        initializeData()


        // przyciski ruchu dat
            prevDayButton.setOnClickListener(){
                time.incrDay()
                initializeData()
            }

            nextDayButton.setOnClickListener(){
                time.decrDay()
                initializeData()
            }
        // koniec przycisków ruchu dat

        relativeLayoutSleep.setOnClickListener(){
            Toast.makeText(applicationContext,"nice",Toast.LENGTH_SHORT).show()
        }

        relativeLayoutSteps.setOnClickListener(){
            Toast.makeText(applicationContext,"nice",Toast.LENGTH_SHORT).show()
        }

        relativeLayoutHeartRatio.setOnClickListener(){
            Toast.makeText(applicationContext,"nice",Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("SetTextI18n")
    fun initializeGoogle(): HealthConnectClient {
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

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    fun initializeData(){
        GlobalScope.launch(Dispatchers.Main) {
            val numberOfSteps = st.readSteps(healthConnectClient, time.getStartTime(), time.getEndTime())
            val timeOfSleep = sl.readSleepDuration(healthConnectClient, time.getStartTime(), time.getEndTime())
            hr.aggregateHeartRate(healthConnectClient, time.getStartTime(), time.getEndTime())
            val totalDistance = dst.readDistance(healthConnectClient, time.getStartTime(), time.getEndTime())
            //val calories = cal.readBurnedCalories(healthConnectClient, time.getStartTime(), time.getEndTime())
            runOnUiThread {
                dateOfTheDayView.text = time.day.toString()
                //stepsView.text = numberOfSteps.toString()
                sleepView.text = timeOfSleep.toString()
                heartRatView.text = hr.getMeanHeartRate().toString()+ " BPM"
                //distanceView.text = totalDistance.toString() + " km"
                //caloriesView.text = calories.toString()
            }
        }
    }

    private fun navBarMenu(){
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.naView)
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ID_settings -> Toast.makeText(applicationContext,"nice",Toast.LENGTH_SHORT).show()
                R.id.ID_profile -> navigateToProfile()
                R.id.ID_addAct -> Toast.makeText(applicationContext,"nice",Toast.LENGTH_SHORT).show()
                R.id.ID_startAct -> Toast.makeText(applicationContext,"nice",Toast.LENGTH_SHORT).show()
                R.id.ID_singOut -> singOut()
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
        initializeData()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun navigateToProfile() {
        finish()
        val intent = Intent(this, YourProfileActivity::class.java)
        startActivity(intent)
    }
}

