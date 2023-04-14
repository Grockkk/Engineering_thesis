package Data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Length
import java.time.Instant
import kotlin.math.round

class Distance {

    suspend fun readDistance(healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant): Double? {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )

        var totalDistance = response[DistanceRecord.DISTANCE_TOTAL]?.inKilometers?.toDouble()
        if (totalDistance != null) {
            totalDistance = round(totalDistance * 100) / 100
        }
        if(totalDistance != null){
            return totalDistance
        }
        else{
            return 0.0;
        }

    }
}