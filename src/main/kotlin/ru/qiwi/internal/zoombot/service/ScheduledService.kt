package ru.qiwi.internal.zoombot.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.qiwi.internal.zoombot.ZoomApplication
import ru.qiwi.internal.zoombot.config.ZoomMeetingSettings
import ru.qiwi.internal.zoombot.mailApi.IcsMeetingManager
import ru.qiwi.internal.zoombot.mailApi.MailboxManager
import ru.qiwi.internal.zoombot.utils.ifOverlaps
import ru.qiwi.internal.zoombot.utils.toLocal
import ru.qiwi.internal.zoombot.zoomApi.MeetingsApi
import ru.qiwi.internal.zoombot.zoomApi.ZoomMeeting
import ru.qiwi.internal.zoombot.zoomApi.infrastructure.ClientException
import ru.qiwi.internal.zoombot.zoomApi.infrastructure.ServerException
import ru.qiwi.internal.zoombot.zoomApi.models.CreateMeetingRequest
import java.time.Instant

@Service
class ScheduledService(
    val zoomMeetingSettings: ZoomMeetingSettings,
    val icsMeetingManager: IcsMeetingManager,
    val mailboxManager: MailboxManager
) {

    @Scheduled(fixedDelay = 15000)
    fun run() {
        val logger: Logger = LoggerFactory.getLogger(ZoomApplication::class.java)
        logger.info("Trying to fetch new meetings...")

        val zoomLogin = zoomMeetingSettings.login
        val zoomPassword = zoomMeetingSettings.password
        val zoomToken = zoomMeetingSettings.token

        logger.info("Starting meetings download")
        val icsMeetings = icsMeetingManager.getMeetingsFromMailbox()
        logger.info("Meeting download complete")
        for (icsMeeting in icsMeetings) {
            if(icsMeeting.startTime < Instant.now()) break
            val apiInstance = MeetingsApi()
            val meetingSettings = CreateMeetingRequest.MeetingSettings(
                hostvideo = zoomMeetingSettings.hostvideo,
                participantvideo = zoomMeetingSettings.participantvideo,
                cnmeeting = zoomMeetingSettings.participantvideo,
                inmeeting = zoomMeetingSettings.inmeeting,
                joinbeforehost = zoomMeetingSettings.joinbeforehost,
                muteuponentry = zoomMeetingSettings.muteuponentry,
                watermark = zoomMeetingSettings.watermark,
                usepmi = zoomMeetingSettings.usePmi,
                approvaltype = zoomMeetingSettings.approvaltype,
                audio = zoomMeetingSettings.audio,
                autorecording = zoomMeetingSettings.autorecording,
            )
            val request = CreateMeetingRequest(
                topic = icsMeeting.subject,
                agenda = icsMeeting.description,
                type = CreateMeetingRequest.MeetingType.ScheduledMeeting,
                startTime = icsMeeting.startTime,
                duration = icsMeeting.duration.toMinutes(),
                password = zoomPassword,
                settings = meetingSettings,
            )
            try {
                val meetingListResponse = apiInstance.getMeetingList(zoomToken, zoomLogin, "upcoming", 30, 1)
                logger.info(meetingListResponse.toString())
                val existingMeetings = meetingListResponse.meetings
                val overlaps = ifOverlaps(existingMeetings, request)
                if(overlaps) {
                    logger.info("This time is already reserved")
                    mailboxManager.sendRejectMessage(icsMeeting.attendees.toTypedArray())
                    break
                }
                val response = apiInstance.createMeeting(zoomToken, zoomLogin, request)
                logger.info("Meeting created")
                logger.info("start time: ${toLocal(response.startTime)}")
                logger.info("Response: $response")

                val zoomMeeting = ZoomMeeting(
                    icsMeeting.subject,
                    icsMeeting.description,
                    response.startTime,
                    icsMeeting.duration,
                    response.joinUrl,
                    icsMeeting.attendees,
                )

                mailboxManager.sendAcceptMessage(zoomMeeting)
            } catch (e: ClientException) {
                logger.error("4xx response calling MeetingsApi#meetingCreate", e)
            } catch (e: ServerException) {
                logger.error("5xx response calling MeetingsApi#meetingCreate", e)
            } catch (e: Exception) {
                logger.error("Exception: ", e)
            }
        }
    }
}