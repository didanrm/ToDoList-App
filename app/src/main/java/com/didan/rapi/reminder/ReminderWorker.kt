package com.didan.rapi.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.didan.rapi.MainActivity
import com.didan.rapi.R
import java.util.concurrent.TimeUnit

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, 0)
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val manager = NotificationManagerCompat.from(applicationContext)

        if (android.os.Build.VERSION.SDK_INT >= 33 && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pengingat tugas",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply { description = "Notifikasi pengingat tugas Rapi" }
        applicationContext.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra(EXTRA_TASK_ID, taskId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Waktunya: $title")
            .setContentText("Buka Rapi untuk melihat detail tugas.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(taskId.hashCode(), notification)
        return Result.success()
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
        private const val CHANNEL_ID = "task_reminders"
        private const val KEY_TASK_ID = "taskId"
        private const val KEY_TITLE = "title"

        fun schedule(context: Context, taskId: Long, title: String, reminderAt: Long?) {
            val workManager = WorkManager.getInstance(context)
            val name = "reminder-$taskId"
            if (reminderAt == null || reminderAt <= System.currentTimeMillis()) {
                workManager.cancelUniqueWork(name)
                return
            }
            val data = Data.Builder()
                .putLong(KEY_TASK_ID, taskId)
                .putString(KEY_TITLE, title)
                .build()
            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(data)
                .setInitialDelay(reminderAt - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build()
            workManager.enqueueUniqueWork(
                name,
                androidx.work.ExistingWorkPolicy.REPLACE,
                request,
            )
        }

        fun cancel(context: Context, taskId: Long) =
            WorkManager.getInstance(context).cancelUniqueWork("reminder-$taskId")
    }
}
