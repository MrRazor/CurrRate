package cz.razor.currrate.data

import cz.razor.currrate.helpers.LocalDateConverter
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import java.time.LocalDate

@Entity
data class CurrencyRate(
    @Id var id: Long = 0,
    var baseCurrency: String,
    var toCurrency: String,
    @Convert(converter = LocalDateConverter::class, dbType = Long::class)
    var date: LocalDate,
    var rate: Double,
    @Unique var uniqueKey: String //${baseCurrency}-${toCurrency}-${date.toString()}
)