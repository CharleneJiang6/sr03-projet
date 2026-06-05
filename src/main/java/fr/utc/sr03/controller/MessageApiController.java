package fr.utc.sr03.controller;

import fr.utc.sr03.model.Message;
import fr.utc.sr03.services.MessageService;
import fr.utc.sr03.websocket.dto.ChatMessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@CrossOrigin(origins = "http://localhost:5173")
public class MessageApiController {

    private final MessageService messageService;

    public MessageApiController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessagesByChannel(@PathVariable Integer channelId) {
        List<Message> messages = messageService.getMessagesByChannelId(channelId);

        List<ChatMessageResponse> response = messages.stream()
                .map(message -> new ChatMessageResponse(
                        message.getId(),
                        message.getChannel().getId(),
                        message.getSender().getId(),
                        message.getSender().getFirstname() + " " + message.getSender().getLastname(),
                        message.getContent(),
                        message.getCreationDate()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}