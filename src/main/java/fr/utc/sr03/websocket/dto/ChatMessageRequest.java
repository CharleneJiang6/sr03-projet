package fr.utc.sr03.websocket.dto;

// Client -> Server

public class ChatMessageRequest {

    private Integer channelId;
    private Integer senderId;
    private String content;

    public ChatMessageRequest() {}

    public Integer getChannelId() { return channelId;}
    public void setChannelId(Integer channelId) { this.channelId = channelId; }

    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}