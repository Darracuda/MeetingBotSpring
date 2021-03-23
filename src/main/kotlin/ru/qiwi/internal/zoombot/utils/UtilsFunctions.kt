package ru.qiwi.internal.zoombot.utils

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
    for (existingMeeting in existingMeetings) {
        if (validateTime(existingMeeting, request))
            return true
    }
    return false
}

fun validateTime(existingMeeting: ExistingMeeting, request: CreateMeetingRequest): Boolean{
    val startDate1 = existingMeeting.startTime
    val startDate2 = request.startTime
    val endDate1 = existingMeeting.startTime + existingMeeting.duration
    val endDate2 = request.startTime + Duration.ofMinutes(request.duration)
    return !startDate1.isAfter(endDate2) && !startDate2.isAfter(endDate1)

}

