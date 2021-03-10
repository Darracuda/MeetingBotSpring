package qiwi.zoombot.spring.mailApi

import com.sun.mail.smtp.SMTPTransport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import qiwi.zoombot.spring.ZoomApplication
import qiwi.zoombot.spring.settings.ImapServiceSettings
import qiwi.zoombot.spring.settings.MeetingMailMessageSettings
import qiwi.zoombot.spring.settings.SmtpServiceSettings
import qiwi.zoombot.spring.zoomApi.ZoomMeeting
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

    fun sendMessage(zoomMeeting: ZoomMeeting){

        val smtpHost = smtpServiceSettings.host
        val smtpPort = smtpServiceSettings.port
        val smtpLogin = smtpServiceSettings.login
        val smtpPassword = smtpServiceSettings.password
        val emailAddressFrom = meetingMailMessageSettings.from
        val emailAddressesToCC = emptyArray<String>()

        val emailSubject = zoomMeeting.topic
        val emailText = zoomMeeting.agenda
        val emailToAddresses = zoomMeeting.attendees?.toTypedArray()

        val joinUrl = zoomMeeting.joinUrl

        val session = getSmtpSession(smtpHost, smtpPort)
        val msg: Message = MimeMessage(session)

        try {
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
            sb.append("Join Url: $joinUrl")
            msg.setText(sb.toString())
            msg.sentDate = Date()
            val t = session.getTransport("smtp") as SMTPTransport
            t.connect(smtpHost, smtpLogin, smtpPassword)
            t.sendMessage(msg, msg.allRecipients)
            logger.info("Message sent - Response: " + t.lastServerResponse)
            t.close()
        } catch (e: MessagingException) {
            e.printStackTrace()
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