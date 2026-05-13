package fr.utc.sr03.services;

import fr.utc.sr03.model.User;
import fr.utc.sr03.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Resource
    private UserRepository userRepository;

    // region CREATE or UPDATE
    public void createUser(User user) {
        userRepository.save(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(int id, Map<String, Object> updates) {
        User user = getUserById(id);

        if (user == null) {
            return null;
        }

        if (updates.containsKey("firstname")) {
            user.setFirstname((String) updates.get("firstname"));
        }

        if (updates.containsKey("lastname")) {
            user.setLastname((String) updates.get("lastname"));
        }

        if (updates.containsKey("mail")) {
            user.setMail((String) updates.get("mail"));
        }

        if (updates.containsKey("password")) {
            user.setPassword((String) updates.get("password"));
        }

        if (updates.containsKey("activated")) {
            user.setActivated((Boolean) updates.get("activated"));
        }

        if (updates.containsKey("admin")) {
            user.setAdmin((Boolean) updates.get("admin"));
        }

        return userRepository.save(user);
    }

    public User setActivated(int id, boolean activated) {
        User user = getUserById(id);

        if (user == null) {
            return null;
        }

        user.setActivated(activated);
        return userRepository.save(user);
    }

    public User setAdmin(int id, boolean admin) {
        User user = getUserById(id);

        if (user == null) {
            return null;
        }

        user.setAdmin(admin);
        return userRepository.save(user);
    }

    public User updatePassword(int id, String newPassword) {
        User user = getUserById(id);

        if (user == null) {
            return null;
        }

        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    // endregion

    // region READ
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> getUserByMail(String emailAddress) {
        return userRepository.findByMail(emailAddress);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getActiveUsers() {
        return userRepository.findByActivatedTrue();
    }

    public List<User> getInactiveUsers() {
        return userRepository.findByActivatedFalse();
    }

    public List<User> getAdminUsers() {
        return userRepository.findByAdminTrue();
    }

    public List<User> getRegularUsers() {
        return userRepository.findByAdminFalse();
    }

    public List<User> searchUsers(
            Boolean admin,
            Boolean activated,
            String firstname,
            String lastname,
            String email
    ) {
        //  If the email is present, find by email first
        if (email != null && !email.isBlank()) {
            return userRepository.findByMail(email)
                    .stream() // Iterate over each element
                    .filter(u -> filterAdmin(u, admin)) // Filter to keep relevant element only
                    .filter(u -> filterActivated(u, activated))
                    .toList();
        }

        List<User> baseUsers;

        if (firstname != null && !firstname.isBlank()
                && lastname != null && !lastname.isBlank()) {
            baseUsers = userRepository
                    .findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(firstname, lastname);
        } else if (firstname != null && !firstname.isBlank()) {
            baseUsers = userRepository
                    .findByFirstnameContainingIgnoreCase(firstname);
        } else if (lastname != null && !lastname.isBlank()) {
            baseUsers = userRepository
                    .findByLastnameContainingIgnoreCase(lastname);
        } else {
            baseUsers = userRepository.findAll();
        }

        return baseUsers.stream()
                .filter(u -> filterAdmin(u, admin))
                .filter(u -> filterActivated(u, activated))
                .toList();
    }

    private boolean filterAdmin(User user, Boolean admin) {
        if (admin == null) {
            return true;
        }
        return admin.equals(user.getAdmin());
    }

    private boolean filterActivated(User user, Boolean activated) {
        if (activated == null) {
            return true;
        }
        return activated.equals(user.getActivated());
    }

    // endregion

    // region DELETE
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public boolean deleteUserById(int id) {
        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);
        return true;
    }

    // endregion
}
