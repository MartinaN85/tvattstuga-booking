package se.martinanyberg.tvattstuga_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.martinanyberg.tvattstuga_booking.model.User;

public interface UserRepository extends JpaRepository<User, String> {
}