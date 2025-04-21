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
import cz.razor.currrate.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.ArrayList

class CurrencyGraphViewModel(private val frankfurterApi: FrankfurterApi,
                             private val currencyRateRepository: CurrencyRateRepository,
                             private val currencyInfoRepository: CurrencyInfoRepository,
                             private val settingsRepository: SettingsRepository):ViewModel() {

     private val _currencyList = MutableStateFlow<ApiResult<List<CurrencyRate>>>(ApiResult.Loading)
    val currencyList: StateFlow<ApiResult<List<CurrencyRate>>> = _currencyList.asStateFlow()

    private val _currencyDetail = MutableStateFlow<ApiResult<CurrencyInfo>>(ApiResult.Loading)
    val currencyDetail: StateFlow<ApiResult<CurrencyInfo>> = _currencyDetail.asStateFlow()

    val baseCurrency: StateFlow<String> = settingsRepository.getBaseCurrencyCode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "EUR")

    fun getSingleCurrency(to: String, date: LocalDate) {
        viewModelScope.launch {
            _currencyList.value = ApiResult.Loading
            try {
                val baseCurrency = settingsRepository.getBaseCurrencyCode().first()
                val currencyRates = ArrayList<CurrencyRate>()
                for (i in 0L..4L) {
                    var currencyRate = currencyRateRepository.getRate(baseCurrency, to, date.minusDays(i))
                    if (currencyRate != null) {
                        currencyRates.add(currencyRate)
                    }
                    else {
                        val response = frankfurterApi.getRatesForDay(date.minusDays(i).toString(), baseCurrency)
                        if (response.isSuccessful) {
                            currencyRateRepository.saveSingleDayResponse(response.body()!!)
                            currencyRate = currencyRateRepository.getRate(baseCurrency, to, date.minusDays(i))
                            currencyRates.add(currencyRate!!)
                        } else {
                            _currencyList.value = ApiResult.Error("Error fetching currency list: ${response.message()}")
                            Log.e("CurrencyGraphViewModel", "Error fetching currency list: ${response.message()}")
                            break;
                        }
                    }
                }
                if(currencyRates.size == 5) {
                    _currencyList.value = ApiResult.Success(currencyRates)
                }
            } catch (e: Exception) {
                _currencyList.value = ApiResult.Error("Exception fetching currency list: ${e.message}")
                Log.e("CurrencyGraphViewModel", "Exception fetching currency list: ${e.message}")
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
                        Log.e("CurrencyGraphViewModel", "Error fetching currency detail: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currencyDetail.value = ApiResult.Error("Exception fetching currency detail: ${e.message}")
                Log.e("CurrencyGraphViewModel", "Exception fetching currency detail: ${e.message}")
            }
        }
    }

}