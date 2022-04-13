package com.bitcoin.myapplication.model.repo


import android.content.Context
import com.bitcoin.myapplication.data.BitCoinResponse

interface BitCoinRepo {
    suspend fun fetchBitCoin(context: Context): BitCoinResponse
}