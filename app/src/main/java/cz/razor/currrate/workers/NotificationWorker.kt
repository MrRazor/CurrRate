package cz.razor.currrate.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import cz.razor.currrate.R
import cz.razor.currrate.helpers.NotificationHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams), KoinComponent{

    private val context = appContext
    private val notificationHelper: NotificationHelper by inject()

    override fun doWork(): Result {
        notificationHelper.showNotification(context.getString(R.string.currency_rates_app),
            context.getString(R.string.currency_rates_update_notification))

        return Result.success()
    }
}