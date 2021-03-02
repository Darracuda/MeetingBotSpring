package com.example.meetingBot.settings

import com.example.meetingBot.zoomApi.models.CreateMeetingRequest
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.util.*

@Component
class ZoomMeetingSettings(
    val hostVideo: Boolean, val participantVideo: Boolean, val cnMeeting: Boolean, val inMeeting: Boolean,
    val joinBeforeHost: Boolean, val muteUponEntry: Boolean, val watermark: Boolean, val usePmi: Boolean,
    val approvalType: CreateMeetingRequest.MeetingSettings.ApprovalType,
    val audio: CreateMeetingRequest.MeetingSettings.Audio,
    val autoRecording: CreateMeetingRequest.MeetingSettings.AutoRecording
) {
    companion object{
        fun create(settingsFile: File): ZoomMeetingSettings {
            val prop = Properties()
            FileInputStream(settingsFile).use { prop.load(it) }

            val hostVideo = prop.getProperty("zoomMeeting.host_video").toBoolean()
            val participantVideo = prop.getProperty("zoomMeeting.participant_video").toBoolean()
            val cnMeeting = prop.getProperty("zoomMeeting.cn_meeting").toBoolean()
            val inMeeting = prop.getProperty("zoomMeeting.in_meeting").toBoolean()
            val joinBeforeHost = prop.getProperty("zoomMeeting.join_before_host").toBoolean()
            val muteUponEntry = prop.getProperty("zoomMeeting.mute_upon_entry").toBoolean()
            val watermark = prop.getProperty("zoomMeeting.watermark").toBoolean()
            val usePmi = prop.getProperty("zoomMeeting.use_pmi").toBoolean()
            val approvalType = CreateMeetingRequest.MeetingSettings.ApprovalType.valueOf(prop.getProperty("zoomMeeting.approval_type"))
            val audio = CreateMeetingRequest.MeetingSettings.Audio.valueOf(prop.getProperty("zoomMeeting.audio"))
            val autoRecording = CreateMeetingRequest.MeetingSettings.AutoRecording.valueOf(prop.getProperty("zoomMeeting.auto_recording"))

            val zoomMeetingSettings = ZoomMeetingSettings(
                hostVideo, participantVideo, cnMeeting,
                inMeeting, joinBeforeHost, muteUponEntry,
                watermark, usePmi, approvalType, audio, autoRecording
            )

            return zoomMeetingSettings
        }
    }
}