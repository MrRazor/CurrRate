package cz.razor.currrate.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class CurrencyInfo(
    @Id var id: Long = 0,
    var code: String,
    var name: String
)