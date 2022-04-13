package com.bitcoin.myapplication.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Rate(
    val code: String?,
    val symbol: String?,
    val rate: String?,
    val description: String?,
    @Json(name = "rate_float") val rateFloat: Float?
) : Serializable

