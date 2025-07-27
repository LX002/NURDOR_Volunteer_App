package com.example.rma_nurdor_project_v2.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.rma_nurdor_project_v2.OpenPdfActivity
import com.example.rma_nurdor_project_v2.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PdfDownloadWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {

    private fun createDownloadFinishedNotification(fileName: String, context: Context, intent: PendingIntent): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.stat_sys_download_anim0)
            .setContentTitle("Downloading PDF")
            .setContentText("$fileName saved in downloads!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intent)
            .setAutoCancel(true)
    }

    private fun createDownloadErrorNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.stat_sys_download_anim0)
            .setContentTitle("Downloading PDF")
            .setContentText("Download failed!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
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

    private fun clearNotification(notificationId: Int, context: Context) {
        with(NotificationManagerCompat.from(context)) { cancel(notificationId) }
    }

    override suspend fun doWork(): Result {
        val idEvent = inputData.getInt("eventId", 0)
        val eventName = inputData.getString("eventName") ?: return Result.failure()

        return try {
            val pdfUri = PdfUtils.downloadPdf(applicationContext, idEvent, "$eventName-info.pdf")
            if(pdfUri != null) {
                val openIntent = Intent(applicationContext, OpenPdfActivity::class.java).apply {
                    putExtra("pdfUri", pdfUri)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                }
                val pendingIntent = PendingIntent.getActivity(applicationContext, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                CoroutineScope(Dispatchers.Main).launch {
                    clearNotification(NotificationConstants.DOWNLOADING_ID, applicationContext)
                    showNotification(NotificationConstants.FINISHED_ID, createDownloadFinishedNotification("$eventName-info.pdf", applicationContext, pendingIntent), applicationContext)
                }
                Result.success()
            }
            else {
                CoroutineScope(Dispatchers.Main).launch {
                    clearNotification(NotificationConstants.DOWNLOADING_ID, applicationContext)
                    showNotification(NotificationConstants.ERROR_ID, createDownloadErrorNotification(applicationContext), applicationContext)
                }
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}