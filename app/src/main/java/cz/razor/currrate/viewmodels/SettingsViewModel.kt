package cz.uhk.fim.cryptoapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.razor.currrate.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) :
    ViewModel() {

    val preferredRateId: StateFlow<String?> = settingsRepository.getBaseCurrencyCode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun savePreferredRateId(baseCurrencyCode: String) {
        viewModelScope.launch {
            settingsRepository.saveBaseCurrencyCode(baseCurrencyCode)
        }
    }
}