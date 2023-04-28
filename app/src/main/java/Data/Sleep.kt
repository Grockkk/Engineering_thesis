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
    suspend fun readSleepDuration(healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant): String? {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime.minusSeconds(86400), endTime.minusSeconds(86400))
            )
        )
        var duration = Duration.ZERO;
        for (sleepRecord in response.records) {
            duration = Duration.between(sleepRecord.startTime, sleepRecord.endTime)
        }

        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        return if(hours > 9){
            String.format("%02d h %02d min", hours, minutes)
        }else{
            String.format("%2d h %02d min", hours, minutes)
        }

    }
}
