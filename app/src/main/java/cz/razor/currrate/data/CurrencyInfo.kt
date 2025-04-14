package cz.razor.currrate.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class CurrencyInfo(
    @Id var id: Long = 0,
    @Unique var code: String,
    var name: String,
    var isToCurrencyFavourite: Boolean = false
)