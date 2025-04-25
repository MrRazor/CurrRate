package cz.razor.currrate.api

import cz.razor.currrate.data.ExchangeRatesSingleDayResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response

interface FrankfurterApi {

    @GET("v1/latest")
    suspend fun getRatesLatest(
        @Query("base") base: String? = null,
        @Query("symbols") symbols: String? = null
    ): Response<ExchangeRatesSingleDayResponse>

    @GET("v1/{date}")
    suspend fun getRatesForDay(
        @Path("date") date: String,
        @Query("base") base: String? = null,
        @Query("symbols") symbols: String? = null
    ): Response<ExchangeRatesSingleDayResponse>

    @GET("v1/currencies")
    suspend fun getCurrencies(): Response<Map<String, String>>
}