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
import ru.qiwi.internal.zoombot.zoomApi.ZoomAccount
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

        val zoomLogin1 = zoomMeetingSettings.login1
        val zoomToken1 = zoomMeetingSettings.token1
        val zoomLogin2 = zoomMeetingSettings.login2
        val zoomToken2 = zoomMeetingSettings.token2
        val zoomAccount1 = ZoomAccount(zoomLogin1, zoomToken1)
        val zoomAccount2 = ZoomAccount(zoomLogin2, zoomToken2)
        val zoomAccounts = listOf(zoomAccount1,zoomAccount2)
        val zoomPassword = zoomMeetingSettings.password

        logger.info("Starting meetings download")
        val icsMeetings = icsMeetingManager.getMeetingsFromMailbox()
        logger.info("Meeting download complete")
        for (icsMeeting in icsMeetings) {
            if(icsMeeting.startTime < Instant.now()){
                mailboxManager.sendRejectMessage(icsMeeting.attendees.toTypedArray())
                break
            }
            val apiInstance = MeetingsApi()
            val meetingSettings = CreateMeetingRequest.MeetingSettings(
                hostvideo = zoomMeetingSettings.hostvideo,
                participantvideo = zoomMeetingSettings.participantvideo,
                cnmeeting = zoomMeetingSettings.cnmeeting,
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
                var meetingCreated = false
                for (zoomAccount in zoomAccounts) {
                    val meetingListResponse = apiInstance.getMeetingList(
                        zoomAccount.token, zoomAccount.login, "upcoming", 30, 1
                    )
                    logger.info(meetingListResponse.toString())
                    val existingMeetings = meetingListResponse.meetings
                    val overlaps = ifOverlaps(existingMeetings, request)
                    if (overlaps) {
                        logger.info("This time is already reserved")
                        continue
                    }
                    val response = apiInstance.createMeeting(zoomAccount.token, zoomAccount.login, request)
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
                    meetingCreated = true
                }
                if(!meetingCreated)
                    mailboxManager.sendRejectMessage(icsMeeting.attendees.toTypedArray())
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