package cz.razor.currrate.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import cz.razor.currrate.consts.SettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    fun getBaseCurrencyCode(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[SettingsKeys.BASE_CURRENCY_CODE] ?: "EUR"
        }
    }

    suspend fun saveBaseCurrencyCode(baseCurrencyCode: String) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.BASE_CURRENCY_CODE] = baseCurrencyCode
        }
    }
}