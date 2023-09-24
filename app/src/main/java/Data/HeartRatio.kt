package Data

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.*

class HeartRatio {
    private var minimumHeartRate: Long? = null
    private var maximumHeartRate: Long? = null
    private var meanHeartRate: Long? = null

    suspend fun aggregateHeartRate(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            val response =
                healthConnectClient.aggregate(
                    AggregateRequest(
                        setOf(HeartRateRecord.BPM_MAX, HeartRateRecord.BPM_MIN,HeartRateRecord.BPM_AVG),
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
            // The result may be null if no data is available in the time range.
            minimumHeartRate = response[HeartRateRecord.BPM_MIN]
            maximumHeartRate = response[HeartRateRecord.BPM_MAX]
            meanHeartRate = response[HeartRateRecord.BPM_AVG]
        } catch (e: Exception) {
            // Run error handling here.
        }
    }

    fun getMinimumHeartRate(): Long? {
        if (minimumHeartRate != null){
            return minimumHeartRate
        }else{
            return 0;
        }

    }
    fun getMaximumHeartRate(): Long? {
        if (maximumHeartRate != null){
            return maximumHeartRate
        }else{
            return 0;
        }
    }
    fun getMeanHeartRate(): Long? {
        if (meanHeartRate != null){
            return meanHeartRate
        }else{
            return 0;
        }
    }

    //////

    suspend fun aggregateheartRatioForBarChart(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Pair<LocalDateTime, Long?>> {
        try {
            val aggregatedResults = healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(HeartRateRecord.BPM_AVG),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )
            var index = 0
            val hrRecords = mutableListOf<Pair<LocalDateTime, Long?>>()
            for (result in aggregatedResults) {
                val date = result.endTime// koniec okresu, czyli data pomiaru
                val AVGHeartRatio = result.result[HeartRateRecord.BPM_AVG]

                while (endTime.minusDays(7).dayOfMonth.plus(index) != date.dayOfMonth){
                    hrRecords.add(Pair(LocalDateTime.now().minusDays(7).plusDays(index.toLong()),0))
                    index += 1
                }
                hrRecords.add(Pair(date, AVGHeartRatio))
                index += 1
            }
            return hrRecords
        } catch (e: Exception) {
            // Obsługa błędów
            Log.e("AggregationError", "An error occurred during aggregation: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun aggregateheartRatioForLineChart(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ):List<Pair<LocalDateTime, Long?>> {
        try {
            val response =
                healthConnectClient.aggregateGroupByDuration(
                    AggregateGroupByDurationRequest(
                        metrics = setOf(HeartRateRecord.BPM_AVG),
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                        timeRangeSlicer = Duration.ofMinutes(10L)
                    )
                )
            val heartRatioRecords = mutableListOf<Pair<LocalDateTime, Long?>>()

            for (record in response) {
                // The result may be null if no data is available in the time range
                val averageHeartRatio = record.result[HeartRateRecord.BPM_AVG]
                val date = LocalDateTime.ofInstant(record.startTime, ZoneOffset.systemDefault())
                Log.e("hr","$averageHeartRatio")
                Log.e("time","$date")
                heartRatioRecords.add(Pair(date,averageHeartRatio))
            }
            return heartRatioRecords
        } catch (e: Exception) {
            return emptyList()
        }
    }
}