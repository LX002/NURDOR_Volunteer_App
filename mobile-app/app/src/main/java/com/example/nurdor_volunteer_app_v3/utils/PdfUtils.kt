package com.example.nurdor_volunteer_app_v3.utils

import android.Manifest
import android.R
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
import retrofit2.awaitResponse

object PdfUtils {

    private val api = RetrofitInstance.instance

    private fun createDownloadingNotification(fileName: String, context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.stat_sys_download)
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

    private suspend fun fetchEventPdfBody(idEvent: Int, fileName: String, context: Context): ByteArray? {
        var eventPdf: ByteArray? = null
        try {
            showNotification(
                NotificationConstants.DOWNLOADING_ID,
                createDownloadingNotification(fileName, context),
                context
            )
            val response = api.downloadPdfWithEventById(idEvent).awaitResponse()
            if(response.isSuccessful) {
                response.body()?.let { body ->
                    eventPdf = body.bytes()
                    Log.i("retrofitApi1", "Brochure body is received!")
                } ?: {
                    Log.i("retrofitApi1", "Brochure body is received but it's null!")
                }
            } else {
                Log.e("retrofitApi1", "Fetching brochure body was unsuccessful!")
            }
        } catch (e: Exception) {
            Log.e("retrofitApi1", "Exception during fetching brochure body: ${e.message}")
        }
        return eventPdf
    }
    suspend fun storeEventPdfIntoDownloadsDirectory(
        context: Context,
        idEvent: Int,
        fileName: String
    ): Uri? {
        val pdfBody = fetchEventPdfBody(idEvent, fileName, context)
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
                resolver.openOutputStream(uri as Uri)?.use { outputStream ->
                    outputStream.write(pdfBody)
                    Log.e("pdfDownload", "Downloads uri: $it")
                    Toast.makeText(context, "PDF saved successfully!", Toast.LENGTH_SHORT).show()
                } ?: Log.e("pdfDownload", "Failed to open output stream!")
            } ?: Log.e("pdfDownload", "Downloads uri is null!")
        } ?: Log.e("pdfDownload", "Brochure body is null!")

        return uri
    }
}