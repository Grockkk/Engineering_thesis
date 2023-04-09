package com.example.engineering_thesis

import java.time.*

class Time() {
    var Day: Long = 0
    val day = LocalDate.now().minusDays(Day)

    fun getStartTime(): Instant {

        val startOfDay = LocalDateTime.of(day, LocalTime.MIN)
        return  Instant.from(startOfDay.toInstant(ZoneOffset.UTC))

    }
    fun getEndTime(): Instant{
        return Instant.from((LocalDateTime.of(day, LocalTime.MAX)).toInstant(ZoneOffset.UTC))
    }
    fun incrDay() {
        Day += 1;
    }
    fun decrDay(){
        if (day != LocalDate.now()){
            Day -= 1;
        }
    }

}