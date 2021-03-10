package qiwi.zoombot.spring.zoomApi.infrastructure

import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter
import qiwi.zoombot.spring.zoomApi.adapters.JsonInstantAdapter
import java.time.Instant
import java.util.*

object Serializer {
    @JvmStatic
    val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .add(Instant::class.java, JsonInstantAdapter())
            .build()
}