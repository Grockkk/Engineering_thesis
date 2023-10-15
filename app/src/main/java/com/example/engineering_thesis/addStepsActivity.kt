package com.example.engineering_thesis

import Data.Distance
import Data.Steps
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.*
import java.util.*

class addStepsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener  {
    private lateinit var gsc: GoogleSignInClient
    private lateinit var healthConnectClient : HealthConnectClient

    private val st = Steps()
    private val dst = Distance()

    private lateinit var startBtn: Button
    private lateinit var endBtn: Button

    private lateinit var addBtn: Button
    private lateinit var cancelBtn: Button

    private var isStartButton: Boolean = true

    private lateinit var addSteps: EditText
    private lateinit var addDist: EditText

    private var startTime: Instant = Instant.from((LocalDateTime.of(LocalDate.now(), LocalTime.MAX)).toInstant(ZoneOffset.UTC))
    private var endTime: Instant = Instant.from((LocalDateTime.of(LocalDate.now(), LocalTime.MAX)).toInstant(ZoneOffset.UTC))

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
        setContentView(R.layout.add_steps_data)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        healthConnectClient = initializeGoogle()

        startBtn =findViewById(R.id.ID_StartTimePicker)
        endBtn =findViewById(R.id.ID_EndTimePicker)

        addSteps =findViewById(R.id.ID_numberOfSteps)
        addDist =findViewById(R.id.ID_distance)

        addBtn = findViewById(R.id.ID_addDataSteps)
        cancelBtn = findViewById(R.id.ID_cancel)

        startBtn.setOnClickListener(){
            getDateTimeCalendar()
            isStartButton = true
            DatePickerDialog(this, this, year, month, day).show()
        }

        endBtn.setOnClickListener(){
            getDateTimeCalendar()
            isStartButton = false
            DatePickerDialog(this, this, year, month, day).show()
        }

        addBtn.setOnClickListener(){
            val number = addSteps.text.toString()

            if(number != "" && startTime.isBefore(endTime)){
                //Toast.makeText(applicationContext,addSteps.text,Toast.LENGTH_SHORT).show()
                lifecycleScope.launch(Dispatchers.Main) {
                st.writeSteps(healthConnectClient,number.toLong(),startTime,endTime)
                if(addDist.text.toString() != ""){
                    dst.writeDistance(healthConnectClient,addDist.text.toString().toDouble(),startTime,endTime)
                }
                Toast.makeText(applicationContext,"Data successfully added",Toast.LENGTH_SHORT).show()
                navigateToAllActivity()
                }
            }
            else if(endTime.isBefore(startTime)){
                Toast.makeText(applicationContext,"end time can not be before start time",Toast.LENGTH_SHORT).show()
            }else if(number == ""){
                Toast.makeText(applicationContext,"enter number of steps",Toast.LENGTH_SHORT).show()
            }
        }
        cancelBtn.setOnClickListener(){
            startTime =Instant.from((LocalDateTime.of(LocalDate.now(), LocalTime.MAX)).toInstant(ZoneOffset.UTC))
            endTime =Instant.from((LocalDateTime.of(LocalDate.now(), LocalTime.MAX)).toInstant(ZoneOffset.UTC))
            startBtn.text = "Add"
            endBtn.text = "Add"
            addSteps.text = null
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

        if(isStartButton){
            startBtn.text = "$savedDay-$savedMonth-$savedYear $savedHour:$savedMinute"
            val localDateTime = LocalDateTime.of(savedYear, savedMonth, savedDay, savedHour, savedMinute)
            startTime = localDateTime.toInstant(ZoneOffset.UTC)
        }
        else{
            endBtn.text = "$savedDay-$savedMonth-$savedYear $savedHour:$savedMinute"
            val localDateTime = LocalDateTime.of(savedYear, savedMonth, savedDay, savedHour, savedMinute)
            endTime = localDateTime.toInstant(ZoneOffset.UTC)
        }
    }

    @SuppressLint("SetTextI18n")
    fun initializeGoogle(): HealthConnectClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        val acct = GoogleSignIn.getLastSignedInAccount(this)

        return HealthConnectClient.getOrCreate(this@addStepsActivity)
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

    private fun navigateToAllActivity() {
        finish()
        val intent = Intent(this, AllActivityScreen::class.java)
        startActivity(intent)
    }
}