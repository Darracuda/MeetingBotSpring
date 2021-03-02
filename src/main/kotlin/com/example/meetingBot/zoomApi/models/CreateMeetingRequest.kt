package com.example.meetingBot.zoomApi.models

import com.squareup.moshi.Json
import org.springframework.stereotype.Component
import java.time.Instant

@Component
data class CreateMeetingRequest(
    val topic: String? = null,
    val type: MeetingType? = null,
    val start_time: Instant? = null,
    val duration: Long? = null,
    val timezone: String? = null,
    val password: String? = null,
    val agenda: String? = null,
    val tracking_fields: Array<MeetingInfoTrackingFields>? = null,
    val settings: MeetingSettings? = null
) {
    enum class MeetingType(val value: Int) {
        @Json(name = "1")
        instantMeeting(1),
        @Json(name = "2")
        ScheduledMeeting(2),
        @Json(name = "3")
        recurringMeetingWithNoFixedTime(3),
        @Json(name = "8")
        recurringMeetingWithFixedTime(8),
    }

    data class MeetingSettings (
        val host_video: Boolean? = null,
        val participant_video: Boolean? = null,
        val cn_meeting: Boolean? = null,
        val in_meeting: Boolean? = null,
        val join_before_host: Boolean? = null,
        val mute_upon_entry: Boolean? = null,
        val watermark: Boolean? = null,
        val use_pmi: Boolean? = null,
        val approval_type: ApprovalType? = null,
        val registration_type: RegistrationType? = null,
        val audio: Audio? = null,
        val auto_recording: AutoRecording? = null,
        val enforce_login: Boolean? = null,
        val enforce_login_domains: String? = null,
        val alternative_hosts: String? = null,
        val close_registration: Boolean? = null,
        val waiting_room: Boolean? = null
    ) {

        enum class Audio(val value: String){
            @Json(name = "both") both("both"),
            @Json(name = "telephony") telephony("telephony"),
            @Json(name = "voip") voip("voip");
        }

        enum class AutoRecording(val value: String){
            @Json(name = "local") local("local"),
            @Json(name = "cloud") cloud("cloud"),
            @Json(name = "none") none("none");
        }

        enum class ApprovalType(val value: Int){
            @Json(name = "1") automaticallyApprove(1),
            @Json(name = "2") manuallyApprove(2),
            @Json(name = "3") noRegistrationRequired(3),
        }

        enum class RegistrationType(val value: Int){
            @Json(name = "0") registerOnceAndAttendAny(0),
            @Json(name = "1") registerEach(1),
            @Json(name = "2") registerOnceAndChoose(2),
        }
    }

    data class MeetingInfoTrackingFields (
            val field: String? = null,
            val value: String? = null
    )
}