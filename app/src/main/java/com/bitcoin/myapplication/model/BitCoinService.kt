package com.bitcoin.myapplication.model

import com.bitcoin.myapplication.data.BitCoinResponse
import retrofit2.http.GET


interface BitCoinService {

    @GET("v1/bpi/currentprice.json")
    suspend fun fetchBitCoin(): BitCoinResponse

}

