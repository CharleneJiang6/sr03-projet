package fr.utc.sr03.controller;

import fr.utc.sr03.model.Channel;
import fr.utc.sr03.services.ChannelService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/channels")
public class ChannelApiController {
    @Resource
    private ChannelService channelService;

    @GetMapping
    public List<Channel> getChannels(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        return channelService.getChannels(userId, type, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Channel> getChannelById(@PathVariable int id) {
        Channel channel = channelService.getChannelById(id);
        if (channel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(channel);
    }

    @PostMapping
    public ResponseEntity<Channel> createChannel(@RequestBody Channel channel) {
        Channel createdChannel = channelService.saveChannel(channel);
        return ResponseEntity.ok(createdChannel);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Channel> updateChannel(
            @PathVariable int id,
            @RequestBody Map<String, Object> updates
    ) {
        Channel updatedChannel = channelService.updateChannel(id, updates);
        if (updatedChannel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedChannel);
    }

    @PatchMapping("/{id}/expiration")
    public ResponseEntity<Channel> updateExpirationDate(
            @PathVariable int id,
            @RequestBody Map<String, String> body
    ) {
        LocalDateTime expirationDate = LocalDateTime.parse(body.get("expirationDate"));

        Channel channel = channelService.updateExpirationDate(id, expirationDate);
        if (channel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(channel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable int id) {
        boolean deleted = channelService.deleteChannelById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
