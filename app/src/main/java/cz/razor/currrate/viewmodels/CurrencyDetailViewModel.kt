package cz.razor.currrate.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.api.FrankfurterApi
import cz.razor.currrate.consts.SettingsKeys
import cz.razor.currrate.data.CurrencyInfo
import cz.razor.currrate.data.CurrencyRate
import cz.razor.currrate.repository.CurrencyInfoRepository
import cz.razor.currrate.repository.CurrencyRateRepository
import cz.razor.currrate.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class CurrencyDetailViewModel(private val frankfurterApi: FrankfurterApi,
                              private val currencyRateRepository: CurrencyRateRepository,
                              private val currencyInfoRepository: CurrencyInfoRepository,
                              private val settingsRepository: SettingsRepository):ViewModel() {
    private val _currency = MutableStateFlow<ApiResult<CurrencyRate>>(ApiResult.Loading)
    val currency: StateFlow<ApiResult<CurrencyRate>> = _currency.asStateFlow()

    private val _currencyYesterday = MutableStateFlow<ApiResult<CurrencyRate>>(ApiResult.Loading)
    val currencyYesterday: StateFlow<ApiResult<CurrencyRate>> = _currencyYesterday.asStateFlow()

    private val _currencyDetail = MutableStateFlow<ApiResult<CurrencyInfo>>(ApiResult.Loading)
    val currencyDetail: StateFlow<ApiResult<CurrencyInfo>> = _currencyDetail.asStateFlow()

    val baseCurrency: StateFlow<String> = settingsRepository.getBaseCurrencyCode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsKeys.DEFAULT_BASE_CURRENCY_CODE)

    fun getSingleCurrency(to: String, date: LocalDate) {
        viewModelScope.launch {
            _currency.value = ApiResult.Loading
            _currencyYesterday.value = ApiResult.Loading
            try {
                val baseCurrency = settingsRepository.getBaseCurrencyCode().first()
                var currencyRate = currencyRateRepository.getRate(baseCurrency, to, date)
                val yesterdayDate = date.minusDays(1)
                var currencyRateYesterday = currencyRateRepository.getRate(baseCurrency, to, yesterdayDate)
                if (currencyRate != null) {
                    _currency.value = ApiResult.Success(currencyRate)
                }
                else {
                    val response = frankfurterApi.getRatesForDay(date.toString(), baseCurrency, to)
                    if (response.isSuccessful) {
                        currencyRateRepository.saveSingleDayResponse(response.body()!!)
                        currencyRate = currencyRateRepository.getRate(baseCurrency, to, date)
                        _currency.value = ApiResult.Success(currencyRate!!)
                    } else {
                        _currency.value = ApiResult.Error("Error fetching currency: ${response.message()}")
                        Log.e("CurrencyDetailViewModel", "Error fetching currency: ${response.message()}")
                    }
                }
                if (currencyRateYesterday != null) {
                    _currencyYesterday.value = ApiResult.Success(currencyRateYesterday)
                }
                else {
                    val response = frankfurterApi.getRatesForDay(yesterdayDate.toString(), baseCurrency, to)
                    if (response.isSuccessful) {
                        currencyRateRepository.saveSingleDayResponse(response.body()!!)
                        currencyRateYesterday = currencyRateRepository.getRate(baseCurrency, to, yesterdayDate)
                        _currencyYesterday.value = ApiResult.Success(currencyRateYesterday!!)
                    } else {
                        _currencyYesterday.value = ApiResult.Error("Error fetching previous value of this currency: ${response.message()}")
                        Log.e("CurrencyDetailViewModel", "Error fetching previous value of this currency: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currency.value = ApiResult.Error("Exception fetching currency: ${e.message}")
                _currencyYesterday.value = ApiResult.Error("Exception fetching previous value of this currency: ${e.message}")
                Log.e("CurrencyDetailViewModel", "Exception fetching (previous) value of this currency: ${e.message}")
            }
        }
    }

    fun getSingleCurrencyDetail(code: String) {
        viewModelScope.launch {
            _currencyDetail.value = ApiResult.Loading
            try {
                var currencyInfo = currencyInfoRepository.getByCode(code)
                if (currencyInfo != null) {
                    _currencyDetail.value = ApiResult.Success(currencyInfo)
                }
                else {
                    val response = frankfurterApi.getCurrencies()
                    if (response.isSuccessful) {
                        currencyInfoRepository.saveAll(response.body()!!)
                        currencyInfo = currencyInfoRepository.getByCode(code)
                        _currencyDetail.value = ApiResult.Success(currencyInfo!!)
                    } else {
                        _currencyDetail.value = ApiResult.Error("Error fetching currency detail: ${response.message()}")
                        Log.e("CurrencyDetailViewModel", "Error fetching currency detail: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currencyDetail.value = ApiResult.Error("Exception fetching currency detail: ${e.message}")
                Log.e("CurrencyDetailViewModel", "Exception fetching currency detail: ${e.message}")
            }
        }
    }

    fun addFavoriteCurrency(currencyInfo: CurrencyInfo) {
        viewModelScope.launch {
            val updatedCurrencyInfo = currencyInfo.copy(isToCurrencyFavourite = true)
            currencyInfoRepository.update(updatedCurrencyInfo)
            getSingleCurrencyDetail(currencyInfo.code)
        }
    }

    fun removeFavoriteCurrency(currencyInfo: CurrencyInfo) {
        viewModelScope.launch {
            val updatedCurrencyInfo = currencyInfo.copy(isToCurrencyFavourite = false)
            currencyInfoRepository.update(updatedCurrencyInfo)
            getSingleCurrencyDetail(currencyInfo.code)
        }
    }

}