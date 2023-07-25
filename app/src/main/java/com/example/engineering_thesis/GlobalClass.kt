package com.example.engineering_thesis

import android.app.Application
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

class GlobalClass: Application() {
    var birthday: String = "0 0 0"

    companion object {
        @JvmStatic
        lateinit var instance: GlobalClass
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    //private fun calculateAge(birthDate: LocalDate): Int {
    //    val currentDate = LocalDate.now()
    //    return Period.between(birthDate, currentDate).years
    //}

}


