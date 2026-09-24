package se.martinanyberg.tvattstuga_booking.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.martinanyberg.tvattstuga_booking.model.User;
import se.martinanyberg.tvattstuga_booking.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public User login(@Valid @RequestBody User user) {
        return service.login(user.getEmail());
    }
}