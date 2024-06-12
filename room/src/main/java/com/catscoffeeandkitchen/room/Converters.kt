package com.catscoffeeandkitchen.room

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun toDate(isoFormattedDate: String?): OffsetDateTime? {
        if (isoFormattedDate == null) return null

        return OffsetDateTime.parse(isoFormattedDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    @TypeConverter
    fun toDateString(date: OffsetDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    @TypeConverter
    fun toStringList(unbrokenString: String?): List<String>? {
        return unbrokenString?.split("|")?.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun listToString(items: List<String>?): String? {
        return items?.joinToString("|")
    }
}
