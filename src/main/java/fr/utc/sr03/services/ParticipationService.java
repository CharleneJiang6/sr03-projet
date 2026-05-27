package fr.utc.sr03.services;

import fr.utc.sr03.repository.ParticipationRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService {

    @Resource
    private ParticipationRepository participationRepository;

    public boolean isUserInChannel(Integer userId, Integer channelId) {
        return participationRepository.existsByUserIdAndChannelId(userId, channelId);
    }
}