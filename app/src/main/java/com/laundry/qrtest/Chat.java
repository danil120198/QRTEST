package com.laundry.qrtest;
public class Chat {
    private String messageId;
    private String emergencyCallId;
    private String senderId;
    private String receiverId;
    private String messageType;
    private String content;
    private String filePath;
    private String latitude;
    private String longitude;
    private String sentAt;

    public Chat(String messageId, String emergencyCallId, String senderId, String receiverId,
                String messageType, String content, String filePath, String latitude, String longitude, String sentAt) {
        this.messageId = messageId;
        this.emergencyCallId = emergencyCallId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.content = content;
        this.filePath = filePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sentAt = sentAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getEmergencyCallId() {
        return emergencyCallId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getSentAt() {
        return sentAt;
    }
}

