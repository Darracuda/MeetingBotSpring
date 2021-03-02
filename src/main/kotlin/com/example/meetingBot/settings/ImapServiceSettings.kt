package com.example.meetingBot.settings

import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.util.*

@Component
class ImapServiceSettings(
    val host: String, val port: String, val login: String, val password: String,
) {
    companion object{
        fun create(settingsFile: File): ImapServiceSettings {
            val prop = Properties()
            FileInputStream(settingsFile).use { prop.load(it) }

            val host = prop.getProperty("imap.host")
            val port = prop.getProperty("imap.port")
            val login = prop.getProperty("imap.login")
            val password = prop.getProperty("imap.password")

            val imapServiceSettings = ImapServiceSettings(host, port, login, password)

            return imapServiceSettings
        }
    }
}