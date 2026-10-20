package com.talentbridge.model;
public final class Message {
    private final String id,senderId,senderName,body,sentAt;
    public Message(String id,String senderId,String senderName,String body,String sentAt){this.id=id;this.senderId=senderId;this.senderName=senderName;this.body=body;this.sentAt=sentAt;}
    public String getId(){return id;} public String getSenderId(){return senderId;} public String getSenderName(){return senderName;}
    public String getBody(){return body;} public String getSentAt(){return sentAt;}
}
