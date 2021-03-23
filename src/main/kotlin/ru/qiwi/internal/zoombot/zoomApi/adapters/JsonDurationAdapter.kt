package ru.qiwi.internal.zoombot.zoomApi.adapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.math.roundToLong

@Component
class JsonDurationAdapter : JsonAdapter<Duration>() {
    override fun toJson(writer: JsonWriter, input: Duration?) {
        val s = input?.toString()
        writer.value(s)
    }

    override fun fromJson(reader: JsonReader): Duration? {
        val s = reader.readJsonValue() as Double
        return Duration.ofMinutes(s.roundToLong())
    }
}
