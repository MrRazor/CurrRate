package cz.razor.currrate.repository

import cz.razor.currrate.data.CurrencyInfo
import cz.razor.currrate.data.CurrencyInfo_
import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException

class CurrencyInfoRepository(private val box: Box<CurrencyInfo>) {

    fun saveAll(currencies: Map<String, String>) {
        val entities = currencies.map { (code, name) ->
            CurrencyInfo(
                code = code,
                name = name
            )
        }
        entities.forEach {
            try {
                box.put(it)
            } catch (e: UniqueViolationException) {
                // Optional: handle duplicate or update existing record
            }
        }
    }

    fun update(currencyInfo: CurrencyInfo) {
        box.put(currencyInfo)
    }

    fun getAll(): List<CurrencyInfo> = box.all
    fun getAllCodes(): List<String> = box.all.map {currencyInfo -> currencyInfo.code}

    fun getByCode(code: String): CurrencyInfo? =
        box.query(CurrencyInfo_.code.equal(code)).build().findFirst()
}