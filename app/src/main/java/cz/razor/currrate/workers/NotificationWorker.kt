package cz.razor.currrate.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import cz.razor.currrate.helpers.NotificationHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : //dědíme z abstraktní třídy Worker, který příjímá parametry typu Context a WorkerParameters
    Worker(appContext, workerParams), KoinComponent{ //jelikož chceme získat instanci NotificationHelper pomocí Koin, musíme implementovat rozhraní KoinComponent

    private val notificationHelper: NotificationHelper by inject()

    override fun doWork(): Result { //metoda abstraktní třídy Worker, kterou je nutné implementovat
        //zde typicky zkontrolujete nějaký stav, a pokud se splní dané podmínky zobrazí se notifikace, pokud ne tak nic neprovádíme
        //my pro účely ukázky posíláme notifikaci vždy
        notificationHelper.showNotification("CryptoApp", "Nezapomeň zkontrolovat ceny kryptoměn!")

        // Vracíme Result.success(), pokud se úloha provedla úspěšně
        return Result.success()
    }
}