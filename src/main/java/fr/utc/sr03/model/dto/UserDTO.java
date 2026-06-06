package fr.utc.sr03.model.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fr.utc.sr03.model.User;

// This class is used to transfer user data without exposing sensitive information like password
@JsonPropertyOrder({"id", "firstname", "lastname", "mail", "admin", "activated"})
public class UserDTO {
    private final Integer id;
    private final String firstname;
    private final String lastname;
    private final String mail;
    private final Boolean admin;
    private final Boolean activated;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.mail = user.getMail();
        this.admin = user.getAdmin();
        this.activated = user.getActivated();
    }

    public Integer getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getMail() {
        return mail;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public Boolean getActivated() {
        return activated;
    }
}
