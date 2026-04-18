package com.example.nurdor_volunteer_app_v3.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit

object PreferenceHelper {
    private const val PREFERENCES_NAME = "user_preferences_1"
    private const val KEY_ID_VOLUNTEER = "volunteer_id"
    private const val KEY_IS_ADMIN = "is_admin"
    private const val KEY_NEAREST_CITY = "volunteer_nearest_city"

    fun setIdVolunteer(context: Context, idVolunteer: Int) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit { putInt(KEY_ID_VOLUNTEER, idVolunteer) }
    }

    fun getIdVolunteer(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getInt(KEY_ID_VOLUNTEER, 0)
    }

    fun setIsAdmin(context: Context, isAdmin: Boolean) {
        Log.i("onCreateOptionsMenuMeth", "setIs admin: $isAdmin")
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit { putBoolean(KEY_IS_ADMIN, isAdmin) }
    }

    fun isAdmin(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(KEY_IS_ADMIN, false)
    }

    fun setNearestCity(context: Context, nearestCity: String) {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit { putString(KEY_NEAREST_CITY, nearestCity) }
    }

    fun getNearestCity(context: Context): String? {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_NEAREST_CITY, "")
    }
}