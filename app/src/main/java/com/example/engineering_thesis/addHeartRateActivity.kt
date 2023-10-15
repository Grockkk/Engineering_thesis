package com.example.engineering_thesis

import RecycleTools.HeartRateSample
import RecycleTools.heartRateAdapter
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.health.connect.client.HealthConnectClient
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.time.*
import java.util.*

class addHeartRateActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener  {
    private lateinit var gsc: GoogleSignInClient
    private lateinit var healthConnectClient : HealthConnectClient

    private lateinit var startBtn: Button
    //private lateinit var endBtn: Button

    private lateinit var addBtn: Button
    private lateinit var cancelBtn: Button

    private lateinit var recycleView: RecyclerView

    private var isStartButton: Boolean = true

    private lateinit var addSample: EditText

    private var startTime: Instant = Instant.from((LocalDateTime.of(LocalDate.now(), LocalTime.MAX)).toInstant(
        ZoneOffset.UTC))
    private var endTime: Instant = Instant.from((LocalDateTime.of(LocalDate.now(), LocalTime.MAX)).toInstant(
        ZoneOffset.UTC))

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_heart_ratio_data)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        healthConnectClient = initializeGoogle()

        recycleView = findViewById(R.id.RecycleViewHR)

        var sampleList = mutableListOf(
            HeartRateSample("Measurement", "Time")
        )

        val adapter = heartRateAdapter(sampleList)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(this)

        startBtn =findViewById(R.id.ID_StartTimePicker)
        //endBtn =findViewById(R.id.ID_EndTimePicker)

        addSample =findViewById(R.id.ID_heartRate)

        addBtn = findViewById(R.id.ID_addDataSteps)
        cancelBtn = findViewById(R.id.ID_cancel)

        startBtn.setOnClickListener(){
            getDateTimeCalendar()
            isStartButton = true
            DatePickerDialog(this, this, year, month, day).show()
        }

        addSample.setOnClickListener {

        }
        
    }

    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayofMonth: Int) {
        savedDay = dayofMonth
        savedMonth = month+1
        savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(this,this,hour,minute,true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minuteOfDay: Int) {
        savedHour = hourOfDay
        savedMinute = minuteOfDay

        //dateString = "$savedDay-$savedMonth-$savedYear $savedHour:$savedMinute"
    }

    @SuppressLint("SetTextI18n")
    fun initializeGoogle(): HealthConnectClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        val acct = GoogleSignIn.getLastSignedInAccount(this)

        return HealthConnectClient.getOrCreate(this@addHeartRateActivity)
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