package com.example.nurdor_volunteer_app_v3.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.net.toUri

class ContactUtils(context: Context) {

    companion object {
        fun composeEmail(addresses: Array<String>, context: Context) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri() // Only email apps handle this.
                putExtra(Intent.EXTRA_EMAIL, addresses)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }

        fun dialPhoneNumber(phoneNumber: String, context: Context) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:$phoneNumber".toUri()
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }
    }
}