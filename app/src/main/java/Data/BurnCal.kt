package Data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import java.time.Instant



class BurnCal {

    suspend fun readBurnedCalories (
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): Comparable<*>? {
            val response =
                healthConnectClient.aggregate(
                    AggregateRequest(
                        metrics = setOf(),
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
        if (response[TotalCaloriesBurnedRecord.ENERGY_TOTAL] == null){
            return 0
        }
            return response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]
    }
}