package Data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

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
        return minimumHeartRate
    }
    fun getMaximumHeartRate(): Long? {
        return maximumHeartRate
    }
    fun getMeanHeartRate(): Long? {
        return meanHeartRate
    }
}