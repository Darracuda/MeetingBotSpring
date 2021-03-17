package ru.qiwi.internal.zoombot.zoomApi.models

import com.squareup.moshi.Json
import java.time.Instant

data class CreateMeetingResponse (
    @Json(name = "host_id") val hostId: String,
    val id: String,
    val uuid: String,
    @Json(name = "start_time") val startTime: Instant,
    @Json(name = "created_at") val createdAt: Instant,
    @Json(name = "start_url") val startUrl: String,
    @Json(name = "join_url") val joinUrl: String,
    @Json(name = "encrypted_password") val encryptedPassword: String,
    @Json(name = "pstn_password") val pstnPassword: String,
    @Json(name = "host_email") val hostEmail: String,
)

