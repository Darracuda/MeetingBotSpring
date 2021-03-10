package qiwi.zoombot.spring.settings

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import qiwi.zoombot.spring.zoomApi.models.CreateMeetingRequest

@Configuration
@ConfigurationProperties(prefix = "settings.zoommeeting")
class ZoomMeetingSettings{
    lateinit var login: String
    lateinit var password: String
    lateinit var token: String
    var hostvideo: Boolean = false
    var participantvideo: Boolean = false
    var cnmeeting: Boolean = false
    var inmeeting: Boolean = false
    var joinbeforehost: Boolean = false
    var muteuponentry: Boolean = false
    var watermark: Boolean = false
    var usePmi: Boolean = false
    lateinit var approvaltype: CreateMeetingRequest.MeetingSettings.ApprovalType
    lateinit var audio: CreateMeetingRequest.MeetingSettings.Audio
    lateinit var autorecording: CreateMeetingRequest.MeetingSettings.AutoRecording
}