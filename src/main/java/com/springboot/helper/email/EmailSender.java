package com.springboot.helper.email;

import org.springframework.stereotype.Service;
import org.springframework.mail.MailSendException;

@Service
public class EmailSender {
    private final EmailSendable emailSendable;
    public EmailSender(EmailSendable emailSendable){
        this.emailSendable = emailSendable;
    }
    public void sendEmail(String message) throws MailSendException ,InterruptedException{
        emailSendable.send(message);
    }

}
