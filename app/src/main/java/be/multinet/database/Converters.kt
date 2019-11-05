package be.multinet.database

import androidx.room.TypeConverter
import java.util.*

class Converters {
    /**
     * converter voor time
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}