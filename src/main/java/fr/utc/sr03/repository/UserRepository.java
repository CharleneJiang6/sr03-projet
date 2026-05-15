package fr.utc.sr03.repository;

import fr.utc.sr03.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByActivatedTrue();

    List<User> findByActivatedFalse();

    Optional<User> findByMail(String mail);

    List<User> findByFirstnameContainingIgnoreCase(String firstname);

    List<User> findByLastnameContainingIgnoreCase(String lastname);

    List<User> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(String firstname, String lastname);

    List<User> findByAdminTrue();
    List<User> findByAdminFalse();

    void deleteById(Integer id);

}