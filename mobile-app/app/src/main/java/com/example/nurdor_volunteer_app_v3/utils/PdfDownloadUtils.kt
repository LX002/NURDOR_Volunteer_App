package com.example.nurdor_volunteer_app_v3.utils

import android.Manifest
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
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
import com.example.nurdor_volunteer_app_v3.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

object PdfDownloadUtils {

    private val api = RetrofitInstance.instance

    fun createDownloadingNotification(fileName: String, context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Downloading PDF")
            .setContentText("Downloading ${fileName}...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    fun createDownloadFinishedNotification(fileName: String, context: Context, intent: PendingIntent): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(com.example.nurdor_volunteer_app_v3.R.drawable.stat_sys_download_anim0)
            .setContentTitle("Downloading PDF")
            .setContentText("$fileName saved in downloads!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intent)
            .setAutoCancel(true)
    }

    fun createDownloadErrorNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(com.example.nurdor_volunteer_app_v3.R.drawable.stat_sys_download_anim0)
            .setContentTitle("Downloading PDF")
            .setContentText("Download failed!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
    }

    fun showNotification(notificationId: Int, notificationBuilder: NotificationCompat.Builder, context: Context) {
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

    fun clearNotification(notificationId: Int, context: Context) {
        with(NotificationManagerCompat.from(context)) { cancel(notificationId) }
    }
    suspend fun storeEventPdfIntoDownloadsDirectory(
        context: Context,
        idEvent: Int,
        fileName: String
    ): Uri? {
        val pdfBody = fetchEventPdfBody(idEvent, fileName, context) ?: return null
        return withContext(Dispatchers.IO) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val downloads = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val uri = resolver.insert(downloads, contentValues)

            uri?.let { targetUri ->
                resolver.openOutputStream(targetUri)?.use { outputStream ->
                    outputStream.write(pdfBody)
                    outputStream.flush()
                    Log.e("pdfDownload", "Downloads uri: $targetUri")
                } ?: Log.e("pdfDownload", "Failed to open output stream!")

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(targetUri, contentValues, null, null)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "PDF saved successfully!", Toast.LENGTH_SHORT).show()
                }
                targetUri
            }
        }
    }

    private suspend fun fetchEventPdfBody(idEvent: Int, fileName: String, context: Context): ByteArray? {
        try {
            showNotification(
                NotificationConstants.DOWNLOADING_ID,
                createDownloadingNotification(fileName, context),
                context
            )
            val response = api.downloadPdfWithEventById(idEvent).awaitResponse()
            if(response.isSuccessful) {
                return response.body()?.bytes()
            } else {
                Log.e("pdfDownload", "Fetching brochure body was unsuccessful!")
            }
        } catch (e: Exception) {
            Log.e("pdfDownload", "Exception during fetching brochure body: ${e.message}")
        }
        return null
    }
}