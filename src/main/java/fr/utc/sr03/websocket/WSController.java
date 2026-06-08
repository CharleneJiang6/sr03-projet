package fr.utc.sr03.websocket;

import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.Message;
import fr.utc.sr03.model.User;
import fr.utc.sr03.services.ChannelService;
import fr.utc.sr03.services.MessageService;
import fr.utc.sr03.services.ParticipationService;
import fr.utc.sr03.services.UserService;
import fr.utc.sr03.websocket.dto.ChatMessageRequest;
import fr.utc.sr03.websocket.dto.ChatMessageResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class WSController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserService userService;
    private final ChannelService channelService;
    private final ParticipationService participationService;

    public WSController(SimpMessagingTemplate messagingTemplate,
                        MessageService messageService,
                        UserService userService,
                        ChannelService channelService,
                        ParticipationService participationService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.userService = userService;
        this.channelService = channelService;
        this.participationService = participationService;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request) {

        User sender = userService.getUserById(request.getSenderId());
        Channel channel = channelService.getChannelById(request.getChannelId());

        if (sender == null || channel == null || request.getContent() == null || request.getContent().isBlank()) {
            return;
        }

        // Forbid sending message to expired channels
        if (channel.getExpirationDate() != null
                && channel.getExpirationDate().isBefore(LocalDateTime.now())) {
            return;
        }

        if (!participationService.isUserInChannel(sender.getId(), channel.getId())) {
            return;
        }

        Message message = new Message(
                request.getContent(),
                LocalDateTime.now(),
                sender,
                channel
        );

        Message saved = messageService.createMessage(message);
        ChatMessageResponse response = new ChatMessageResponse(
                saved.getId(),
                channel.getId(),
                sender.getId(),
                sender.getFirstname() + " " + sender.getLastname(),
                saved.getContent(),
                saved.getCreationDate()
        );

        messagingTemplate.convertAndSend(
                "/topic/channel/" + channel.getId(),
                response
        );
    }

}
