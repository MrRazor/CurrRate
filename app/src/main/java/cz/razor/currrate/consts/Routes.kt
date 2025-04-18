package cz.razor.currrate.consts

import java.time.LocalDate

object Routes {
    const val CurrencyList = "currencyList"
    const val CurrencyDetail = "currencyDetail/{to}/{date}"
    const val FavouriteCurrencyList = "favouriteCurrencyList"
    const val Settings = "settings"

    fun currencyDetail(to: String, date: LocalDate): String {
        return "currencyDetail/$to/$date"
    }
}