package cz.razor.currrate.data

data class ExchangeRatesSingleDayResponse(
    val base: String?,
    val date: String?,
    val rates: Map<String, Double>?,
    val message: String?
)