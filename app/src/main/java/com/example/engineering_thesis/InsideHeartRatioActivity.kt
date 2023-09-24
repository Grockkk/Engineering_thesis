package com.example.engineering_thesis

import Data.HeartRatio
import Time.Time
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.health.connect.client.HealthConnectClient
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.*

class InsideHeartRatioActivity : AppCompatActivity() {
    private lateinit var healthConnectClient : HealthConnectClient
    private lateinit var gsc: GoogleSignInClient
    private val hr = HeartRatio()
    private val time = Time()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inside_heart_ratio)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        healthConnectClient = initializeGoogle()


        val barChart: BarChart = findViewById(R.id.barChart)
        val lineChart: LineChart = findViewById(R.id.lineChart)

        initializeBarChartData(time.getStartTimeWeek(),time.getEndTimeWeek(), barChart)
        initializelineChartData(time.getStartTime(),time.getEndTime(),lineChart)

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val x = e.x // Wartość x (dla słupka)
                    initializelineChartData(time.getStartTimeWeek().plusSeconds((86400*x).toLong()),time.getStartTimeWeek().plusSeconds((86400*(x)).toLong()),lineChart)
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
        return HealthConnectClient.getOrCreate(this@InsideHeartRatioActivity)
    }

    //////

    private fun initializeBarChartData(startTime: Instant, endTime: Instant, barChart: BarChart) {
        GlobalScope.launch(Dispatchers.Main) {
            val tab = ArrayList<BarEntry>()

            val results = hr.aggregateheartRatioForBarChart(
                healthConnectClient,
                LocalDateTime.ofInstant(startTime, ZoneOffset.UTC).with(LocalTime.MIN)
                    .minusSeconds(18000),
                LocalDateTime.ofInstant(endTime, ZoneOffset.UTC).with(LocalTime.MAX)
                    .minusSeconds(18000)
            )

            var index = 0f // Numer indeksu dla osi X
            val xAxisLabels = ArrayList<String>()
            for (result in results) {
                val date = result.first // Zakładam, że wynik zawiera datę
                val heartRatioAVG = result.second

                if (heartRatioAVG != null) {
                    tab.add(BarEntry(index, heartRatioAVG.toFloat()))
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
            }

            val barDataSet = BarDataSet(tab, "Average Heart Ratio")
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
    private fun initializelineChartData(startTime: Instant, endTime: Instant, lineChart: LineChart) {
        GlobalScope.launch(Dispatchers.Main) {

            val entries = arrayListOf<Entry>()
            val results = hr.aggregateheartRatioForLineChart(
                healthConnectClient,
                LocalDateTime.ofInstant(startTime, ZoneOffset.UTC).with(LocalTime.MIN),
                LocalDateTime.ofInstant(endTime, ZoneOffset.UTC).with(LocalTime.MAX)
            )
            val xAxisLabels = ArrayList<String>()
            var index = 0f
            var h = ""
            var m = ""

            for (outcome in results){
                if(outcome.second != null){
                    val averageHeartRatio = outcome.second!!.toFloat()
                    val date = outcome.first

                    entries.add(Entry(index, averageHeartRatio))
                    index += 1f

                    h = date.hour.toString()
                    m = date.minute.toString()

                    if(h.length <= 1){
                        h = "0$h"
                    }
                    if(m.length <= 1){
                        m = "0$m"
                    }

                    xAxisLabels.add("$h:$m")
                }
            }
            val xAxis = lineChart.xAxis
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
            xAxis.labelCount = 5
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            lineChart.description.text = ""
            // Teraz możesz dodać dane do wykresu liniowego

            val dataSet = LineDataSet(entries, "")
            dataSet.color = Color.RED
            dataSet.fillColor = Color.RED
            dataSet.setDrawCircles(false)
            dataSet.disableDashedLine()
            dataSet.setDrawFilled(true)
            val lineData = LineData(dataSet)
            lineChart.data = lineData
            lineChart.invalidate() // Odśwież wykres

        }
    }

}