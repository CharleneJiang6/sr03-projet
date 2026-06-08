package fr.utc.sr03.controller;

import fr.utc.sr03.model.ApiResponse;
import fr.utc.sr03.model.Channel;
import fr.utc.sr03.services.ChannelService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
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
    public ResponseEntity<?> createChannel(@RequestBody Channel channel) {
        try {
            Channel createdChannel = channelService.saveChannel(channel);
            return ResponseEntity.ok(createdChannel);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(e.getMessage())
            );
        }
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


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable int id) {
        boolean deleted = channelService.deleteChannelById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
