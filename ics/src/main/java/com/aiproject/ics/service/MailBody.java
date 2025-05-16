package com.aiproject.ics.service;

import lombok.Builder;

@Builder
public class MailBody {
    private String to;
    private String subject;
    private String text;

    @Override
    public String toString() {
        return "MailBody{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public MailBody(String to, String subject, String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
    }

    public MailBody() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
