package Data

import android.widget.Toast
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.kilograms
import androidx.health.connect.client.units.meters
import com.example.engineering_thesis.GlobalClass
import java.time.*
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
                timeRangeFilter = TimeRangeFilter.between(startTime.minusSeconds(31556926*5), endTime)
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
                timeRangeFilter = TimeRangeFilter.between(startTime.minusSeconds(31556926*5), endTime)
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

    suspend fun writeWeightInput(healthConnectClient: HealthConnectClient,weightInput: Double) {
        val time = ZonedDateTime.now().withNano(0)
        val weightRecord = WeightRecord(
            weight = Mass.kilograms(weightInput),
            time = time.toInstant(),
            zoneOffset = time.offset
        )
        val records = listOf(weightRecord)
        try {
            healthConnectClient.insertRecords(records)
        } catch (e: Exception) {
        }
    }
    suspend fun writeHeightInput(healthConnectClient: HealthConnectClient,heightInput: Double) {
        val time = ZonedDateTime.now().withNano(0)
        val heightRecord = HeightRecord(
            height = Length.meters(heightInput),
            time = time.toInstant(),
            zoneOffset = time.offset
        )
        val records = listOf(heightRecord)
        try {
            healthConnectClient.insertRecords(records)
        } catch (e: Exception) {
        }
    }

}