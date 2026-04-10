package fr.utc.sr03.controller;


import fr.utc.sr03.model.User;
import fr.utc.sr03.services.JakartaEmail;
import fr.utc.sr03.services.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ApiController {

    @Resource
    private UserService userService;

    // region [User Actions Endpoints]

    @GetMapping(value = "/getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "/oneUser/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    // TODO: maybe check how to do with DTO/DAO
    @PostMapping(value = "/createUser")
    public void createUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password
    ) {
        User user = new User(
                firstName,
                lastName,
                email,
                password
        );
        userService.createUser(user);
    }

    // endregion


    // region [Channel Actions Endpoints]

    // endregion

    // region [Test Only Endpoints]
    @PostMapping(value = "/createTest")

    public void createTest() {
        User user = new User(
                "Cédric", "Martinet", "cedric.martinet@utc.fr", "1234"
        );
        userService.createUser(user);
    }

    @GetMapping(value = "/testmail")
    public void testmail() {
        //Test envoi Mail
        JakartaEmail jakartaEmail = new JakartaEmail();
        jakartaEmail.sendMail();
    }
    // endregion

}
