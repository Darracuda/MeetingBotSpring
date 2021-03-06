package ru.qiwi.internal.zoombot.zoomApi.models

import com.squareup.moshi.Json
import java.time.Instant

data class CreateMeetingRequest(
    val topic: String?,
    val type: MeetingType,
    @Json(name = "start_time") val startTime: Instant,
    val duration: Long ,
    //val timezone: String?,
    val password: String,
    val agenda: String?,
    //@Json(name = "tracking_fields") val trackingFields: Array<MeetingInfoTrackingFields>?,
    val settings: MeetingSettings,
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
        val hostvideo: Boolean? = null,
        val participantvideo: Boolean? = null,
        val cnmeeting: Boolean? = null,
        val inmeeting: Boolean? = null,
        val joinbeforehost: Boolean? = null,
        val muteuponentry: Boolean? = null,
        val watermark: Boolean? = null,
        val usepmi: Boolean? = null,
        val approvaltype: ApprovalType? = null,
        val registrationtype: RegistrationType? = null,
        val audio: Audio? = null,
        val autorecording: AutoRecording? = null,
        val enforcelogin: Boolean? = null,
        val enforcelogindomains: String? = null,
        val alternativehosts: String? = null,
        val closeregistration: Boolean? = null,
        val waitingroom: Boolean? = null
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