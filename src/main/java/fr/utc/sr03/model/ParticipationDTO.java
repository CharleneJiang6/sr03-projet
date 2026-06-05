package fr.utc.sr03.model;

import fr.utc.sr03.model.Participation;
import fr.utc.sr03.model.User;
import fr.utc.sr03.model.Channel;

public class ParticipationDTO {

    private Integer id;

    private Integer userId;
    private String userFirstname;
    private String userLastname;
    private String userMail;

    private Integer channelId;
    private String channelTitle;
    private String channelType;

    public ParticipationDTO() {
    }

    public ParticipationDTO(Integer id,
                            Integer userId,
                            String userFirstname,
                            String userLastname,
                            String userMail,
                            Integer channelId,
                            String channelTitle,
                            String channelType) {
        this.id = id;
        this.userId = userId;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.userMail = userMail;
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.channelType = channelType;
    }

    public static ParticipationDTO fromEntity(Participation participation) {
        User user = participation.getUser();
        Channel channel = participation.getChannel();

        return new ParticipationDTO(
                participation.getId(),
                user != null ? user.getId() : null,
                user != null ? user.getFirstname() : null,
                user != null ? user.getLastname() : null,
                user != null ? user.getMail() : null,
                channel != null ? channel.getId() : null,
                channel != null ? channel.getTitle() : null,
                channel != null && channel.getType() != null ? channel.getType().name() : null
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserFirstname() {
        return userFirstname;
    }

    public void setUserFirstname(String userFirstname) {
        this.userFirstname = userFirstname;
    }

    public String getUserLastname() {
        return userLastname;
    }

    public void setUserLastname(String userLastname) {
        this.userLastname = userLastname;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }
}
