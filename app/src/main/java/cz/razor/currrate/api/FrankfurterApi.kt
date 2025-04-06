package cz.razor.currrate.api

import cz.razor.currrate.data.ExchangeRatesPeriodResponse
import cz.razor.currrate.data.ExchangeRatesSingleDayResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response

interface FrankfurterApi {

    @GET("v1/{date}")
    fun getRatesForDay(
        @Path("date") date: String,
        @Query("base") base: String? = null,
        @Query("symbols") symbols: String? = null
    ): Response<ExchangeRatesSingleDayResponse>

    @GET("v1/{startDate}..{endDate}")
    fun getRatesForPeriod(
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @Query("base") base: String? = null,
        @Query("symbols") symbols: String? = null
    ): Response<ExchangeRatesPeriodResponse>

    @GET("v1/currencies")
    fun getCurrencies(): Response<Map<String, String>>
}