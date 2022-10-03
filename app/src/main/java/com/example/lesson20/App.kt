package com.example.lesson20

import android.app.Application
import java.text.SimpleDateFormat
import java.util.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        self = this
    }

    companion object {
        private lateinit var self: App

        private val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

        fun getInstanceApp(): App {
            return self
        }

        fun getDateFormat(): SimpleDateFormat {
            return dateFormat
        }
    }
}