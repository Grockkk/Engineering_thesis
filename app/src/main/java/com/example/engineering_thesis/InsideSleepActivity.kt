package com.example.engineering_thesis

import Data.Sleep
import Time.Time
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class InsideSleepActivity : AppCompatActivity() {
    private lateinit var healthConnectClient : HealthConnectClient
    private lateinit var gsc: GoogleSignInClient

    private lateinit var awake: TextView
    private lateinit var light: TextView
    private lateinit var deep: TextView
    private lateinit var REM: TextView
    private lateinit var unknown: TextView

    private val time = Time()
    private val sl = Sleep()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inside_sleep)

        healthConnectClient = initializeGoogle()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val barChart: BarChart = findViewById(R.id.barChart)

        awake = findViewById(R.id.ID_awake)
        light = findViewById(R.id.ID_light)
        deep = findViewById(R.id.ID_deep)
        REM = findViewById(R.id.ID_REM)
        unknown = findViewById(R.id.ID_unknown)

        initializeBarChartData(time.getStartTimeWeek(),time.getEndTimeWeek(), barChart)
        initializePieChartData(time.getStartTime(),time.getEndTime())


        // Dodajemy słuchacza zdarzeń na wykresie
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val x = e.x // Wartość x (dla słupka)
                    initializePieChartData(time.getStartTimeWeek().plusSeconds((86400*x).toLong()),time.getStartTimeWeek().plusSeconds((86400*(x)).toLong()))
                }
            }

            override fun onNothingSelected() {
                // Wywoływane, gdy żaden słupek nie jest zaznaczony
            }
        })
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
        return HealthConnectClient.getOrCreate(this@InsideSleepActivity)
    }

    @SuppressLint("SetTextI18n")
    private fun initializePieChartData(startTime: Instant, endTime: Instant){
        lifecycleScope.launch {

            val sleepStagesRecords = sl.readSleepSessionsStages(
                healthConnectClient,
                LocalDateTime.ofInstant(startTime, ZoneOffset.UTC).with(LocalTime.MIN).minusSeconds(18000),
                LocalDateTime.ofInstant(endTime, ZoneOffset.UTC).with(LocalTime.MAX).minusSeconds(18000))

            val entries = arrayListOf<PieEntry>()
            var totalDuration = 0L
            for (result in sleepStagesRecords){
                var duration = result.first
                val label = result.second
                totalDuration += duration
                entries.add(PieEntry(duration.toFloat(), "$label [%]"))

                var h = 0

                while(duration >= 60){
                    h += 1
                    duration -= 60
                }
                if(sleepStagesRecords.size == 3){
                    awake.text = "no data"
                    unknown.text = "no data"
                }
                when(label){
                    "Awake" -> awake.text = "$h h $duration min"
                    "Light" -> light.text = "$h h $duration min"
                    "Deep" -> deep.text = "$h h $duration min"
                    "REM" -> REM.text = "$h h $duration min"
                    "Unknown" -> unknown.text = "$h h $duration min"
                }
            }
            val dataSet = PieDataSet(entries, "")
            dataSet.colors = listOf(
                Color.rgb(168,252,151),
                Color.rgb(158,114,186),
                Color.rgb(0,234,244),
                Color.rgb(249,248,113),
                Color.GRAY
            )
            dataSet.valueTextColor = Color.BLACK


            val pieChart: PieChart = findViewById(R.id.pieChart)
            val pieData = PieData(dataSet)
            pieChart.data = pieData

            pieData.setValueTextSize(13f)
            pieData.setValueTextColor(Color.BLACK)

            var h = 0
            while(totalDuration >= 60){
                h += 1
                totalDuration -= 60
            }

            pieChart.centerText = "Duration:\n\n$h h $totalDuration min"
            pieChart.setUsePercentValues(true)
            pieChart.setCenterTextColor(Color.BLACK)
            pieChart.setCenterTextSize(14f) // Zwiększenie rozmiaru czcionki do 14 punktów
            pieChart.setDrawCenterText(true)
            pieChart.setDrawEntryLabels(true)
            pieChart.setEntryLabelColor(Color.BLACK)
            pieChart.setEntryLabelTextSize(10f)
            pieChart.holeRadius = 50f
            pieChart.transparentCircleRadius = 61f
            pieChart.animateY(1000,Easing.EaseInOutBack)
            pieChart.setHoleColor(Color.rgb(143,194,255))

            // Usunięcie Description label
            pieChart.description.isEnabled = false

            val l = pieChart.legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            l.setDrawInside(false) // Nie rysuj legendy wewnątrz wykresu
            l.textColor = Color.BLACK

            pieChart.invalidate()
        }
    }

    private fun initializeBarChartData(startTime: Instant, endTime: Instant, barChart: BarChart) {

        lifecycleScope.launch(Dispatchers.Main) {
        val tab = ArrayList<BarEntry>()

        val results =sl.aggregateSleepForChart(
            healthConnectClient,
            LocalDateTime.ofInstant(startTime, ZoneOffset.UTC).with(LocalTime.MIN)
                .minusSeconds(18000),
            LocalDateTime.ofInstant(endTime, ZoneOffset.UTC).with(LocalTime.MAX)
                .minusSeconds(18000)
        )

        var index = 0f // Numer indeksu dla osi X
        val xAxisLabels = ArrayList<String>()
        for (result in results) {

            val date = result.first
            val duration = result.second
            Log.e("result",result.second.toString())
            if (duration != null) {
                tab.add(BarEntry(index, duration.toFloat()))
            }
            var d = ""
            var m = ""

            m = if (date.toLocalDate().month.value < 10) {
                "0" + date.toLocalDate().month.value.toString()
            } else {
                date.toLocalDate().month.value.toString()
            }

            d = if (date.toLocalDate().dayOfMonth < 10) {
                "0" + date.toLocalDate().dayOfMonth.toString()
            } else {
                date.toLocalDate().dayOfMonth.toString()
            }

            xAxisLabels.add("$d-$m")
            index += 1f // Zwiększ indeks dla następnej daty
        }

        val barDataSet = BarDataSet(tab, "Duration of sleep [min]")
        barDataSet.color = (Color.rgb(13, 53, 101))
        val data = BarData(barDataSet)

        barChart.data = data
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.animateY(1000, Easing.EaseInOutBack)
        val xAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels) // Ustawienie etykiet
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        val l = barChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.setDrawInside(false) // Nie rysuj legendy wewnątrz wykresu

        barChart.invalidate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val pieChart: PieChart = findViewById(R.id.pieChart)
        val barChart: BarChart = findViewById(R.id.barChart)
        pieChart.clear()
        barChart.clear()

        lifecycleScope.coroutineContext.cancelChildren()
    }
}