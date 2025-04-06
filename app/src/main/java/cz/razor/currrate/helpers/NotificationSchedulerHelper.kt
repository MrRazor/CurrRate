package cz.razor.currrate.helpers

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import cz.razor.currrate.workers.NotificationWorker
import java.util.concurrent.TimeUnit

class NotificationSchedulerHelper(private val context: Context) {

    fun scheduleNotificationWorker() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).cancelAllWork()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "notificationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorkRequest
        )

        Log.i("NotificationSchedulerHelper", "Notification worker scheduled")

    }
}
