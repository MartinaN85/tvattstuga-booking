package se.martinanyberg.tvattstuga_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.martinanyberg.tvattstuga_booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByDateAndTimeSlot(
            String date,
            String timeSlot
    );

    long countByUserEmail(
            String userEmail
    );

    List<Booking> findByUserEmail(
            String userEmail
    );
}