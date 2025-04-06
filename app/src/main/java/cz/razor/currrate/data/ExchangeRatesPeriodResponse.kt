package cz.razor.currrate.data

import com.google.gson.annotations.SerializedName

data class ExchangeRatesPeriodResponse(
    val base: String?,

    @SerializedName("start_date")
    val startDate: String?,

    @SerializedName("end_date")
    val endDate: String?,

    val rates: Map<String, Map<String, Double>>?,

    val message: String?
)