package fr.utc.sr03.services;

import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.Participation;
import fr.utc.sr03.model.User;
import fr.utc.sr03.model.dto.ChannelResponseDTO;
import fr.utc.sr03.model.enums.ChannelType;
import fr.utc.sr03.model.enums.InvitationStatus;
import fr.utc.sr03.repository.ChannelRepository;
import fr.utc.sr03.repository.InvitationRepository;
import fr.utc.sr03.repository.ParticipationRepository;
import fr.utc.sr03.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    @Resource
    private ChannelRepository channelRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ParticipationRepository participationRepository;

    @Resource
    private InvitationRepository invitationRepository;

    public Channel saveChannel(Channel channel) {
        if (channel.getCreationDate() == null || channel.getExpirationDate() == null) {
            throw new IllegalArgumentException("Les dates de création et d'expiration sont requises");
        }

        if (channel.getCreationDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            throw new IllegalArgumentException("La date de création ne peut pas être dans le passé");
        }

        // Expiration must be equal or after creation (not before)
        if (channel.getExpirationDate().isBefore(channel.getCreationDate())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure ou égale à la date de début");
        }

        if (channel.getTitle() == null || channel.getTitle().isBlank()) {
            throw new IllegalArgumentException("Le titre est requis");
        }

        if (channel.getType() == null) {
            throw new IllegalArgumentException("Le type de channel est requis. Valeurs autorisées: GROUP, PRIVATE");
        }

        if (channel.getOwner() == null || channel.getOwner().getId() == null) {
            throw new IllegalArgumentException("Le propriétaire du channel est requis");
        } else if (!userRepository.existsById(channel.getOwner().getId())) {
            throw new IllegalArgumentException("Le propriétaire du channel n'existe pas");
        }

        Channel savedChannel = channelRepository.save(channel);
        User creator = userRepository.findById(channel.getOwner().getId()).get();

        // add the owner as a participant of his own channel
        participationRepository.save(new Participation(creator, savedChannel));
        return savedChannel;
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
            channel.setCreationDate(OffsetDateTime.parse((String) updates.get("creationDate")).toLocalDateTime());
        }

        if (updates.containsKey("expirationDate")) {
            channel.setExpirationDate(OffsetDateTime.parse((String) updates.get("expirationDate")).toLocalDateTime());
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
        return channelRepository.findByExpirationDateAfter(LocalDateTime.now(ZoneOffset.UTC));
    }

    public List<Channel> getExpiredChannels() {
        return channelRepository.findByExpirationDateBefore(LocalDateTime.now(ZoneOffset.UTC));
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
                .collect(Collectors.toMap(
                        Channel::getId,
                        channel -> channel,
                        (existing, duplicate) -> existing
                ))
                .values()
                .stream()
                .toList();
    }

    @Transactional
    public boolean deleteChannelById(int channelId) {
        if (!channelRepository.existsById(channelId)) {
            return false;
        }

        // Remove all participations for this channel before deleting it
        participationRepository.deleteByChannelId(channelId);
        invitationRepository.deleteByChannelId(channelId);
        channelRepository.deleteById(channelId);
        return true;
    }

    public List<ChannelResponseDTO> getChannelDtos(Integer userId, String type, String status) {
        List<Channel> channels = getChannels(userId, type, status);
        return channels.stream()
                .map(channel -> toDto(channel, userId))
                .toList();
    }

    public ChannelResponseDTO toDto(Channel channel, Integer currentUserId) {
        ChannelResponseDTO dto = new ChannelResponseDTO();
        dto.setId(channel.getId());
        dto.setTitle(channel.getTitle());
        dto.setDescription(channel.getDescription());
        dto.setType(channel.getType().name());
        dto.setCreationDate(channel.getCreationDate());
        dto.setExpirationDate(channel.getExpirationDate());
        dto.setOwnerId(channel.getOwner() != null ? channel.getOwner().getId() : null);

        int membersCount = participationRepository.findByChannelId(channel.getId()).size();
        dto.setMembersCount(membersCount);

        // Pending Invitations for this user
        int pendingInvitationsCount = 0;
        if (currentUserId != null) {
            pendingInvitationsCount =
                    invitationRepository.countByChannelIdAndSenderIdAndStatus(
                            channel.getId(), currentUserId, InvitationStatus.PENDING
                    );
        }
        dto.setPendingInvitationsCount(pendingInvitationsCount);

        return dto;
    }
}
