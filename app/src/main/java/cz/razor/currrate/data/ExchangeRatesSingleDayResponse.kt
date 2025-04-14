package cz.razor.currrate.data

import com.google.gson.annotations.SerializedName

data class ExchangeRatesSingleDayResponse(
    @SerializedName("base") val base: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("rates") val rates: Map<String, Double>?,
    @SerializedName("message") val message: String?
)