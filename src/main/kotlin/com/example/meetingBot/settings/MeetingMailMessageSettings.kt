package com.example.meetingBot.settings

import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.util.*

@Component
class MeetingMailMessageSettings(val from: String) {
    companion object{
        fun create(settingsFile: File): MeetingMailMessageSettings {
            val prop = Properties()
            FileInputStream(settingsFile).use { prop.load(it) }

            val smtpEmailAddressFrom = prop.getProperty("smtp.emailAddressFrom")

            val meetingMailMessageSettings = MeetingMailMessageSettings(smtpEmailAddressFrom)

            return meetingMailMessageSettings
        }
    }
}