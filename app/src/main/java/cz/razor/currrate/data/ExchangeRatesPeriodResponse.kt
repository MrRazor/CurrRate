package cz.razor.currrate.data

import com.google.gson.annotations.SerializedName

data class ExchangeRatesPeriodResponse(
    @SerializedName("base") val base: String?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("rates") val rates: Map<String, Map<String, Double>>?,
    @SerializedName("message") val message: String?
)