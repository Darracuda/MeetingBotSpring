package ru.qiwi.internal.zoombot.utils

import org.threeten.extra.Interval
import ru.qiwi.internal.zoombot.zoomApi.models.CreateMeetingRequest
import ru.qiwi.internal.zoombot.zoomApi.models.ExistingMeeting
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun toLocal(input: Instant): LocalDateTime {
    return LocalDateTime.ofInstant(input, ZoneOffset.UTC)
}

fun ifOverlaps(existingMeetings: Array<ExistingMeeting>, request: CreateMeetingRequest): Boolean {
    var overlaps = false
    for (existingMeeting in existingMeetings) {
        if (overlaps) break
        val newMeetingInterval: Interval =
            Interval.of(request.startTime, Duration.ofMinutes(request.duration))
        val existingMeetingStart = existingMeeting.startTime
        val existingMeetingInterval: Interval =
            Interval.of(existingMeetingStart, existingMeeting.duration)
        overlaps = newMeetingInterval.overlaps(existingMeetingInterval)
    }
    return overlaps
}