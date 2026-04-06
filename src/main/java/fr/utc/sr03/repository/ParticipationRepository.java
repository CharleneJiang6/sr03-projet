package fr.utc.sr03.repository;

import fr.utc.sr03.model.AppUser;
import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Integer> {

    List<Participation> findByUser(AppUser user);

    List<Participation> findByChannel(Channel channel);

    Optional<Participation> findByUserAndChannel(AppUser user, Channel channel);

    boolean existsByUserAndChannel(AppUser user, Channel channel);

    void deleteByUserAndChannel(AppUser user, Channel channel);
}
