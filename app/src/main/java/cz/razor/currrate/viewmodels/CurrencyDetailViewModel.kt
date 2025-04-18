package cz.razor.currrate.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.api.FrankfurterApi
import cz.razor.currrate.data.CurrencyInfo
import cz.razor.currrate.data.CurrencyRate
import cz.razor.currrate.repository.CurrencyInfoRepository
import cz.razor.currrate.repository.CurrencyRateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CurrencyDetailViewModel(private val frankfurterApi: FrankfurterApi, private val currencyRateRepository: CurrencyRateRepository, private val currencyInfoRepository: CurrencyInfoRepository):ViewModel() {
    private val _currency = MutableStateFlow<ApiResult<CurrencyRate>>(ApiResult.Loading)
    val currency: StateFlow<ApiResult<CurrencyRate>> = _currency.asStateFlow()

    private val _currencyDetail = MutableStateFlow<ApiResult<CurrencyInfo>>(ApiResult.Loading)
    val currencyDetail: StateFlow<ApiResult<CurrencyInfo>> = _currencyDetail.asStateFlow()

    fun getSingleCurrency(base: String, to: String, date: LocalDate) {
        viewModelScope.launch {
            _currency.value = ApiResult.Loading
            try {
                var currencyRate = currencyRateRepository.getRate(base, to, date)
                if (currencyRate != null) {
                    _currency.value = ApiResult.Success(currencyRate)
                }
                else {
                    val response = frankfurterApi.getRatesForDay(date.toString(), base)
                    if (response.isSuccessful) {
                        currencyRateRepository.saveSingleDayResponse(response.body()!!)
                        currencyRate = currencyRateRepository.getRate(base, to, date)
                        _currency.value = ApiResult.Success(currencyRate!!)
                    } else {
                        _currency.value = ApiResult.Error("Error fetching currency: ${response.message()}")
                        Log.e("CurrencyDetailViewModel", "Error fetching currency: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currency.value = ApiResult.Error("Exception fetching currency: ${e.message}")
                Log.e("CurrencyDetailViewModel", "Exception fetching currency: ${e.message}")
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