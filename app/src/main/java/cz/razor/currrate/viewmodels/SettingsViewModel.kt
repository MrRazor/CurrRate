package cz.razor.currrate.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.api.FrankfurterApi
import cz.razor.currrate.repository.CurrencyInfoRepository
import cz.razor.currrate.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val frankfurterApi: FrankfurterApi,
                        private val currencyInfoRepository: CurrencyInfoRepository,
                        private val settingsRepository: SettingsRepository) :
    ViewModel() {

    private val _currencyCodeList = MutableStateFlow<ApiResult<List<String>>>(ApiResult.Loading)
    val currencyCodeList: StateFlow<ApiResult<List<String>>> = _currencyCodeList.asStateFlow()

    val baseCurrency: StateFlow<String> = settingsRepository.getBaseCurrencyCode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "EUR")

    fun saveBaseCurrency(baseCurrencyCode: String) {
        viewModelScope.launch {
            settingsRepository.saveBaseCurrencyCode(baseCurrencyCode)
        }
    }

    fun getCurrencyCodeList() {
        viewModelScope.launch {
            _currencyCodeList.value = ApiResult.Loading
            try {
                var currencyInfoList = currencyInfoRepository.getAllCodes()
                if(currencyInfoList.isNotEmpty()) {
                    _currencyCodeList.value = ApiResult.Success(currencyInfoList)
                }
                else {
                    val response = frankfurterApi.getCurrencies()
                    if (response.isSuccessful) {
                        currencyInfoRepository.saveAll(response.body()!!)
                        currencyInfoList = currencyInfoRepository.getAllCodes()
                        _currencyCodeList.value = ApiResult.Success(currencyInfoList)
                    }
                    else {
                        _currencyCodeList.value = ApiResult.Error("Error fetching currency code list: ${response.message()}")
                        Log.e("SettingsViewModel", "Error fetching currency code list: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _currencyCodeList.value = ApiResult.Error("Exception fetching currency code list: ${e.message}")
                Log.e("SettingsViewModel", "Exception fetching currency code list: ${e.message}")
            }
        }
    }

}