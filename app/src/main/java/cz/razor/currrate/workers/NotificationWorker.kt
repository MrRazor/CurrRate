package cz.razor.currrate.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import cz.razor.currrate.helpers.NotificationHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams), KoinComponent{

    private val notificationHelper: NotificationHelper by inject()

    override fun doWork(): Result {
        notificationHelper.showNotification("CryptoApp", "Nezapomeň zkontrolovat ceny kryptoměn!")

        return Result.success()
    }
}