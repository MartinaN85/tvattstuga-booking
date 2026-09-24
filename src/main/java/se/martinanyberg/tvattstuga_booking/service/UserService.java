package se.martinanyberg.tvattstuga_booking.service;

import org.springframework.stereotype.Service;
import se.martinanyberg.tvattstuga_booking.model.User;
import se.martinanyberg.tvattstuga_booking.repository.UserRepository;

import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository repository;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[^@\\s]+@[^@\\s]+\\.(se|com|nu)$",
                    Pattern.CASE_INSENSITIVE
            );

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User login(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "E-post får inte vara tom"
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException(
                    "Ange en giltig e-post som slutar på .se, .com eller .nu"
            );
        }

        return repository.findById(normalizedEmail)
                .orElseGet(() ->
                        repository.save(
                                new User(normalizedEmail)
                        )
                );
    }
}