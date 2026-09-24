package se.martinanyberg.tvattstuga_booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.martinanyberg.tvattstuga_booking.model.User;
import se.martinanyberg.tvattstuga_booking.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;


    @Test
    void login_rejectsEmptyEmail() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.login("")
                );

        assertEquals(
                "E-post får inte vara tom",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }


    @Test
    void login_rejectsEmailWithoutAt() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.login("martinatest.se")
                );

        assertEquals(
                "Ange en giltig e-post som slutar på .se, .com eller .nu",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }


    @Test
    void login_rejectsUnsupportedDomain() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.login("martina@test.org")
                );

        assertEquals(
                "Ange en giltig e-post som slutar på .se, .com eller .nu",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }


    @Test
    void login_acceptsValidSeEmail() {

        String email =
                "martina@test.se";

        User user =
                new User(email);

        when(repository.findById(email))
                .thenReturn(Optional.of(user));

        User result =
                service.login(email);

        assertEquals(
                email,
                result.getEmail()
        );

        verify(repository)
                .findById(email);
    }


    @Test
    void login_acceptsValidComEmail() {

        String email =
                "martina@gmail.com";

        User user =
                new User(email);

        when(repository.findById(email))
                .thenReturn(Optional.of(user));

        User result =
                service.login(email);

        assertEquals(
                email,
                result.getEmail()
        );
    }


    @Test
    void login_acceptsValidNuEmail() {

        String email =
                "martina@test.nu";

        User user =
                new User(email);

        when(repository.findById(email))
                .thenReturn(Optional.of(user));

        User result =
                service.login(email);

        assertEquals(
                email,
                result.getEmail()
        );
    }


    @Test
    void login_createsUserWhenUserDoesNotExist() {

        String email =
                "newuser@test.se";

        when(repository.findById(email))
                .thenReturn(Optional.empty());

        when(repository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        User result =
                service.login(email);

        assertEquals(
                email,
                result.getEmail()
        );

        verify(repository)
                .save(any(User.class));
    }


    @Test
    void login_normalizesEmail() {

        String input =
                "  Martina@Test.SE  ";

        String normalized =
                "martina@test.se";

        User user =
                new User(normalized);

        when(repository.findById(normalized))
                .thenReturn(Optional.of(user));

        User result =
                service.login(input);

        assertEquals(
                normalized,
                result.getEmail()
        );

        verify(repository)
                .findById(normalized);
    }
}