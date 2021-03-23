package ru.qiwi.internal.zoombot.zoomApi.models

import com.squareup.moshi.Json
import java.time.Duration
import java.time.Instant

data class GetMeetingListResponse (
    val meetings: Array<ExistingMeeting>,
    val page_count: Int,
    val page_number: Int,
    val page_size: Int,
    val total_records: Int,
)

data class ExistingMeeting(
    @Json(name = "host_id") val hostId: String,
    val id: String,
    val uuid: String,
    val topic: String,
    val type: String,
    val duration: Duration,
    val timezone: String,
    val agenda: String?,
    @Json(name = "start_time") val startTime: Instant,
    @Json(name = "created_at") val createdAt: Instant,
    @Json(name = "join_url") val joinUrl: String,
)
