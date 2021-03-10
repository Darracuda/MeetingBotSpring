package qiwi.zoombot.spring.zoomApi

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant

class ZoomMeeting(
    val topic: String?,
    val agenda: String?,
    val startTime: Instant,
    val duration: Duration,
    val joinUrl: String,
    var attendees: List<String>?,
)