package cz.razor.currrate.consts

import java.time.LocalDate

object Routes {
    const val CurrencyList = "currencyList"
    const val CurrencyDetail = "currencyDetail/{base}/{to}/{date}"
    const val FavouriteCurrencyList = "favouriteCurrencyList"
    const val Settings = "settings"

    fun currencyDetail(base: String, to: String, date: LocalDate): String {
        return "currencyDetail/$base/$to/$date"
    }
}