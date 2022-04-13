package com.bitcoin.myapplication.model.repo

import android.content.Context
import com.bitcoin.myapplication.data.BitCoinResponse
import com.bitcoin.myapplication.model.BitCoinRateWorker
import com.bitcoin.myapplication.model.BitCoinService
import com.bitcoin.myapplication.model.NetworkHelper

class BitCoinRepoImp : BitCoinRepo {

    private val retrofit: BitCoinService by lazy {
        with(NetworkHelper) {
            retrofit(BASE_URL)
                .create(BitCoinService::class.java)
        }
    }

    override suspend fun fetchBitCoin(context: Context): BitCoinResponse {
        val bitCoinResponse = retrofit.fetchBitCoin()
        persistBitcoinRate(bitCoinResponse, context)
        return bitCoinResponse
    }

    private fun persistBitcoinRate(bitcoin: BitCoinResponse, context: Context) {
        bitcoin.bpi?.usd?.rateFloat?.let {
            val sharedPreferences = context.getSharedPreferences(
                BitCoinRateWorker.Constants.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putFloat(BitCoinRateWorker.Constants.CURRENT_RATE, it)
            val minRate = sharedPreferences.getFloat(BitCoinRateWorker.Constants.MIN_RATE, 0F)
            if (minRate == 0F || minRate > it) {
                editor.putFloat(BitCoinRateWorker.Constants.MIN_RATE, it)
            }
            val maxRate = sharedPreferences.getFloat(BitCoinRateWorker.Constants.MAX_RATE, 0F)
            if (maxRate == 0F || maxRate < it) {
                editor.putFloat(BitCoinRateWorker.Constants.MAX_RATE, it)
            }
            editor.apply()
        }

    }


}