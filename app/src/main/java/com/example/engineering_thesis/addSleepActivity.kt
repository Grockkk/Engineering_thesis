package com.example.engineering_thesis

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.health.connect.client.HealthConnectClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.time.*
import java.util.*

class addSleepActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener  {
    private lateinit var gsc: GoogleSignInClient
    private lateinit var healthConnectClient : HealthConnectClient

    private lateinit var startBtn: Button
    private lateinit var endBtn: Button
    private lateinit var dateString: String

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
        setContentView(R.layout.add_sleep_data)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        healthConnectClient = initializeGoogle()

        dateString = "Add"

        startBtn.setOnClickListener(){
            getDateTimeCalendar()
            DatePickerDialog(this, this, year, month, day).show()
            startBtn.text = dateString
        }

        endBtn.setOnClickListener(){
            getDateTimeCalendar()
            DatePickerDialog(this, this, year, month, day).show()
            endBtn.text = dateString
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

    private fun pickDate(){
            getDateTimeCalendar()
            DatePickerDialog(this,this,year,month,day).show()
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

        dateString = "$savedDay-$savedMonth-$savedYear $savedHour:$savedMinute"
    }

    @SuppressLint("SetTextI18n")
    fun initializeGoogle(): HealthConnectClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        val acct = GoogleSignIn.getLastSignedInAccount(this)

        return HealthConnectClient.getOrCreate(this@addSleepActivity)
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