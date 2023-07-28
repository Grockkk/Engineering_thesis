package Data

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SleepStageRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Duration
import java.time.Instant

class Sleep {
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun readSleepDuration(healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant): String? {
        val response =
            healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime.minusSeconds(18000), endTime.minusSeconds(18000))
                )
            )
// The result may be null if no data is available in the time range.

            if(response[SleepSessionRecord.SLEEP_DURATION_TOTAL] != null){
                val hours = response[SleepSessionRecord.SLEEP_DURATION_TOTAL]?.toHours()
                val minutes = response[SleepSessionRecord.SLEEP_DURATION_TOTAL]?.toMinutesPart()

                return "$hours" + "h" + "$minutes" + "m"
            }
            else{
                return "brak pomiaru"
            }
    }
}
