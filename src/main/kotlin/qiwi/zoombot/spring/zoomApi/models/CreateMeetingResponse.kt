package qiwi.zoombot.spring.zoomApi.models

import java.time.Instant

data class CreateMeetingResponse (
    var host_id: String,
    var id: String,
    var uuid: String,
    var start_time: Instant,
    var created_at: Instant,
    var start_url: String,
    var join_url: String,
    var encrypted_password: String,
    var pstn_password: String,
    var host_email: String,
)

