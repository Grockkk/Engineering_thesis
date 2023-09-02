package Data

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.kilometers
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import kotlin.math.round

class Distance {

    suspend fun readDistance(healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant): Length {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )

        var totalDistance = response[DistanceRecord.DISTANCE_TOTAL]?.inKilometers
        if (totalDistance != null) {
            totalDistance = round(totalDistance * 100) / 100
        }
        if(totalDistance != null){
            return totalDistance.kilometers
        }
        else{
            return 0.0.kilometers;
        }

    }
    suspend fun getWeeklyData(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Double> {
        try {
            val aggregatedResults = healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            var MaxRecord = 0.0
            var MinRecord = 100000.0

            val distRecords = mutableListOf<Double>()
            for (result in aggregatedResults) {
                val totalDist = result.result[DistanceRecord.DISTANCE_TOTAL]?.inKilometers
                if (totalDist != null) {
                    if (totalDist > MaxRecord){
                        MaxRecord = totalDist
                    }
                    if (totalDist < MinRecord){
                        MinRecord = totalDist
                    }
                }
            }

            MaxRecord = round(MaxRecord * 100) / 100
            MinRecord = round(MinRecord * 100) / 100

            distRecords.add(MaxRecord)
            distRecords.add(MinRecord)

            return distRecords
        } catch (e: Exception) {
            // Obsługa błędów
            Log.e("AggregationError", "An error occurred during aggregation: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }
}