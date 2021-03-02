package com.example.meetingBot.settings

import org.springframework.stereotype.Component
import java.io.File

@Component
class SettingsManager(
    val imap : ImapServiceSettings,
    val message: MeetingMailMessageSettings,
    val smtp: SmtpServiceSettings,
    val zoomMeeting: ZoomMeetingSettings,
    val zoom: ZoomServiceSettings,
) {
    companion object{
        fun create(settingsFile: File): SettingsManager {
            val imapServiceSettings = ImapServiceSettings.create(settingsFile)
            val meetingMailMessageSettings = MeetingMailMessageSettings.create(settingsFile)
            val smtpServiceSettings = SmtpServiceSettings.create(settingsFile)
            val zoomMeetingSettings = ZoomMeetingSettings.create(settingsFile)
            val zoomServiceSettings = ZoomServiceSettings.create(settingsFile)

            val settingsManager = SettingsManager(
                 imapServiceSettings, meetingMailMessageSettings, smtpServiceSettings, zoomMeetingSettings, zoomServiceSettings
            )
            return settingsManager
        }
    }
}