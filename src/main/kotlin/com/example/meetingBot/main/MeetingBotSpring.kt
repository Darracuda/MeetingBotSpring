package com.example.meetingBot.main

import com.example.meetingBot.settings.SettingsManager
import com.example.meetingBot.mailApi.IcsMeetingManager
import com.example.meetingBot.mailApi.MailboxManager
import com.example.meetingBot.zoomApi.MeetingsApi
import com.example.meetingBot.zoomApi.infrastructure.ClientException
import com.example.meetingBot.zoomApi.infrastructure.ServerException
import com.example.meetingBot.zoomApi.models.CreateMeetingRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@SpringBootApplication
class ZoomApplication: CommandLineRunner{
	override fun run(vararg args: String?) {
		val logger: Logger = LoggerFactory.getLogger(ZoomApplication::class.java)
		logger.info("Program started")

		val settingsFile = File("")
		val settings = SettingsManager.create(settingsFile)

		val zoomLogin = settings.zoom.login
		val zoomPassword = settings.zoom.password
		val zoomToken = settings.zoom.token

		while(true) {
			val mailboxManager = MailboxManager(settings)
			val icsMeetingManager = IcsMeetingManager(mailboxManager)
			logger.info("Starting meetings download")
			val icsMeetings = icsMeetingManager.getMeetingsFromMailbox()
			logger.info("Meeting download complete")

			for (icsMeeting in icsMeetings) {
				val apiInstance = MeetingsApi()
				val meetingSettings = CreateMeetingRequest.MeetingSettings(
					host_video = settings.zoomMeeting.hostVideo,
					participant_video = settings.zoomMeeting.participantVideo,
					cn_meeting = settings.zoomMeeting.cnMeeting,
					in_meeting = settings.zoomMeeting.inMeeting,
					join_before_host = settings.zoomMeeting.joinBeforeHost,
					mute_upon_entry = settings.zoomMeeting.muteUponEntry,
					watermark = settings.zoomMeeting.watermark,
					use_pmi = settings.zoomMeeting.usePmi,
					approval_type = settings.zoomMeeting.approvalType,
					audio = settings.zoomMeeting.audio,
					auto_recording = settings.zoomMeeting.autoRecording,
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
					logger.info("start time: ${toLocal(response.start_time)}")
					logger.info("Response: $response")

					val zoomMeeting = ZoomMeeting(
						icsMeeting.subject,
						icsMeeting.description,
						response.start_time,
						icsMeeting.duration,
						response.join_url,
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
			Thread.sleep(30000)
		}
	}
}

fun main(args: Array<String>) {
	runApplication<ZoomApplication>(*args)
}

fun toLocal(input: Instant?): LocalDateTime? {
	return LocalDateTime.ofInstant(input, ZoneOffset.UTC)
}
