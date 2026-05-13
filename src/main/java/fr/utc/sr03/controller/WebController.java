// Spring MVC controller calling Thymeleaf templates to render Admin interface.

package fr.utc.sr03.controller;


import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.utc.sr03.model.User;
import fr.utc.sr03.services.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/index")
    public String index() {
        return "index";
    }

//    @RequestMapping(value = "/users")
//    public String users(Model model) {
//        model.addAttribute("myusers", userService.getAllUsers());
//        return "users";
//    }


    // Show the user creation form
    @GetMapping(value = "/create-user") // URL path
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "createuser"; // HTML view to render
    }

    // Handle the form submission to create a new user
    @PostMapping(value = "/create-user")
    public String createUser(Model model, @ModelAttribute User user) {
        // Encrypt the password before saving
        String password = user.getPassword();
        String passwordEncrypted = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        System.out.println(passwordEncrypted);
        user.setPassword(passwordEncrypted);

        User createdUser = userService.saveUser(user);
        model.addAttribute("user", createdUser);
        return "usercreated"; // Show the results of creating a new user
    }

    // Show the user login form
    @GetMapping(value = "/user-login") // URL path
    public String loginUser(Model model) {
        model.addAttribute("user", new User());
        return "user-login";
    }

    // Handle user login flow
    @PostMapping(value = "user-login")
    public String loginUser(Model model, @ModelAttribute User user) {
        User existingUser = userService.getUserByMail(user.getMail()).orElse(null);
        System.out.println(existingUser); //TODO: debug only
        if (existingUser != null) {
            String incomingPassword = user.getPassword();
            String storedPasswordHash = existingUser.getPassword();
            BCrypt.Result result = BCrypt.verifyer().verify(incomingPassword.toCharArray(), storedPasswordHash);
            if (result.verified) { // If the input password corresponds to the stored one
                model.addAttribute("user", existingUser);
                return "user-connected"; // Show the results of a successful login
            } else {
                model.addAttribute("error", "Invalid password");
                return "user-login"; // Show the login form again with an error message
            }
        } else {
            model.addAttribute("error", "User not found");
            return "user-login"; // Show the login form again with an error message
        }
    }

    @GetMapping(value = "/users")
    public String users(Model model) {
        System.out.println(userService.getAllUsers().size());
        model.addAttribute("myusers", userService.getAllUsers());
        return "users";
    }


}
