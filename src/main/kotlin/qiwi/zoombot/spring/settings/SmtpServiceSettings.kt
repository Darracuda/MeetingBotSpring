package qiwi.zoombot.spring.settings

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "settings.smtp")
class SmtpServiceSettings {
    lateinit var host: String
    lateinit var port: String
    lateinit var login: String
    lateinit var password: String
}