package com.example.rma_nurdor_project_v2.utils

import android.Manifest
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.rma_nurdor_project_v2.OpenPdfActivity
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.repository.RetrofitInstance
import retrofit2.awaitResponse

object PdfUtils {

    private val retrofitApi = RetrofitInstance.instance

    private fun createDownloadingNotification(fileName: String, context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Downloading PDF")
            .setContentText("Downloading ${fileName}...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    private fun showNotification(notificationId: Int, notificationBuilder: NotificationCompat.Builder, context: Context) {
        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(context as AppCompatActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
                return@with
            }
            notify(notificationId, notificationBuilder.build())
        }
    }

    private suspend fun getEventPdfBody(idEvent: Int, fileName: String, context: Context): ByteArray? {
        var eventPdf: ByteArray? = null
        try {
            showNotification(NotificationConstants.DOWNLOADING_ID, createDownloadingNotification(fileName, context), context)
            val response = retrofitApi.getEventPdf(idEvent).awaitResponse()
            if(response.isSuccessful) {
                response.body()?.let { body ->
                    eventPdf = body.bytes()
                    Log.i("pdfCreating", "Body is received!")
                } ?: Log.i("pdfCreating", "Body is null!")

            } else {
                Log.e("pdfCreating", "Creating pdf was unsuccessful: pdf is empty")
            }
        } catch (e: Exception) {
            Log.e("pdfCreating", "Exception during creating pdf: ${e.message}")
        }

        return eventPdf
    }

    //@RequiresApi(Build.VERSION_CODES.Q)
    suspend fun downloadPdf(
        context: Context,
        idEvent: Int,
        fileName: String
    ): Uri? {
        val pdfBody = getEventPdfBody(idEvent, fileName, context)
        var uri: Uri? = null
        pdfBody?.let {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val downloads = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            uri = resolver.insert(downloads, contentValues)

            uri?.let {
                resolver.openOutputStream(uri!!)?.use { outputStream ->
                    outputStream.write(pdfBody)
                    Log.e("pdfCreating", "uri: $it")
                    //Toast.makeText(context, "PDF saved successfully!", Toast.LENGTH_SHORT).show()
                } ?: Log.e("pdfCreating", "Failed to open output stream")
            } ?: Log.e("pdfCreating", "Uri is null!")
        } ?: Log.e("pdfCreating", "Pdf body is null!")

        return uri
    }

    fun openPdf(pdfUri: Uri?, context: Context) {
        if(pdfUri != null) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "No PDF viewer installed!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "PDF not found!", Toast.LENGTH_SHORT).show()
        }
    }
}