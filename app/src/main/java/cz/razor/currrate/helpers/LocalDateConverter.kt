package cz.razor.currrate.helpers

import io.objectbox.converter.PropertyConverter
import java.time.LocalDate

class LocalDateConverter : PropertyConverter<LocalDate, Long> {
    override fun convertToEntityProperty(databaseValue: Long?): LocalDate {
        return databaseValue?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.MIN
    }

    override fun convertToDatabaseValue(entityProperty: LocalDate?): Long {
        return entityProperty?.toEpochDay() ?: 0L
    }
}