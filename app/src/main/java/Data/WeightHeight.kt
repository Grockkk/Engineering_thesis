package Data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Mass
import com.example.engineering_thesis.GlobalClass
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import kotlin.math.round

class WeightHeight {
    var Height: Double = 0.0
    var Weight: Double = 0.0
    var globaAge = GlobalClass()

    var birthDate = globaAge.birthday

    suspend fun readWeightAndHeight(healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant)  {
        val responseWeight = healthConnectClient.readRecords(
            ReadRecordsRequest(
                WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        for (weightRecord in responseWeight.records) {
            if(weightRecord.weight.inKilograms == null){
                Weight = 0.0
            }else{
                Weight = weightRecord.weight.inKilograms
            }

        }

        val responseHeight = healthConnectClient.readRecords(
            ReadRecordsRequest(
                HeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        for (heightRecord in responseHeight.records) {
            if(heightRecord.height.inMeters == 0.0){
                Height = 0.0
            }else{
                Height = heightRecord.height.inMeters
            }

        }
    }

}