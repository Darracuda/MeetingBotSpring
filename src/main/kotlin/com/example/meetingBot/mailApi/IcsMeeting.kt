package com.example.meetingBot.mailApi

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant

@Component
class IcsMeeting(
    var subject: String?,
    var description: String?,
    var startTime: Instant,
    var duration: Duration,
    val organizer: String,
    var attendees: List<String>,
)