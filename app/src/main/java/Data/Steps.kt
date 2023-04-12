package Data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

class Steps {

    suspend fun readSteps(healthConnectClient : HealthConnectClient, startTime: Instant, endTime: Instant): Int? {
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
            return response[StepsRecord.COUNT_TOTAL]?.toInt()
        }
    }
}