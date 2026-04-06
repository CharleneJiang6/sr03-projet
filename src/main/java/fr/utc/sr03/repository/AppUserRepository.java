package fr.utc.sr03.repository;

import fr.utc.sr03.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    List<AppUser> findByActivatedTrue();

    List<AppUser> findByActivatedFalse();

    Optional<AppUser> findByMail(String mail);

    List<AppUser> findByFirstnameContainingIgnoreCase(String firstname);

    List<AppUser> findByLastnameContainingIgnoreCase(String lastname);

    List<AppUser> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(String firstname, String lastname);
}