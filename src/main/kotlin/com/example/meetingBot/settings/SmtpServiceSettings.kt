package com.example.meetingBot.settings

import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.util.*

@Component
class SmtpServiceSettings(
    val host: String, val port: String,
    val login: String, val password: String,
) {
        companion object{
            fun create(settingsFile: File): SmtpServiceSettings {
                val prop = Properties()
                FileInputStream(settingsFile).use { prop.load(it) }

                val host = prop.getProperty("smtp.host")
                val port = prop.getProperty("smtp.port")
                val login = prop.getProperty("smtp.login")
                val password = prop.getProperty("smtp.password")

                val smtpServiceSettings = SmtpServiceSettings(host, port, login, password)

                return smtpServiceSettings
            }
        }
}