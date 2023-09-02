package Data

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregationResultGroupedByPeriod
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import kotlin.time.Duration.Companion.days

class Steps {

    suspend fun readSteps(healthConnectClient : HealthConnectClient, startTime: Instant, endTime: Instant): Int {
        val response =
            healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
// The result may be null if no data is available in the time range.
        if(response[StepsRecord.COUNT_TOTAL]?.toInt() == null) {
            return 0
        }
        else{
            return response[StepsRecord.COUNT_TOTAL]!!.toInt()
        }
    }

    suspend fun aggregateStepsWithDates(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Pair<LocalDateTime, Long?>> {
        try {
            val aggregatedResults = healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            val stepRecords = mutableListOf<Pair<LocalDateTime, Long?>>()
            for (result in aggregatedResults) {
                val date = result.startTime// Początek okresu, czyli data pomiaru
                val totalSteps = result.result[StepsRecord.COUNT_TOTAL] ?: null
                stepRecords.add(Pair(date, totalSteps))
            }

            while (stepRecords.size < 7){
                val last = stepRecords.last()
                val date = last.first
                stepRecords.add(Pair(date.plusDays(1),0))
            }

            return stepRecords
        } catch (e: Exception) {
            // Obsługa błędów
            Log.e("AggregationError", "An error occurred during aggregation: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun readStepsWeek(healthConnectClient : HealthConnectClient, startTime: Instant, endTime: Instant): Int {
        val response =
            healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
// The result may be null if no data is available in the time range.
        if(response[StepsRecord.COUNT_TOTAL]?.toInt() == null) {
            return 0
        }
        else{
            return response[StepsRecord.COUNT_TOTAL]!!.toInt()
        }
    }

    suspend fun getWeeklyData(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Int> {
        try {
            val aggregatedResults = healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            var MaxRecord = 0
            var MinRecord = 100000
            var TotalRecord = 0

            val stepRecords = mutableListOf<Int>()
            for (result in aggregatedResults) {
                val totalSteps = result.result[StepsRecord.COUNT_TOTAL]

                if (totalSteps != null) {
                    TotalRecord += totalSteps.toInt()
                    if (totalSteps > MaxRecord){
                        MaxRecord = totalSteps.toInt()
                    }
                    if (totalSteps < MinRecord){
                        MinRecord = totalSteps.toInt()
                    }
                }

            }
            stepRecords.add(TotalRecord)
            stepRecords.add(MaxRecord)
            stepRecords.add(MinRecord)

            return stepRecords
        } catch (e: Exception) {
            // Obsługa błędów
            Log.e("AggregationError", "An error occurred during aggregation: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }

}