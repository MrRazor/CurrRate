package cz.razor.currrate.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.api.FrankfurterApi
import cz.razor.currrate.consts.SettingsKeys
import cz.razor.currrate.data.CurrencyRate
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
                             private val settingsRepository: SettingsRepository):ViewModel() {

     private val _currencyList = MutableStateFlow<ApiResult<List<CurrencyRate>>>(ApiResult.Loading)
    val currencyList: StateFlow<ApiResult<List<CurrencyRate>>> = _currencyList.asStateFlow()

    val baseCurrency: StateFlow<String> = settingsRepository.getBaseCurrencyCode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsKeys.DEFAULT_BASE_CURRENCY_CODE)

    var skippedDays = 0L

    fun getCurrencyList(to: String, date: LocalDate) {
        viewModelScope.launch {
            _currencyList.value = ApiResult.Loading
            try {
                val baseCurrency = settingsRepository.getBaseCurrencyCode().first()
                val currencyRates = ArrayList<CurrencyRate>()
                for (i in 0L..4L) {
                    if (loadCurrencyValueForDayMinusDays(baseCurrency, to, date, i, currencyRates)) break
                }
                if(currencyRates.size == 5) {
                    _currencyList.value = ApiResult.Success(currencyRates)
                }
            } catch (e: Exception) {
                _currencyList.value = ApiResult.Error(e.message ?: "")
                Log.e("CurrencyGraphViewModel", "Exception fetching currency list: ${e.message}")
            }
        }
    }

    private suspend fun loadCurrencyValueForDayMinusDays(
        baseCurrency: String,
        to: String,
        date: LocalDate,
        minusDays: Long,
        currencyRates: ArrayList<CurrencyRate>
    ): Boolean {
        val minusDaysAfterSkippedDays = minusDays + skippedDays
        var currencyRate = currencyRateRepository.getRate(baseCurrency, to, date.minusDays(minusDaysAfterSkippedDays))
        if (currencyRate != null) {
            currencyRates.add(currencyRate)
        } else {
            val response = frankfurterApi.getRatesForDay(date.minusDays(minusDaysAfterSkippedDays).toString(), baseCurrency, to)
            if (response.isSuccessful) {
                currencyRateRepository.saveSingleDayResponse(response.body()!!)
                currencyRate = currencyRateRepository.getRate(baseCurrency, to, date.minusDays(minusDaysAfterSkippedDays))
                if(currencyRate == null) {
                    skippedDays += 1L;
                    loadCurrencyValueForDayMinusDays(baseCurrency, to, date, minusDays, currencyRates)
                }
                else {
                    currencyRates.add(currencyRate)
                }
            } else {
                _currencyList.value =
                    ApiResult.Error(response.message())
                Log.e(
                    "CurrencyGraphViewModel",
                    "Error fetching currency list: ${response.message()}"
                )
                return true
            }
        }
        return false
    }

}