package com.bitcoin.myapplication.ui.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitcoin.myapplication.data.BitCoinRangeRate
import com.bitcoin.myapplication.data.Resource
import com.bitcoin.myapplication.model.BitCoinRateWorker
import com.bitcoin.myapplication.model.repo.BitCoinRepoImp
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val sharedPreferences: SharedPreferences =
        getApplication<Application>().applicationContext.getSharedPreferences(
            BitCoinRateWorker.Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE
        )

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }


    private val repo = BitCoinRepoImp()
    private val _uiStatus = MutableLiveData<Resource<BitCoinRangeRate>>(Resource.loading(null))
    val uiStatus: LiveData<Resource<BitCoinRangeRate>> = _uiStatus

    fun getBitCoin() {

        viewModelScope.launch {
            try {
                val bitCoinResponse =
                    repo.fetchBitCoin(getApplication<Application>().applicationContext)
                val bitCoinRangeRate = constructBitCoinRange(
                    bitCoinResponse.bpi?.usd?.rateFloat
                )
                _uiStatus.value = Resource.success(bitCoinRangeRate)
            } catch (e: Exception) {
                _uiStatus.value = Resource.error(e.message.toString(), constructBitCoinRange(null))
            }
        }
    }

    private fun constructBitCoinRange(currentRate: Float?): BitCoinRangeRate {
        return BitCoinRangeRate(
            sharedPreferences.getFloat(BitCoinRateWorker.Constants.MIN_RATE, 0F),
            sharedPreferences.getFloat(BitCoinRateWorker.Constants.MAX_RATE, 0F),
            currentRate ?: sharedPreferences.getFloat(BitCoinRateWorker.Constants.CURRENT_RATE, 0F)
        )
    }

    override fun onSharedPreferenceChanged(sharedPreference: SharedPreferences?, key: String?) {
        sharedPreference?.let {
            if (key == BitCoinRateWorker.Constants.CURRENT_RATE) {
                _uiStatus.value =
                    Resource.success(constructBitCoinRange(it.getFloat(key, 0F)))
            }
        }
    }
}