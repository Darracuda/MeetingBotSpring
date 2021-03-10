package qiwi.zoombot.spring.settings

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "settings.message")
class MeetingMailMessageSettings {
    lateinit var from: String
}