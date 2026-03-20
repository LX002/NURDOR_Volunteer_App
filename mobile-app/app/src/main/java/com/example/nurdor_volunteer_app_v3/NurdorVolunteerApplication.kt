package com.example.nurdor_volunteer_app_v3

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.application
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class NurdorVolunteerApplication: Application() {

    companion object {
        lateinit var encryptedPrefs: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        // [NOTE TO MYSELF] change this to DataStore in later version of project
        encryptedPrefs = EncryptedSharedPreferences.create(
            this,
            "encrypted_prefs",
            MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}