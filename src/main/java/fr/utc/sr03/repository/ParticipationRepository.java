package fr.utc.sr03.repository;

import fr.utc.sr03.model.User;
import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Integer> {

    List<Participation> findByUser(User user);

    List<Participation> findByChannel(Channel channel);

    Optional<Participation> findByUserAndChannel(User user, Channel channel);

    boolean existsByUserAndChannel(User user, Channel channel);

    void deleteByUserAndChannel(User user, Channel channel);
}
