package Data

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period


class BurnCal {

    suspend fun readBurnedCalories (
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): String {
            val response =
                healthConnectClient.aggregate(
                    AggregateRequest(
                        metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )

        val kcal = response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]
        if (kcal == null){
            return "0"
        }
        return "%.0f".format(kcal.inKilocalories)

    }

    suspend fun getWeeklyData(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Double> {
        try {
            val aggregatedResults = healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            var MaxRecord = 0.0
            var MinRecord = 1000000.0

            val CaloriesRecord = mutableListOf<Double>()
            for (result in aggregatedResults) {
                val totalCalories = result.result[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories

                if (totalCalories != null) {
                    if (totalCalories > MaxRecord){
                        MaxRecord = totalCalories
                    }
                    if (totalCalories < MinRecord){
                        MinRecord = totalCalories
                    }
                }

            }


            CaloriesRecord.add(MaxRecord)
            CaloriesRecord.add(MinRecord)

            return CaloriesRecord
        } catch (e: Exception) {
            // Obsługa błędów
            Log.e("AggregationError", "An error occurred during aggregation: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }
}