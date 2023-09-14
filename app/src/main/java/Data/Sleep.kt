package Data

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SleepStageRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toDuration

class Sleep {
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun readSleepDuration(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<Pair<Long?, Int?>>{
        val response =
            healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime.minusSeconds(18000),
                        endTime.minusSeconds(18000)
                    )
                )
            )
// The result may be null if no data is available in the time range.
        val sleepRecords = mutableListOf<Pair<Long?, Int?>>()

            if(response[SleepSessionRecord.SLEEP_DURATION_TOTAL] != null){
                val hours = response[SleepSessionRecord.SLEEP_DURATION_TOTAL]?.toHours()
                val minutes = response[SleepSessionRecord.SLEEP_DURATION_TOTAL]?.toMinutesPart()

                sleepRecords.add(Pair(hours,minutes))
                return sleepRecords
            }
            else{
                sleepRecords.add(Pair(0L,0))
                return sleepRecords
            }
    }
    suspend fun readSleepSessionsStages(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Pair<Long, String>>
    {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    SleepStageRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        val sleepRecords = mutableListOf<Pair<Long, String>>()

        var AWAKE = 0.0.toLong()
        var DEEP = 0.0.toLong()
        var LIGHT = 0.0.toLong()
        var REM = 0.0.toLong()
        var UNKNOWN = 0.0.toLong()

        for (sleepRecord in response.records) {
            // Retrieve relevant sleep stages from each sleep record
            val stage = sleepRecord.stage
            val dur = Duration.between(sleepRecord.startTime,sleepRecord.endTime).seconds/60

            when (stage) {
                1 -> AWAKE += dur
                5 -> DEEP += dur
                4 -> LIGHT += dur
                6 -> REM += dur
                else -> UNKNOWN += dur
            }
        }

        sleepRecords.add(Pair(LIGHT,"Light"))
        sleepRecords.add(Pair(DEEP,"Deep"))
        sleepRecords.add(Pair(REM,"REM"))
        if(AWAKE != 0.0.toLong()){
            sleepRecords.add(Pair(AWAKE,"Awake"))
        }
        if(UNKNOWN != 0.0.toLong()){
            sleepRecords.add(Pair(UNKNOWN,"Unknown"))
        }
        return sleepRecords
    }

    suspend fun aggregateSleepForChart(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Pair<LocalDateTime, Long?>> {
        try {
            val aggregatedResults = healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )
            var index = 0
            val sleepRecords = mutableListOf<Pair<LocalDateTime, Long?>>()
            for (result in aggregatedResults) {
                val date = result.endTime// koniec okresu, czyli data pomiaru
                val totalSleep = result.result[SleepSessionRecord.SLEEP_DURATION_TOTAL]?.seconds?.div(
                    60
                )

                while (endTime.minusDays(7).dayOfMonth.plus(index) != date.dayOfMonth){
                    sleepRecords.add(Pair(LocalDateTime.now().minusDays(7).plusDays(index.toLong()),0))
                    index += 1
                }
                sleepRecords.add(Pair(date, totalSleep))
                index += 1
            }
            return sleepRecords
        } catch (e: Exception) {
            // Obsługa błędów
            Log.e("AggregationError", "An error occurred during aggregation: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }
}