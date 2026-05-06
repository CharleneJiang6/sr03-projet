package fr.utc.sr03.services;

import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.Participation;
import fr.utc.sr03.model.User;
import fr.utc.sr03.repository.ChannelRepository;
import fr.utc.sr03.repository.ParticipationRepository;
import fr.utc.sr03.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ChannelService {

    @Resource
    private ChannelRepository channelRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ParticipationRepository participationRepository;

    // Region CREATE / UPDATE

    public Channel saveChannel(Channel channel) {
        return channelRepository.save(channel);
    }

    public Channel updateChannel(int channelId, Map<String, Object> updates) {
        Channel channel = getChannelById(channelId);

        if (channel == null) {
            return null;
        }

        if (updates.containsKey("title")) {
            channel.setTitle((String) updates.get("title"));
        }

        if (updates.containsKey("description")) {
            channel.setDescription((String) updates.get("description"));
        }

        if (updates.containsKey("creationDate")) {
            channel.setCreationDate(LocalDateTime.parse((String) updates.get("creationDate")));
        }

        if (updates.containsKey("expirationDate")) {
            channel.setExpirationDate(LocalDateTime.parse((String) updates.get("expirationDate")));
        }

        return channelRepository.save(channel);
    }

    public Channel updateExpirationDate(int channelId, LocalDateTime expirationDate) {
        Channel channel = getChannelById(channelId);

        if (channel == null) {
            return null;
        }

        channel.setExpirationDate(expirationDate);
        return channelRepository.save(channel);
    }
    // endregion

    // Region READ

    public Channel getChannelById(int channelId) {
        return channelRepository.findById(channelId).orElse(null);
    }

    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    public List<Channel> searchByTitle(String title) {
        return channelRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Channel> getActiveChannels() {
        return channelRepository.findByExpirationDateAfter(LocalDateTime.now());
    }

    public List<Channel> getExpiredChannels() {
        return channelRepository.findByExpirationDateBefore(LocalDateTime.now());
    }

    public List<Channel> getChannels(Integer userId, String type, String status) {
        if (status != null && !status.isBlank()) {
            if (status.equalsIgnoreCase("active")) {
                return getActiveChannels();
            }

            if (status.equalsIgnoreCase("expired")) {
                return getExpiredChannels();
            }
        }

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return List.of();
            }

            if (type != null && type.equalsIgnoreCase("created")) {
                return getChannelsCreatedByUser(user);
            }

            if (type != null && type.equalsIgnoreCase("invited")) {
                return getChannelsWhereUserIsParticipant(user);
            }

            return getAllChannelsForUser(user);
        }

        return getAllChannels();
    }

    public List<Channel> getChannelsCreatedByUser(User user) {
        return channelRepository.findByOwner(user);
    }

    public List<Channel> getChannelsWhereUserIsParticipant(User user) {
        return participationRepository.findByUser(user)
                .stream()
                .map(Participation::getChannel)
                .toList();
    }

    public List<Channel> getAllChannelsForUser(User user) {
        List<Channel> createdChannels = getChannelsCreatedByUser(user);
        List<Channel> invitedChannels = getChannelsWhereUserIsParticipant(user);

        return java.util.stream.Stream.concat(
                        createdChannels.stream(),
                        invitedChannels.stream()
                )
                .distinct()
                .toList();
    }
    // endregion

    // Region DELETE

    public boolean deleteChannelById(int channelId) {
        if (!channelRepository.existsById(channelId)) {
            return false;
        }

        channelRepository.deleteById(channelId);
        return true;
    }

    // endregion
}