package com.example.engineering_thesis

import Data.WeightHeight
import Time.Time
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.health.connect.client.HealthConnectClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class YourProfileActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    var day = 0
    var month = 0
    var year = 0
    var BDay = 0
    var Bmonth = 0
    var Byear = 0


    var globalAge = GlobalClass()

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var healthConnectClient : HealthConnectClient
    private lateinit var gsc: GoogleSignInClient

    private lateinit var ageView: TextView
    private lateinit var heightView: TextView
    private lateinit var weightView: TextView
    private lateinit var dataBtn: Button
    private lateinit var nameView: TextView
    private lateinit var mailView: TextView

    private val wh = WeightHeight()
    private val time = Time()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_profile)



        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataBtn = findViewById(R.id.ID_changeDataButton)
        ageView = findViewById(R.id.ID_age)
        nameView = findViewById(R.id.ID_name)
        mailView = findViewById(R.id.ID_mail)
        weightView = findViewById(R.id.ID_Weight)
        heightView = findViewById(R.id.ID_Height)

        healthConnectClient = initializeGoogle()

        ageView.text = globalAge.birthday

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val birthday = sharedPreferences.getString("birthday", null)
        if (birthday != null) {
            ageView.text = birthday
            GlobalClass.instance.birthday = birthday
        }

        initializeData()
        dataBtn.setOnClickListener{
            showCustomDialogBox()
        }
    }



    private fun showCustomDialogBox() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.change_data_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnBirth: Button = dialog.findViewById(R.id.ID_changeDateBtn)
        val btnSubmit: Button = dialog.findViewById(R.id.ID_submit)
        val heightPicker: NumberPicker = dialog.findViewById(R.id.ID_changeHeight)
        val widthPicker: NumberPicker = dialog.findViewById(R.id.ID_changeWidth)

        widthPicker.minValue = 0
        widthPicker.maxValue = 500
        widthPicker.value = (wh.Weight).toInt()


        heightPicker.minValue = 0
        heightPicker.maxValue = 500
        heightPicker.value = (wh.Height * 100).toInt()

        btnBirth.setOnClickListener{
            getDateCalendar()
            DatePickerDialog(this,this,year,month,day).show()
        }

        btnSubmit.setOnClickListener{
            var h = (heightPicker.value).toDouble()
            h /= 100
            val w = (widthPicker.value).toDouble()

            GlobalScope.launch(Dispatchers.Main) {
                wh.writeWeightInput(healthConnectClient,w)
                wh.writeHeightInput(healthConnectClient,h)
            }
            initializeData()
            Toast.makeText(applicationContext,"Data has been changed",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getDateCalendar(){
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    @SuppressLint("SetTextI18n")
    fun initializeGoogle(): HealthConnectClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct!= null) {
            nameView.text = acct.displayName
            mailView.text = acct.email
        }

        return HealthConnectClient.getOrCreate(this@YourProfileActivity)
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

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int,day: Int) {
        BDay = day
        Bmonth = month+1
        Byear = year
        val formatter = DateTimeFormatter.ofPattern("d M yyyy") // Format daty, np. "27 6 2023"
        val date = LocalDate.parse("$BDay $Bmonth $Byear", formatter)
        GlobalClass.instance.birthday = date.toString()
        ageView.text = GlobalClass.instance.birthday



        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("birthday", GlobalClass.instance.birthday)
        editor.apply()
    }

    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    fun initializeData(){
        GlobalScope.launch(Dispatchers.Main) {
            wh.readWeightAndHeight(healthConnectClient, time.getStartTime(), time.getEndTime())
            weightView.text = wh.Weight.toString()
            heightView.text = ("%.0f".format(wh.Height*100))

        }
    }
}