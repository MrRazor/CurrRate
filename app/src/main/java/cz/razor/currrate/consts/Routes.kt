package cz.razor.currrate.consts

object Routes {
    const val CurrencyList = "currencyList"
    const val CurrencyDetail = "currencyDetail/{currencyId}"
    const val FavouriteCurrency = "favouriteCurrency"
    const val Settings = "settings"

    fun currencyDetail(currencyId: String): String {
        return "currencyDetail/$currencyId"
    }
}