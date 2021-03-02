package com.example.meetingBot.mailApi

import com.sun.mail.util.BASE64DecoderStream
import org.springframework.stereotype.Component
import javax.mail.Message
import javax.mail.Multipart
import javax.mail.internet.MimeBodyPart

@Component
class IcsCalendarManager(private val message: Message) {
    fun getCalendars(): List<IcsCalendar>{
        val contentType: String = message.contentType
        if(!contentType.startsWith("multipart", ignoreCase = true)){
            return emptyList()
        }

        val multiPart = message.content as Multipart

        return (0 until multiPart.count)
            .map{ i -> multiPart.getBodyPart(i) as MimeBodyPart}
            .filter { part -> part.contentType.startsWith("TEXT/CALENDAR", ignoreCase = true) }
            .map{ part ->
                val contentStream = part.content as BASE64DecoderStream
                val content = contentStream.readAllBytes()
                IcsCalendar(content)
            }
    }
}