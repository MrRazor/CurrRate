package cz.razor.currrate.consts

import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {
    val BASE_CURRENCY_CODE = stringPreferencesKey("base_currency_code")
    val DEFAULT_BASE_CURRENCY_CODE = "EUR"
}