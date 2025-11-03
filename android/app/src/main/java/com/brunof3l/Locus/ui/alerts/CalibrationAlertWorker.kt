package com.brunof3l.locus.ui.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class CalibrationAlertWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
  override fun doWork(): Result {
    val cod = inputData.getString("cod") ?: ""
    val title = inputData.getString("title") ?: "Alerta de Calibração"
    val message = inputData.getString("message") ?: ""
    val channelId = "calibration_alerts"

    // Create notification channel for Android 8+
    val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(channelId, "Alertas de Calibração", NotificationManager.IMPORTANCE_DEFAULT)
      nm.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(applicationContext, channelId)
      .setSmallIcon(android.R.drawable.ic_dialog_alert)
      .setContentTitle(title)
      .setContentText(message)
      .setAutoCancel(true)
      .build()

    NotificationManagerCompat.from(applicationContext).notify(cod.hashCode(), notification)
    return Result.success()
  }
}