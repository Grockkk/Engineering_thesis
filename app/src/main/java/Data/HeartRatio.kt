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
            val hrRecords = mutableListOf<Pair<LocalDateTime, Long?>>()
            for (result in aggregatedResults) {
                val date = result.startTime
                val AVGHeartRatio = result.result[HeartRateRecord.BPM_AVG]
                Log.e("hr","$AVGHeartRatio")
                Log.e("time","$date")
                hrRecords.add(Pair(date, AVGHeartRatio))
            }

            var outcome = mutableListOf<Pair<LocalDateTime, Long?>>()

            // Tworzymy pętle wyjściową metody i wypełniamy zerami
            for (i in 1..8) {
                outcome.add(Pair(startTime.plusDays((i-1).toLong()),0))
            }

            // Tworzymy indeks którym będziemy iterować po outcomie
            var index = 0

            // W pętli sprawdzamy czy daty w sleepRecords oraz outcome się zgadzają, jeśli tak to
            // outcome[index] przyjmuje wartość value oraz zwiększamy index
            // W przeciwnym wypadku zwiększamy index
            for (value in hrRecords) {

                // Zwiększamy index do uzyskania tych samych dat
                while (index < outcome.size && value.first != outcome[index].first) {
                    index++
                    Log.e("value", value.first.toString())
                    Log.e("outcome", outcome[index].first.toString())
                }

                // Jeżeli jest to ostatni index przypisujemy ostatnią wartość sleepRecords
                // Pętla wykona się jedynie jeśli na ostatniej dacie rekordów jest wartość
                if (index >= outcome.size) {
                    outcome[index-1] = value
                    break
                }

                // Jeśli znaleźliśmy dopasowanie, przypisz wartość oraz zwiększ index
                outcome[index] = value
                index++
            }
            return outcome
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
                heartRatioRecords.add(Pair(date,averageHeartRatio))
            }
            return heartRatioRecords
        } catch (e: Exception) {
            return emptyList()
        }
    }
}