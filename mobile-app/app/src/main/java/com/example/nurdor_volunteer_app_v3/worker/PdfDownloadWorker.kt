package com.example.nurdor_volunteer_app_v3.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nurdor_volunteer_app_v3.activity.OpenPdfActivity
import com.example.nurdor_volunteer_app_v3.utils.NotificationConstants
import com.example.nurdor_volunteer_app_v3.utils.PdfDownloadUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PdfDownloadWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val idEvent = inputData.getInt("eventId", 0)
        val eventName = inputData.getString("eventName") ?: return Result.failure()

        return try {
            val pdfUri = PdfDownloadUtils.storeEventPdfIntoDownloadsDirectory(applicationContext, idEvent, "$eventName-info.pdf", )
            if(pdfUri != null) {
                val openIntent = Intent(applicationContext, OpenPdfActivity::class.java).apply {
                    putExtra("pdfUri", pdfUri)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                }
                val pendingIntent = PendingIntent.getActivity(applicationContext, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                CoroutineScope(Dispatchers.Main).launch {
                    PdfDownloadUtils.clearNotification(NotificationConstants.DOWNLOADING_ID, applicationContext)
                    PdfDownloadUtils.showNotification(NotificationConstants.DOWNLOADING_ID, PdfDownloadUtils.createDownloadFinishedNotification("$eventName-info.pdf", applicationContext, pendingIntent), applicationContext)
                }
                Result.success()
            }
            else {
                CoroutineScope(Dispatchers.Main).launch {
                    PdfDownloadUtils.clearNotification(NotificationConstants.DOWNLOADING_ID, applicationContext)
                    PdfDownloadUtils.showNotification(NotificationConstants.DOWNLOADING_ID, PdfDownloadUtils.createDownloadErrorNotification(applicationContext), applicationContext)
                }
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}