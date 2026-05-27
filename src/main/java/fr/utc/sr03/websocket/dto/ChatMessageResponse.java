package fr.utc.sr03.websocket.dto;
import java.time.LocalDateTime;

// Server -> Client
public class ChatMessageResponse {
    private Integer id;
    private Integer channelId;
    private Integer senderId;
    private String senderName;
    private String content;
    private LocalDateTime creationDate;

    public ChatMessageResponse() {
    }

    public ChatMessageResponse(Integer id,
                               Integer channelId,
                               Integer senderId,
                               String senderName,
                               String content,
                               LocalDateTime creationDate)
    {
        this.id = id;
        this.channelId = channelId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.creationDate = creationDate;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getChannelId() { return channelId; }
    public void setChannelId(Integer channelId) { this.channelId = channelId; }

    public Integer getSenderId() { return senderId;}
    public void setSenderId(Integer senderId) { this.senderId = senderId;}

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
}
