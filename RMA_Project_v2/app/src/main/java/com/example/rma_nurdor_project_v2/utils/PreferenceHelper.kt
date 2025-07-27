package com.example.rma_nurdor_project_v2.utils

import android.content.Context
import android.util.Log

object PreferenceHelper {
    private const val PREFERENCES_NAME = "user_preferences_1"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_ID_VOLUNTEER = "volunteer_id"
    private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
    private const val KEY_IS_ADMIN = "is_administrator"
    private const val KEY_EVENT_STARTED = "event_started"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_NEAREST_CITY = "volunteer_nearest_city"

    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setIdVolunteer(context: Context, idVolunteer: Int) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit().putInt(KEY_ID_VOLUNTEER, idVolunteer).apply()
    }

    fun getIdVolunteer(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getInt(KEY_ID_VOLUNTEER, 0)
    }

    fun setFirstLaunch(context: Context, isFirstLaunch: Boolean) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(KEY_IS_FIRST_LAUNCH, isFirstLaunch).apply()
    }

    fun isFirstLaunch(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    fun setIsAdmin(context: Context, isAdmin: Boolean) {
        Log.i("onCreateOptionsMenuMeth", "setIs admin: $isAdmin")
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(KEY_IS_ADMIN, isAdmin).apply()
    }

    fun isAdmin(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(KEY_IS_ADMIN, false)
    }

    fun setStartedEvent(context: Context, idEvent: Int) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit().putInt(KEY_EVENT_STARTED, idEvent).apply()
    }

    fun getStartedEvent(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getInt(KEY_EVENT_STARTED, -1)
    }

    fun isDarkMode(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, isDarkMode: Boolean) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply()
    }

    fun setVolunteerNearestCity(context: Context, nearestCity: String) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(KEY_NEAREST_CITY, nearestCity).apply()
    }

    fun getVolunteerNearestCity(context: Context): String? {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_NEAREST_CITY, "")
    }
}