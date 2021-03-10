package qiwi.zoombot.spring.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import qiwi.zoombot.spring.ZoomApplication
import qiwi.zoombot.spring.mailApi.IcsMeetingManager
import qiwi.zoombot.spring.mailApi.MailboxManager
import qiwi.zoombot.spring.settings.ZoomMeetingSettings
import qiwi.zoombot.spring.toLocal
import qiwi.zoombot.spring.zoomApi.MeetingsApi
import qiwi.zoombot.spring.zoomApi.ZoomMeeting
import qiwi.zoombot.spring.zoomApi.infrastructure.ClientException
import qiwi.zoombot.spring.zoomApi.infrastructure.ServerException
import qiwi.zoombot.spring.zoomApi.models.CreateMeetingRequest

@Service
class ScheduledService(
    val zoomMeetingSettings: ZoomMeetingSettings,
    val icsMeetingManager: IcsMeetingManager,
    val mailboxManager: MailboxManager
) {

    @Scheduled(fixedDelay = 15000)
    fun run() {
        val logger: Logger = LoggerFactory.getLogger(ZoomApplication::class.java)
        logger.info("Program started")

        val zoomLogin = zoomMeetingSettings.login
        val zoomPassword = zoomMeetingSettings.password
        val zoomToken = zoomMeetingSettings.token

        logger.info("Starting meetings download")
        val icsMeetings = icsMeetingManager.getMeetingsFromMailbox()
        logger.info("Meeting download complete")

        for (icsMeeting in icsMeetings) {
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
                start_time = icsMeeting.startTime,
                duration = icsMeeting.duration.toMinutes(),
                password = zoomPassword,
                settings = meetingSettings,
            )
            try {
                val response = apiInstance.createMeeting(zoomToken, zoomLogin, request)
                logger.info("Meeting created")
                logger.info("start time: ${toLocal(response.starttime)}")
                logger.info("Response: $response")

                val zoomMeeting = ZoomMeeting(
                    icsMeeting.subject,
                    icsMeeting.description,
                    response.starttime,
                    icsMeeting.duration,
                    response.joinurl,
                    icsMeeting.attendees,
                )

                mailboxManager.sendMessage(zoomMeeting)
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