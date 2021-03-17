package ru.qiwi.internal.zoombot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "settings.message")
class MeetingMailMessageSettings {
    lateinit var from: String
}