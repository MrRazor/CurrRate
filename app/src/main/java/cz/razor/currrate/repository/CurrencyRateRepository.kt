package cz.razor.currrate.repository

import cz.razor.currrate.data.CurrencyRate
import cz.razor.currrate.data.CurrencyRate_
import cz.razor.currrate.data.ExchangeRatesPeriodResponse
import cz.razor.currrate.data.ExchangeRatesSingleDayResponse
import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException
import java.time.LocalDate

class CurrencyRateRepository(private val box: Box<CurrencyRate>) {

    fun saveSingleDayResponse(response: ExchangeRatesSingleDayResponse) {
        if(response.base == null
            || response.rates == null
            || response.date == null
            || response.message != null) {
            return;
        }

        val date = LocalDate.parse(response.date)
        val base = response.base

        val entities: List<CurrencyRate> = response.rates.map { (toCurrency, rate) ->
                CurrencyRate(
                    baseCurrency = base,
                    toCurrency = toCurrency,
                    date = date,
                    rate = rate,
                    uniqueKey = "$base-$toCurrency-${date}"
                )
        }

        entities.forEach {
            try {
                box.put(it)
            } catch (e: UniqueViolationException) {
                // Optional: log or update instead
            }
        }
    }

    fun savePeriodResponse(response: ExchangeRatesPeriodResponse) {
        if(response.base == null
            || response.rates == null
            || response.startDate == null
            || response.endDate == null
            || response.message != null) {
            return;
        }

        val base = response.base

        val entities = response.rates.flatMap { (dateStr, rates) ->
            val date = LocalDate.parse(dateStr)
            rates.map { (toCurrency, rate) ->
                CurrencyRate(
                    baseCurrency = base,
                    toCurrency = toCurrency,
                    date = date,
                    rate = rate,
                    uniqueKey = "$base-$toCurrency-${date}"
                )
            }
        }

        entities.forEach {
            try {
                box.put(it)
            } catch (e: UniqueViolationException) {
                // Optional: handle duplicate or update existing record
            }
        }
    }

    fun getRatesForBaseAndDate(baseCurrency: String, date: LocalDate): List<CurrencyRate> {
        return box.query(CurrencyRate_.baseCurrency.equal(baseCurrency).and(CurrencyRate_.date.equal(date.toEpochDay())))
            .build()
            .find()
    }

    fun getRate(base: String, to: String, date: LocalDate): CurrencyRate? {
        val key = "$base-$to-${date}"
        return box.query(CurrencyRate_.uniqueKey.equal(key))
            .build()
            .findFirst()
    }
}