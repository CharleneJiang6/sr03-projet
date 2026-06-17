package fr.utc.sr03.repository;

import fr.utc.sr03.model.User;
import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Integer> {

    List<Participation> findByUserId(Integer userId);

    List<Participation> findByChannelId(Integer channelId);

    Optional<Participation> findByUserIdAndChannelId(Integer userId, Integer channelId);

    boolean existsByUserIdAndChannelId(Integer userId, Integer channelId);

    List<Participation> findByUser(User user);

    void deleteByChannelId(int channelId);


}
