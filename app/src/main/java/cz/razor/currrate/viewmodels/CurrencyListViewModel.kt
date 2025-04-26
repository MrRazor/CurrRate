package cz.razor.currrate.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.api.FrankfurterApi
import cz.razor.currrate.data.CurrencyInfo
import cz.razor.currrate.data.CurrencyRate
import cz.razor.currrate.helpers.NetworkMonitorHelper
import cz.razor.currrate.repository.CurrencyInfoRepository
import cz.razor.currrate.repository.CurrencyRateRepository
import cz.razor.currrate.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class CurrencyListViewModel(private val frankfurterApi: FrankfurterApi,
                            private val currencyRateRepository: CurrencyRateRepository,
                            private val currencyInfoRepository: CurrencyInfoRepository,
                            private val settingsRepository: SettingsRepository,
                            private val networkMonitorHelper: NetworkMonitorHelper):ViewModel() {
    private val _currencyList = MutableStateFlow<ApiResult<List<CurrencyRate>>>(ApiResult.Loading)
    val currencyList: StateFlow<ApiResult<List<CurrencyRate>>> = _currencyList.asStateFlow()

    private val _currencyDetailList = MutableStateFlow<ApiResult<List<CurrencyInfo>>>(ApiResult.Loading)
    val currencyDetailList: StateFlow<ApiResult<List<CurrencyInfo>>> = _currencyDetailList.asStateFlow()

    fun getCurrencyList() {
        viewModelScope.launch {
            _currencyList.value = ApiResult.Loading
            try {
                val baseCurrency = settingsRepository.getBaseCurrencyCode().first()
                var currencyRateList = currencyRateRepository.getLatestRatesForBase(baseCurrency)
                if (currencyRateList.isNotEmpty() && (currencyRateList[0].date == LocalDate.now(ZoneId.of("CET")) || !networkMonitorHelper.isOnline.first())) {
                    _currencyList.value = ApiResult.Success(currencyRateList)
                }
                else {
                    val response = frankfurterApi.getRatesLatest(baseCurrency)
                    if (response.isSuccessful) {
                        currencyRateRepository.saveSingleDayResponse(response.body()!!)
                        currencyRateList = currencyRateRepository.getLatestRatesForBase(baseCurrency)
                        _currencyList.value = ApiResult.Success(currencyRateList)
                    } else {
                        _currencyList.value = ApiResult.Error(response.message())
                        Log.e("CurrencyListViewModel", "Error fetching currency list: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currencyList.value = ApiResult.Error(e.message ?: "")
                Log.e("CurrencyListViewModel", "Exception fetching currency list: ${e.message}")
            }
        }
    }

    fun getCurrencyDetailList() {
        viewModelScope.launch {
            _currencyDetailList.value = ApiResult.Loading
            try {
                var currencyInfoList = currencyInfoRepository.getAll()
                if(currencyInfoList.isNotEmpty()) {
                    _currencyDetailList.value = ApiResult.Success(currencyInfoList)
                }
                else {
                    val response = frankfurterApi.getCurrencies()
                    if (response.isSuccessful) {
                        currencyInfoRepository.saveAll(response.body()!!)
                        currencyInfoList = currencyInfoRepository.getAll()
                        _currencyDetailList.value = ApiResult.Success(currencyInfoList)
                    }
                    else {
                        _currencyDetailList.value = ApiResult.Error(response.message())
                        Log.e("CurrencyListViewModel", "Error fetching currency detail list: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currencyDetailList.value = ApiResult.Error(e.message ?: "")
                Log.e("CurrencyListViewModel", "Exception fetching currency detail list: ${e.message}")
            }
        }
    }

    fun addFavoriteCurrency(currencyInfo: CurrencyInfo) {
        viewModelScope.launch {
            val updatedCurrencyInfo = currencyInfo.copy(isToCurrencyFavourite = true)
            currencyInfoRepository.update(updatedCurrencyInfo)
            getCurrencyDetailList()
        }
    }

    fun removeFavoriteCurrency(currencyInfo: CurrencyInfo) {
        viewModelScope.launch {
            val updatedCurrencyInfo = currencyInfo.copy(isToCurrencyFavourite = false)
            currencyInfoRepository.update(updatedCurrencyInfo)
            getCurrencyDetailList()
        }
    }

}