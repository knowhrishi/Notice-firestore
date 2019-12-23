package com.example.notice_firestore.Notices;

public class Note {

    public String content;
    public String url;
    public String author;
    public String timestamp;
    public String uid;
    public String year;
    public int priority;

    public Note() {
    }

    public Note(String uid, String content, String url, String author, String timestamp, String year, int priority) {
        this.uid = uid;
        this.content = content;
        this.url= url;
        this.author = author;
        this.timestamp = timestamp;
        this.year = year;
        this.priority = priority;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
