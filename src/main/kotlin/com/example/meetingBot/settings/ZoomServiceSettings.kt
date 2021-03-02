package com.example.meetingBot.settings

import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.util.*

@Component
class ZoomServiceSettings(
    val login: String, val password: String, val token: String,
) {
    companion object{
        fun create(settingsFile: File): ZoomServiceSettings {
            val prop = Properties()
            FileInputStream(settingsFile).use { prop.load(it) }

            val login = prop.getProperty("zoomMeeting.login")
            val password = prop.getProperty("zoomMeeting.password")
            val token = prop.getProperty("zoomMeeting.token")

            val zoomServiceSettings = ZoomServiceSettings(login, password, token)

            return zoomServiceSettings
        }
    }
}