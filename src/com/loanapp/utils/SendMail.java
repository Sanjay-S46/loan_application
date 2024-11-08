package com.loanapp.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

    // private String from = System.getenv("SENDER_EMAIL");
    // private String password = System.getenv("PASSWORD");
    
    private String from = "210701231@rajalakshmi.edu.in";
    private String password = "s@nj@y2003";

    public void sendMailThread(String to, String subject, String message) {
        Runnable mailTask = () -> {
            sendMail(to, subject, message);
        };
        Thread mailThread = new Thread(mailTask);
        mailThread.start();
    }

    
    public boolean sendMail(String to,String subject, String message){

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.socketFactory.port","465");
        prop.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setContent(message, "text/html;");
            Transport.send(msg);
            return true;
        } 
        catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }   
    }
}
