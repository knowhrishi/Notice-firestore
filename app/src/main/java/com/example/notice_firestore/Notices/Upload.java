package com.example.notice_firestore.Notices;

public class Upload{

    public String content;
    public String url;
    public String author;
    public String timestamp;
    public String uid;
    public String year;
    public Upload() {
    }

    public Upload(String uid, String content, String url, String author, String timestamp, String year) {
        this.uid = uid;
        this.content = content;
        this.url= url;
        this.author = author;
        this.timestamp = timestamp;
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUrl(String url) {
        this.url = url;
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

    @Override
    public String toString() {
        return author + "\n" + content ;
    }
}