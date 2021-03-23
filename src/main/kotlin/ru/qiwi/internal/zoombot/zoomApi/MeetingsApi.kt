package ru.qiwi.internal.zoombot.zoomApi

import org.springframework.stereotype.Component
import ru.qiwi.internal.zoombot.zoomApi.infrastructure.*
import ru.qiwi.internal.zoombot.zoomApi.models.CreateMeetingRequest
import ru.qiwi.internal.zoombot.zoomApi.models.CreateMeetingResponse
import ru.qiwi.internal.zoombot.zoomApi.models.GetMeetingListResponse

@Component
class MeetingsApi(basePath: String = "https://api.zoom.us/v2") : ApiClient(basePath) {
    @Suppress("UNCHECKED_CAST")
    fun createMeeting(token: String, userId: String, request: CreateMeetingRequest) : CreateMeetingResponse {
        val acceptsHeaders: Map<String,String> = mapOf("Accept" to "application/json, application/xml")
        val tokenHeaders: Map<String,String> = mapOf("Authorization" to "Bearer $token")
        val headers: MutableMap<String,String> = mutableMapOf()
        headers.putAll(acceptsHeaders)
        headers.putAll(tokenHeaders)

        val config = RequestConfig(
            RequestMethod.POST,
            "/users/${userId}/meetings",
            query = mapOf(),
            headers = headers
        )
        val response = request<CreateMeetingResponse>(
            config,
            request
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as CreateMeetingResponse
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException((response as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((response as ServerError<*>).message ?: "Server error")
        }
    }
    @Suppress("UNCHECKED_CAST")
    fun getMeetingList(token: String, userId: String, type: String, pageSize: Int, pageNumber: Int) : GetMeetingListResponse {
        val body: Any? = null
        val query = mapOf("type" to listOf(type), "page_size" to listOf("$pageSize"), "page_number" to listOf("$pageNumber"))

        val contentHeaders: Map<String, String> = mapOf()
        val tokenHeaders: Map<String,String> = mapOf("Authorization" to "Bearer $token")
        val acceptsHeaders: Map<String, String> = mapOf("Accept" to "application/json, application/xml")
        val headers: MutableMap<String, String> = mutableMapOf()
        headers.putAll(contentHeaders)
        headers.putAll(acceptsHeaders)
        headers.putAll(tokenHeaders)

        val config = RequestConfig(
            RequestMethod.GET,
            "/users/{userId}/meetings".replace("{"+"userId"+"}", userId),
            query = query,
            headers = headers
        )
        val response = request<GetMeetingListResponse>(
            config,
            body
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as GetMeetingListResponse
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException((response as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((response as ServerError<*>).message ?: "Server error")
        }
    }
}