package qiwi.zoombot.spring.settings

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "settings.imap")
class ImapServiceSettings{
    lateinit var host:String
    lateinit var port:String
    lateinit var login:String
    lateinit var password:String
}