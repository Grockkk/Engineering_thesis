package com.example.engineering_thesis

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart

class InsideHeartRatioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inside_heart_ratio)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val barChart: BarChart = findViewById(R.id.barChart)
        val lineChart: LineChart = findViewById(R.id.lineChart)
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