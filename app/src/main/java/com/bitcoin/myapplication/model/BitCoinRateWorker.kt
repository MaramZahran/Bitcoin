package com.bitcoin.myapplication.model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.bitcoin.myapplication.R
import com.bitcoin.myapplication.data.BitCoinResponse
import com.bitcoin.myapplication.model.repo.BitCoinRepo
import com.bitcoin.myapplication.model.repo.BitCoinRepoImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BitCoinRateWorker(
    appContext: Context,
    workerParams: WorkerParameters
) :
    CoroutineWorker(appContext, workerParams) {

    private val bitCoinRepo: BitCoinRepo = BitCoinRepoImp()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.channel_name)
            val descriptionText = applicationContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            try {
                val minRate = fetchRateValue(Constants.MIN_RATE)
                val maxRate = fetchRateValue(Constants.MAX_RATE)
                val bitcoin = bitCoinRepo.fetchBitCoin(applicationContext)
                showNotification(minRate, maxRate, bitcoin)

                Result.success()
            } catch (e: Exception) {
                Result.failure(workDataOf(Constants.ERROR_KEY to e.message.toString()))
            }
        }


    }

    private fun fetchRateValue(key: String): Float {
        val sharedPreferences = applicationContext.getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getFloat(key, 0F)
    }

    private fun showNotification(minRate: Float, maxRate: Float, bitcoin: BitCoinResponse) {
        bitcoin.bpi?.usd?.rateFloat?.let {

            if (minRate == 0F || minRate > it) {
                publishNotification("${applicationContext.getString(R.string.min_Bitcoin_rate)} $it")
            } else if (maxRate == 0F || maxRate < it) {
                publishNotification("${applicationContext.getString(R.string.max_Bitcoin_rate)} $it")

            } else {
                publishNotification("${applicationContext.getString(R.string.current_Bitcoin_rate)} $it")
            }
        }
    }

    private fun publishNotification(message: String) {
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(
                Constants.NOTIFICATION_ID,
                createNotification(message)
            )
        }
    }

    private fun createNotification(textContent: String): Notification {
        return NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    object Constants {

        const val SHARED_PREFERENCES_NAME = "BITCOIN"
        const val WORKER_NAME = "Bitcoin"
        const val ERROR_KEY = "error"
        const val MIN_RATE = "min_rate"
        const val MAX_RATE = "max_rate"
        const val CURRENT_RATE = "current_rate"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "BITCOIN"
    }
}