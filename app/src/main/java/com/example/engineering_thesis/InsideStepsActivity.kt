package com.example.engineering_thesis

import Data.BurnCal
import Data.Distance
import Data.Steps
import Time.Time
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.units.kilometers
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.*

class InsideStepsActivity : AppCompatActivity() {
    private lateinit var healthConnectClient : HealthConnectClient
    private lateinit var gsc: GoogleSignInClient

    private lateinit var TotalSteps: TextView
    private lateinit var MaxSteps: TextView
    private lateinit var MinSteps: TextView

    private lateinit var TotalDist: TextView
    private lateinit var MaxDist: TextView
    private lateinit var MinDist: TextView

    private lateinit var TotalCal: TextView
    private lateinit var MaxCal: TextView
    private lateinit var MinCal: TextView

    private val time = Time()
    private val st = Steps()
    private val dt = Distance()
    private val bc = BurnCal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inside_steps)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        healthConnectClient = initializeGoogle()

        TotalSteps = findViewById(R.id.ID_TotalSteps)
        MaxSteps = findViewById(R.id.ID_weeklyMAXSteps)
        MinSteps = findViewById(R.id.ID_weeklyMINSteps)

        TotalDist = findViewById(R.id.ID_TotalDistance)
        MaxDist = findViewById(R.id.ID_weeklyMAXDistance)
        MinDist = findViewById(R.id.ID_weeklyMINDistance)

        TotalCal = findViewById(R.id.ID_TotalCalories)
        MaxCal = findViewById(R.id.ID_weeklyMAXCalories)
        MinCal = findViewById(R.id.ID_weeklyMINCalories)



        initializeData(time.getStartTimeWeek(),time.getEndTimeWeek())

        GetData(time.getStartTimeWeek(),time.getEndTimeWeek())

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

    @SuppressLint("SetTextI18n")
    fun initializeGoogle(): HealthConnectClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        return HealthConnectClient.getOrCreate(this@InsideStepsActivity)
    }

    private fun initializeData(startTime: Instant,endTime: Instant, ) {
        GlobalScope.launch(Dispatchers.Main){
            val tab = ArrayList<BarEntry>()

            val results = st.aggregateStepsWithDates(
                healthConnectClient,
                LocalDateTime.ofInstant(startTime, ZoneOffset.UTC).with(LocalTime.MIN),
                LocalDateTime.ofInstant(endTime,ZoneOffset.UTC).with(LocalTime.MAX))

            var index = 0f // Numer indeksu dla osi X
            val xAxisLabels = ArrayList<String>()

            for (result in results) {
                val date = result.first // Zakładam, że wynik zawiera datę
                val steps = result.second

                if (steps != null) {
                    tab.add(BarEntry(index, steps.toFloat()))
                }

                var d = ""
                var m = ""

                m = if(date.toLocalDate().month.value < 10){
                    "0" + date.toLocalDate().month.value.toString()
                }else{
                    date.toLocalDate().month.value.toString()
                }

                d = if(date.toLocalDate().dayOfMonth < 10){
                    "0" + date.toLocalDate().dayOfMonth.toString()
                }else{
                    date.toLocalDate().dayOfMonth.toString()
                }

                xAxisLabels.add("$d-$m")

                index += 1f // Zwiększ indeks dla następnej daty

                Log.d("MyApp","$steps")
                Log.d("MyApp","$date")

                val barDataSet = BarDataSet(tab, "Steps Data")
                barDataSet.color = (Color.rgb(13, 53, 101))
                val data = BarData(barDataSet)

                val barChart: BarChart = findViewById(R.id.barChart)
                barChart.data = data
                barChart.setDrawGridBackground(false)

                val l = barChart.legend
                barChart.description.text = ""
                barChart.setDrawGridBackground(false)

                // Ustawienie formatera osi X
                val xAxis = barChart.xAxis
                xAxis.setDrawGridLines(false)
                xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels) // Ustawienie etykiet
                xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE // Ustawienie położenia osi X

                barChart.invalidate()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun GetData(startTime: Instant, endTime: Instant){
        GlobalScope.launch(Dispatchers.Main){
            val tabSteps = st.getWeeklyData(
                healthConnectClient,
                LocalDateTime.ofInstant(startTime, ZoneOffset.UTC).with(LocalTime.MIN),
                LocalDateTime.ofInstant(endTime,ZoneOffset.UTC).with(LocalTime.MAX))
            TotalSteps.text = tabSteps[0].toString()
            MaxSteps.text = tabSteps[1].toString()
            MinSteps.text = tabSteps[2].toString()

            val tabDist = dt.getWeeklyData(
                healthConnectClient,
                LocalDateTime.ofInstant(startTime, ZoneOffset.UTC).with(LocalTime.MIN),
                LocalDateTime.ofInstant(endTime,ZoneOffset.UTC).with(LocalTime.MAX))

            TotalDist.text = dt.readDistance(
                healthConnectClient,
                startTime,
                endTime).toString()
            MaxDist.text = tabDist[0].kilometers.toString()
            MinDist.text = tabDist[1].kilometers.toString()

            val tabCal = bc.getWeeklyData(
                healthConnectClient,
                LocalDateTime.ofInstant(startTime, ZoneOffset.UTC).with(LocalTime.MIN),
                LocalDateTime.ofInstant(endTime,ZoneOffset.UTC).with(LocalTime.MAX))

            TotalCal.text = bc.readBurnedCalories(
                healthConnectClient,
                startTime,
                endTime) + " kcal"

            val maxcal = "%.0f".format(tabCal[0])
            val mincal = "%.0f".format(tabCal[1])

            MaxCal.text = "$maxcal kcal"
            MinCal.text = "$mincal kcal"
        }
    }
}


/*val lineChart: LineChart = findViewById(R.id.lineChart)

        val entries = arrayListOf<Entry>()
        entries.add(Entry(1f, 5f))
        entries.add(Entry(2f, 8f))
        entries.add(Entry(3f, 6f))
        entries.add(Entry(4f, 10f))
        entries.add(Entry(5f, 7f))

        val dataSet = LineDataSet(entries, "Example Line Chart")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate() // Refresh the chart

        val pieChart: PieChart = findViewById(R.id.pieChart)

        val entries2 = arrayListOf<PieEntry>()
        entries2.add(PieEntry(30f, "Label 1"))
        entries2.add(PieEntry(20f, "Label 2"))
        entries2.add(PieEntry(50f, "Label 3"))

        val dataSet2 = PieDataSet(entries2, "Example Pie Chart")
        dataSet2.colors = listOf(Color.RED, Color.GREEN, Color.BLUE)
        dataSet2.valueTextColor = Color.BLACK

        val pieData = PieData(dataSet2)
        pieChart.data = pieData
        pieChart.centerText = "hi"
        pieChart.invalidate() // Refresh the chart*/