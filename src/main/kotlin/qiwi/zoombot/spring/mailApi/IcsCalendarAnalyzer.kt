package qiwi.zoombot.spring.mailApi

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.Dur
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.DtEnd
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.util.MapTimeZoneCache
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import qiwi.zoombot.spring.ZoomApplication
import qiwi.zoombot.spring.toLocal
import java.io.StringReader
import java.time.Duration

class IcsCalendarAnalyzer(val icsCalendar: IcsCalendar) {
    fun getMeeting(): IcsMeeting {
        val logger: Logger = LoggerFactory.getLogger(ZoomApplication::class.java)

        logger.info("This message contains .ics file attachment. Started receiving meeting info ...")
        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache::class.java.name)
        val content = String(icsCalendar.content, charset("UTF-8"))
        val reader = StringReader(content)
        val builder = CalendarBuilder()
        val calendar = builder.build(reader)
        val event = calendar.getComponents<VEvent>(Component.VEVENT).single()
        val description = event.description?.value
        val subject = event.summary?.value
        val attendees = event
            .getProperties<Attendee>(Property.ATTENDEE)
            .filter { it.calAddress.scheme.equals("MAILTO", ignoreCase = true) }
            .map { x -> x.calAddress.schemeSpecificPart }
        logger.info("Found ${attendees.size} attendees with MAILTO scheme: $attendees")
        val organizer = event.organizer.calAddress.schemeSpecificPart
        logger.info("Found organizer: $organizer")

        val dur = calculateDuration(event.startDate, event.endDate, event.duration)
        val duration = Duration.ofSeconds(dur.seconds.toLong()) +
                Duration.ofMinutes(dur.minutes.toLong()) +
                Duration.ofHours(dur.hours.toLong())

         val startDateTime = event.startDate.date.toInstant()
        val meeting = IcsMeeting(subject, description, startDateTime, duration, organizer, attendees)
        logger.info("Received meeting info from .ics file complete")
        logger.info("Meeting subject: $subject, description: $description, start time: ${toLocal(startDateTime)}, duration: $organizer, attendees: $attendees")
        return meeting
    }

    private fun calculateDuration(startDate: DtStart?, endDate: DtEnd?, duration: net.fortuna.ical4j.model.property.Duration?): Dur {
        if (duration != null) {
            return duration.duration
        }
        if (startDate == null){
            return Dur(0,0,0,0)
        }
        if (endDate == null) {
            return Dur(0,0,0,0)
        }
        return Dur(startDate.date, endDate.date)
    }
}

