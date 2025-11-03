package com.brunof3l.locus.ui.alerts

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object CalibrationAlertScheduler {
  private const val CHANNEL_TITLE_PRE = "Calibração próxima do vencimento"
  private const val CHANNEL_TITLE_OVER = "Calibração vencida"

  fun scheduleCalibrationAlerts(context: Context, cod: String, descricao: String, dueMillis: Long) {
    val wm = WorkManager.getInstance(context)
    val now = System.currentTimeMillis()
    val dayMs = TimeUnit.DAYS.toMillis(1)
    val preDays = listOf(30L, 15L, 10L, 5L, 3L, 2L, 1L)

    // Pré-vencimento
    preDays.forEach { d ->
      val triggerAt = dueMillis - d * dayMs
      val delay = triggerAt - now
      if (delay > 0) {
        val input = Data.Builder()
          .putString("cod", cod)
          .putString("title", CHANNEL_TITLE_PRE)
          .putString("message", "O item '" + descricao + "' vence em " + d + " dia(s).")
          .build()
        val req = OneTimeWorkRequestBuilder<CalibrationAlertWorker>()
          .setInitialDelay(delay, TimeUnit.MILLISECONDS)
          .addTag("calibration_pre_${cod}_$d")
          .setInputData(input)
          .build()
        wm.enqueueUniqueWork("calibration_pre_${cod}_$d", ExistingWorkPolicy.REPLACE, req)
      }
    }

    // Pós-vencimento: alerta diário
    val overdueDelay = dueMillis - now
    val inputOver = Data.Builder()
      .putString("cod", cod)
      .putString("title", CHANNEL_TITLE_OVER)
      .putString("message", "O item '" + descricao + "' está com a calibração vencida.")
      .build()
    val periodic = PeriodicWorkRequestBuilder<CalibrationAlertWorker>(1, TimeUnit.DAYS)
      .setInitialDelay(if (overdueDelay > 0) overdueDelay else 0, TimeUnit.MILLISECONDS)
      .addTag("calibration_overdue_${cod}")
      .setInputData(inputOver)
      .build()
    wm.enqueueUniquePeriodicWork("calibration_overdue_${cod}", ExistingPeriodicWorkPolicy.UPDATE, periodic)
  }
}