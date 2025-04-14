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

class CurrencyViewModel(private val frankfurterApi: FrankfurterApi, private val currencyRateRepository: CurrencyRateRepository, private val currencyInfoRepository: CurrencyInfoRepository):ViewModel() {
    private val _currencyList = MutableStateFlow<ApiResult<List<CurrencyRate>>>(ApiResult.Loading)
    val currencyList: StateFlow<ApiResult<List<CurrencyRate>>> = _currencyList.asStateFlow()

    private val _currencyDetailList = MutableStateFlow<ApiResult<List<CurrencyInfo>>>(ApiResult.Loading)
    val currencyDetailList: StateFlow<ApiResult<List<CurrencyInfo>>> = _currencyDetailList.asStateFlow()

    private val _currency = MutableStateFlow<ApiResult<CurrencyRate>>(ApiResult.Loading)
    val currency: StateFlow<ApiResult<CurrencyRate>> = _currency.asStateFlow()

    private val _currencyDetail = MutableStateFlow<ApiResult<CurrencyInfo>>(ApiResult.Loading)
    val currencyDetail: StateFlow<ApiResult<CurrencyInfo>> = _currencyDetail.asStateFlow()

    fun getCurrencyList() {
        viewModelScope.launch {
            _currencyList.value = ApiResult.Loading
            try {
                val baseCurrency = "EUR"
                var currencyRateList = currencyRateRepository.getLatestRatesForBase(baseCurrency)
                if (currencyRateList.isNotEmpty()) {
                    _currencyList.value = ApiResult.Success(currencyRateList)
                }
                else {
                    val response = frankfurterApi.getRatesLatest()
                    if (response.isSuccessful) {
                        currencyRateRepository.saveSingleDayResponse(response.body()!!)
                        currencyRateList = currencyRateRepository.getLatestRatesForBase(baseCurrency)
                        _currencyList.value = ApiResult.Success(currencyRateList)
                    } else {
                        _currencyList.value = ApiResult.Error("Error fetching currency list: ${response.message()}")
                        Log.e("CurrencyViewModel", "Error fetching currency list: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currencyList.value = ApiResult.Error("Exception fetching currency list: ${e.message}")
                Log.e("CurrencyViewModel", "Exception fetching currency list: ${e.message}")
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
                        _currencyDetailList.value = ApiResult.Error("Error fetching currency detail list: ${response.message()}")
                        Log.e("CurrencyViewModel", "Error fetching currency detail list: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currencyDetailList.value = ApiResult.Error("Exception fetching currency detail list: ${e.message}")
                Log.e("CurrencyViewModel", "Exception fetching currency detail list: ${e.message}")
            }
        }
    }

    fun getSingleCurrency(base: String, to: String, date: LocalDate) {
        viewModelScope.launch {
            _currency.value = ApiResult.Loading
            try {
                var currencyRate = currencyRateRepository.getRate(base, to, date)
                if (currencyRate != null) {
                    _currency.value = ApiResult.Success(currencyRate)
                }
                else {
                    val response = frankfurterApi.getRatesForDay(date.toString())
                    if (response.isSuccessful) {
                        currencyRateRepository.saveSingleDayResponse(response.body()!!)
                        currencyRate = currencyRateRepository.getRate(base, to, date)
                        _currency.value = ApiResult.Success(currencyRate!!)
                    } else {
                        _currency.value = ApiResult.Error("Error fetching currency: ${response.message()}")
                        Log.e("CurrencyViewModel", "Error fetching currency: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currency.value = ApiResult.Error("Exception fetching currency: ${e.message}")
                Log.e("CurrencyViewModel", "Exception fetching currency: ${e.message}")
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
                        Log.e("CurrencyViewModel", "Error fetching currency detail: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currencyDetail.value = ApiResult.Error("Exception fetching currency detail: ${e.message}")
                Log.e("CurrencyViewModel", "Exception fetching currency detail: ${e.message}")
            }
        }
    }

}