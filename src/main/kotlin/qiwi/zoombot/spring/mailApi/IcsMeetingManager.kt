package qiwi.zoombot.spring.mailApi

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import qiwi.zoombot.spring.ZoomApplication
import java.lang.Exception
import javax.mail.Flags

@Component
class IcsMeetingManager(private val mailboxManager: MailboxManager) {
    fun getMeetingsFromMailbox(): List<IcsMeeting>{
        val logger: Logger = LoggerFactory.getLogger(ZoomApplication::class.java)
        val messages = mailboxManager.receiveMessages()
        val unreadEmailCount = messages.size
        logger.info("Received $unreadEmailCount new email messages")
        val meetings = mutableListOf<IcsMeeting>()
        for (message in messages) {
            val calendarManager = IcsCalendarManager(message)
            val calendars = calendarManager.getCalendars()
            calendars.forEach{
                val analyzer = IcsCalendarAnalyzer(it)
                try {
                    logger.info("Started calendar analysis in ${message.messageNumber} message...")
                    val meeting = analyzer.getMeeting()
                    meetings.add(meeting)
                } catch (ex: Exception){
                    logger.info("Exception:", ex)
                }
            }
            message.setFlag(Flags.Flag.SEEN, true)
        }
        logger.info("Found ${meetings.size} calendar(s)")
        return meetings
    }
}