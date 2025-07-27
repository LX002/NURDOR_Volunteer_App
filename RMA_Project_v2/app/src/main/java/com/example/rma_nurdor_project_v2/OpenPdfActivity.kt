package com.example.rma_nurdor_project_v2

import android.app.Notification.Action
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class OpenPdfActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION, Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()

        val pdfUri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("pdfUri", Uri::class.java)
        } else {
            intent.getParcelableExtra("pdfUri")
        }

        Log.i("openPdfActivity", "$pdfUri")

        pdfUri?.let {
            try {
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(it, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                try {
                    startActivity(viewIntent)
                } catch (e: ActivityNotFoundException) {
                    val anotherViewIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(it,"*/*")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    try {
                        startActivity(anotherViewIntent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(this, "No PDF viewer found...", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error in opening pdf...", Toast.LENGTH_SHORT).show()
            }
        } ?: Log.i("openPdfActivity", "pdfUri is null")
    }
}