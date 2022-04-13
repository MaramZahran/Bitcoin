package com.bitcoin.myapplication.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Bpi(

    @Json(name = "USD") var usd: Rate?,
    @Json(name = "GBP") var gbp: Rate?,
    @Json(name = "EUR") var eur: Rate?


)
