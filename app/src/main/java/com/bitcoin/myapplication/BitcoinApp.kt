package com.bitcoin.myapplication

import android.app.Application
import androidx.lifecycle.Observer
import androidx.work.*
import com.bitcoin.myapplication.model.BitCoinRateWorker
import java.util.concurrent.TimeUnit

class BitcoinApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val myWorkRequest = PeriodicWorkRequestBuilder<BitCoinRateWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork(
            BitCoinRateWorker.Constants.WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP, myWorkRequest
        )

    }
}