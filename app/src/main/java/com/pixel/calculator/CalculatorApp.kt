package com.pixel.calculator

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.pixel.calculator.Constants.MODE_VIEW_KEY

class CalculatorApp : Application() {
    companion object {
        const val SHARED_KEY = "Mode"
    }

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(SHARED_KEY, MODE_PRIVATE)
        initAppNightMode()
    }

    private fun initAppNightMode() {
        val mode = sharedPref.getInt(MODE_VIEW_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        when (mode) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            AppCompatDelegate.MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}
