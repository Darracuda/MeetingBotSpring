package ru.qiwi.internal.zoombot.mailApi

import com.sun.mail.smtp.SMTPTransport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.qiwi.internal.zoombot.ZoomApplication
import ru.qiwi.internal.zoombot.config.ImapServiceSettings
import ru.qiwi.internal.zoombot.config.MeetingMailMessageSettings
import ru.qiwi.internal.zoombot.config.SmtpServiceSettings
import ru.qiwi.internal.zoombot.zoomApi.ZoomMeeting
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.search.FlagTerm

@Component
class MailboxManager(
    val imapServiceSettings: ImapServiceSettings,
    val smtpServiceSettings: SmtpServiceSettings,
    val meetingMailMessageSettings: MeetingMailMessageSettings
){
    private val logger: Logger = LoggerFactory.getLogger(ZoomApplication::class.java)

    fun createAcceptMessage(session: Session, zoomMeeting: ZoomMeeting): Message{
        val msg: Message = MimeMessage(session)
        val emailAddressFrom = meetingMailMessageSettings.from
        val emailAddressesToCC = emptyArray<String>()

        val emailSubject = zoomMeeting.topic
        val emailText = zoomMeeting.agenda
        val emailToAddresses = zoomMeeting.attendees?.toTypedArray()

        val joinUrl = zoomMeeting.joinUrl

        msg.setFrom(InternetAddress(emailAddressFrom))
        msg.setRecipients(Message.RecipientType.TO, emailToAddresses?.map { a -> InternetAddress(a) }?.toTypedArray())
        msg.setRecipients(
            Message.RecipientType.CC,
            emailAddressesToCC.map { a -> InternetAddress(a) }.toTypedArray()
        )
        if (emailSubject!=null)
            msg.subject = emailSubject
        val sb = StringBuilder()
        if (emailText!=null)
            sb.append("$emailText\n")
        sb.append("Встреча успешно создана \n")
        sb.append("Подключайтесь: $joinUrl \n")
        if(zoomMeeting.topic!=null)
            sb.append("Тема встречи: ${zoomMeeting.topic} \n")
        if(zoomMeeting.agenda!=null)
            sb.append("Описание встречи: ${zoomMeeting.agenda} \n")
        sb.append("Начало встречи: ${zoomMeeting.startTime}")
        msg.setText(sb.toString())
        msg.sentDate = Date()
        return msg
    }

    fun sendAcceptMessage(zoomMeeting: ZoomMeeting){
        val host = smtpServiceSettings.host
        val port = smtpServiceSettings.port
        val login = smtpServiceSettings.login
        val password = smtpServiceSettings.password

        val session = getSmtpSession(host, port)
        val msg = createAcceptMessage(session, zoomMeeting)

        try {
            val t = session.getTransport("smtp") as SMTPTransport
            t.connect(host, login, password)
            t.sendMessage(msg, msg.allRecipients)
            logger.info("Message sent - Response: " + t.lastServerResponse)
            t.close()
        } catch (e: MessagingException) {
            logger.error("Exception:", e)
        }
    }

    fun createRejectMessage(session: Session, emailToAddresses: Array<String>): Message{
        val emailAddressFrom = meetingMailMessageSettings.from
        val emailAddressesToCC = emptyArray<String>()

        val emailSubject = "Невозможно создать встречу"
        val emailText = "К сожалению, это время недоступно. Попробуйте создать встречу на другое время"
        val msg: Message = MimeMessage(session)
        msg.setFrom(InternetAddress(emailAddressFrom))
        msg.setRecipients(Message.RecipientType.TO, emailToAddresses.map { a -> InternetAddress(a) }.toTypedArray())
        msg.setRecipients(
            Message.RecipientType.CC,
            emailAddressesToCC.map { a -> InternetAddress(a) }.toTypedArray()
        )
        msg.subject = emailSubject
        val sb = StringBuilder()
        sb.append("$emailText\n")
        msg.setText(sb.toString())
        msg.sentDate = Date()
        return msg
    }

    fun sendRejectMessage(emailToAddresses: Array<String>){
        val host = smtpServiceSettings.host
        val port = smtpServiceSettings.port
        val login = smtpServiceSettings.login
        val password = smtpServiceSettings.password

        val session = getSmtpSession(host, port)
        val msg = createRejectMessage(session, emailToAddresses)

        try {
            val t = session.getTransport("smtp") as SMTPTransport
            t.connect(host, login, password)
            t.sendMessage(msg, msg.allRecipients)
            logger.info("Message sent - Response: " + t.lastServerResponse)
            t.close()
        } catch (e: MessagingException) {
            logger.error("Exception: ", e)
        }
    }

    fun receiveMessages(): Array<Message> {
        val host = imapServiceSettings.host
        val port = imapServiceSettings.port
        val login = imapServiceSettings.login
        val password = imapServiceSettings.password

        logger.info("Creating mail session...")
        val session = getImapSession(host, port)
        logger.info("Receiving mail store...")
        val store = session.getStore("imap")
        logger.info("Connecting to mail store...")
        store.connect(login, password)
        val inboxFolder = store.getFolder("INBOX")
        logger.info("Opening mail folder...")
        inboxFolder.open(Folder.READ_WRITE)
        logger.info("Mail folder is successfully opened")
        val messages: Array<Message> = inboxFolder.search(
            FlagTerm(Flags(Flags.Flag.SEEN), false)
        )
        //inboxFolder.close(false)
        //store.close()
        return messages
    }

    private fun getSmtpSession(host: String?, port: String?): Session {
        val properties = Properties()
        properties["mail.smtp.host"] = host
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.port"] = port
        properties["mail.smtp.starttls.enable"] = "true"
        return Session.getInstance(properties)
    }

    private fun getImapSession(host: String?, port: String?): Session {
        val properties = Properties()
        // server setting
        properties["mail.imap.host"] = host
        properties["mail.imap.port"] = port
        // SSL setting
        properties["mail.imap.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        properties["mail.imap.socketFactory.fallback"] = "false"
        properties["mail.imap.socketFactory.port"] = port
        properties["mail.mime.base64.ignoreerrors"] = "true"
        properties["mail.imap.partialfetch"] = "true"
        properties["mail.smtp.ssl.enable"] = "true"

        return Session.getInstance(properties)
    }
}