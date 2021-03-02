package com.example.sendmail

import java.security.Security.addProvider
import java.util.*
import javax.activation.DataHandler
import javax.mail.Message.RecipientType.TO
import javax.mail.PasswordAuthentication
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class GMailSender : javax.mail.Authenticator {
    var mailhost = "smtp.gmail.com"
    var user: String = ""
    var password: String = ""
    var session: javax.mail.Session? = null

    constructor(user: String, password: String) : super() {
        this.user = user
        this.password = password

        var provider = JSSEProvider("HarmonyJSSE", 1.0,
            "Harmony JSSE Provider")
        provider.access()
        addProvider(provider)

        val props = Properties()
        props.setProperty("mail.transport.protocol", "smtp")
        props.setProperty("mail.host", mailhost)
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.port", "465")
        props.put("mail.smtp.socketFactory.port", "465")
        props.put(
            "mail.smtp.socketFactory.class",
            "javax.net.ssl.SSLSocketFactory"
        )
        props.put("mail.smtp.socketFactory.fallback", "false")
        props.setProperty("mail.smtp.quitwait", "false")

        session = javax.mail.Session.getDefaultInstance(props, this)
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(user, password)
    }


    fun sendMail(subject: String, body: String, sender: String, recipients: String) {
        val message = MimeMessage(session)
        val handler = DataHandler(ByteArrayDataSource(body.toByteArray(), "text/html"))
        message.sender = InternetAddress(sender)
        message.subject = subject
        message.dataHandler = handler
        if (recipients.indexOf(',') > 0)
            message.setRecipients(TO, InternetAddress.parse(recipients))
        else
            message.setRecipient(TO, InternetAddress(recipients))

        var thread = Thread(object : Runnable{
            override fun run() {
                try {
                    Transport.send(message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        thread.start()

    }

}

