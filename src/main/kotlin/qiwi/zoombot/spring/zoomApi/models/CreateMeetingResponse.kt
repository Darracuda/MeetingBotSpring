package qiwi.zoombot.spring.zoomApi.models

import java.time.Instant

data class CreateMeetingResponse (
    var hostid: String,
    var id: String,
    var uuid: String,
    var starttime: Instant,
    var createdat: Instant,
    var starturl: String,
    var joinurl: String,
    var encrypted_password: String,
    var pstnpassword: String,
    var hostemail: String,
)

