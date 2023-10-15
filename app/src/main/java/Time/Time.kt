package Time

import java.time.*

class Time() {
    var Day: Long = 0
    var day = LocalDate.now().minusDays(Day)

    fun getStartTime(): Instant {

        val startOfDay = LocalDateTime.of(day, LocalTime.MIN)
        return  Instant.from(startOfDay.toInstant(ZoneOffset.UTC))

    }
    fun getEndTime(): Instant{
        return Instant.from((LocalDateTime.of(day, LocalTime.MAX)).toInstant(ZoneOffset.UTC))
    }

    fun getToday(): Instant{
        return Instant.from((LocalDateTime.of(LocalDate.now(), LocalTime.MAX)).toInstant(ZoneOffset.UTC))
    }

    fun incrDay() {
        Day += 1;
        day = LocalDate.now().minusDays(Day)
    }
    fun decrDay(){
        if (day != LocalDate.now()){
            Day -= 1;
            day = LocalDate.now().minusDays(Day)
        }
    }

    fun getStartTimeWeek(): Instant {
        val startLocalDate = LocalDate.now()
        val startOfDay = LocalDateTime.of(startLocalDate, LocalTime.MIN).minusDays(7)
        return  Instant.from(startOfDay.toInstant(ZoneOffset.UTC))
    }

    fun getEndTimeWeek(): Instant{
        val endLocalDate = LocalDate.now()
        return Instant.from((LocalDateTime.of(endLocalDate, LocalTime.MAX)).toInstant(ZoneOffset.UTC))
    }

    fun setDateToday(){
        day = LocalDate.now()
    }

}