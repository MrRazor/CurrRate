package cz.razor.currrate.repository

import cz.razor.currrate.data.CurrencyInfo
import cz.razor.currrate.data.CurrencyInfo_
import io.objectbox.Box

class CurrencyInfoRepository(private val box: Box<CurrencyInfo>) {

    fun saveAll(currencies: Map<String, String>) {
        val entities = currencies.map { (code, name) ->
            CurrencyInfo(
                code = code,
                name = name
            )
        }
        box.put(entities)
    }

    fun getAll(): List<CurrencyInfo> = box.all

    fun getByCode(code: String): CurrencyInfo? =
        box.query(CurrencyInfo_.code.equal(code)).build().findFirst()
}