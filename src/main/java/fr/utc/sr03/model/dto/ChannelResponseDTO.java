package fr.utc.sr03.model.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "title", "description", "type", "creationDate", "expirationDate",
        "ownerId", "membersCount", "pendingInvitationsCount"})
public class ChannelResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private String type; // "GROUP" ou "PRIVATE"
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private Integer ownerId;

    // Useful data for frontend
    private int membersCount;
    private int pendingInvitationsCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public int getPendingInvitationsCount() {
        return pendingInvitationsCount;
    }

    public void setPendingInvitationsCount(int pendingInvitationsCount) {
        this.pendingInvitationsCount = pendingInvitationsCount;
    }
}
