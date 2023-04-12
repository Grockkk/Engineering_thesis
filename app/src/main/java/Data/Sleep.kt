package Data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SleepStageRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Duration
import java.time.Instant

class Sleep {
    /*
    suspend fun readSleepDuration(healthConnectClient : HealthConnectClient, startTime: Instant, endTime: Instant): String? {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        for (sleepRecord in response.records) {
            // Process each exercise record
            // Optionally pull in sleep stages of the same time range
            val sleepStageRecords =
                healthConnectClient
                    .readRecords(
                        ReadRecordsRequest(
                            SleepStageRecord::class,
                            timeRangeFilter =
                            TimeRangeFilter.between(sleepRecord.startTime, sleepRecord.endTime)
                        )
                    )
                    .records
        }

    }
     */
    suspend fun readSleepDuration(healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant): String? {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )

        var totalDuration = Duration.ZERO
        for (sleepRecord in response.records) {
            val duration = Duration.between(sleepRecord.startTime, sleepRecord.endTime)
            totalDuration = totalDuration.plus(duration)
        }

        val hours = totalDuration.toHours()
        val minutes = totalDuration.toMinutes() % 60
        val seconds = totalDuration.seconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
