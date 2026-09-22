package se.martinanyberg.tvattstugabooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.martinanyberg.tvattstugabooking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}